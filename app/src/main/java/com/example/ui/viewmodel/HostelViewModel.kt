package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.HostelDatabase
import com.example.data.local.InitialData
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.CanteenItemEntity
import com.example.data.model.CanteenOrderEntity
import com.example.data.model.DailyMessMenuEntity
import com.example.data.model.GatePassEntity
import com.example.data.model.MealAttendanceEntity
import com.example.data.model.StudentEntity
import com.example.data.repository.HostelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Random

enum class AppTab(val title: String) {
    ATTENDANCE("Roll Call"),
    GATE_PASS("Gate Pass"),
    CANTEEN("Canteen & Mess"),
    RESIDENTS("Residents")
}

data class CartItem(
    val item: CanteenItemEntity,
    val quantity: Int
)

data class AttendanceSummary(
    val totalStudents: Int,
    val presentCount: Int,
    val lateCount: Int,
    val onLeaveCount: Int,
    val absentCount: Int,
    val unrecordedCount: Int,
    val onActiveGatePassCount: Int
)

class HostelViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HostelRepository

    init {
        val database = HostelDatabase.getDatabase(application, viewModelScope)
        repository = HostelRepository(database.hostelDao())
    }

    // UI Navigation & Mode
    private val _currentTab = MutableStateFlow(AppTab.ATTENDANCE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _isWardenMode = MutableStateFlow(true)
    val isWardenMode: StateFlow<Boolean> = _isWardenMode.asStateFlow()

    private val _activeStudentId = MutableStateFlow<Long?>(1L) // For student view
    val activeStudentId: StateFlow<Long?> = _activeStudentId.asStateFlow()

    // Attendance State
    private val _selectedDate = MutableStateFlow(InitialData.getTodayDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedSession = MutableStateFlow("NIGHT_CURFEW") // "NIGHT_CURFEW" or "MORNING"
    val selectedSession: StateFlow<String> = _selectedSession.asStateFlow()

    private val _selectedBlockFilter = MutableStateFlow("All")
    val selectedBlockFilter: StateFlow<String> = _selectedBlockFilter.asStateFlow()

    private val _attendanceStatusFilter = MutableStateFlow("All") // "All", "Unmarked", "Present", "Late", "Absent", "On Leave"
    val attendanceStatusFilter: StateFlow<String> = _attendanceStatusFilter.asStateFlow()

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Canteen State
    private val _canteenCategory = MutableStateFlow("All")
    val canteenCategory: StateFlow<String> = _canteenCategory.asStateFlow()

    private val _cart = MutableStateFlow<Map<Long, CartItem>>(emptyMap())
    val cart: StateFlow<Map<Long, CartItem>> = _cart.asStateFlow()

    private val _orderStudentId = MutableStateFlow<Long?>(null)
    val orderStudentId: StateFlow<Long?> = _orderStudentId.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<CanteenOrderEntity?>(null)
    val lastPlacedOrder: StateFlow<CanteenOrderEntity?> = _lastPlacedOrder.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    // Data Streams from Repository
    val allStudents: StateFlow<List<StudentEntity>> = repository.allStudents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allGatePasses: StateFlow<List<GatePassEntity>> = repository.allGatePasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGatePasses: StateFlow<List<GatePassEntity>> = repository.activeGatePasses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCanteenItems: StateFlow<List<CanteenItemEntity>> = repository.allCanteenItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<CanteenOrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allMessMenus: StateFlow<List<DailyMessMenuEntity>> = repository.allMessMenus
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic Attendance Flow based on Date & Session
    val currentAttendanceRecords: StateFlow<List<AttendanceRecordEntity>> =
        combine(_selectedDate, _selectedSession) { date, session ->
            Pair(date, session)
        }.flatMapLatest { (date, session) ->
            repository.getAttendanceRecords(date, session)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Meal attendances for today
    val todayMealAttendances: StateFlow<List<MealAttendanceEntity>> = _selectedDate
        .flatMapLatest { date -> repository.getMealAttendances(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Computed Attendance Summary
    val attendanceSummary: StateFlow<AttendanceSummary> = combine(
        allStudents,
        currentAttendanceRecords,
        activeGatePasses
    ) { students, records, activePasses ->
        val recordMap = records.associateBy { it.studentId }
        var present = 0
        var late = 0
        var onLeave = 0
        var absent = 0
        var unmarked = 0

        for (student in students) {
            val rec = recordMap[student.id]
            if (rec == null) {
                unmarked++
            } else {
                when (rec.status) {
                    "PRESENT" -> present++
                    "LATE" -> late++
                    "ON_LEAVE" -> onLeave++
                    "ABSENT" -> absent++
                    else -> unmarked++
                }
            }
        }

        AttendanceSummary(
            totalStudents = students.size,
            presentCount = present,
            lateCount = late,
            onLeaveCount = onLeave,
            absentCount = absent,
            unrecordedCount = unmarked,
            onActiveGatePassCount = activePasses.size
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        AttendanceSummary(0, 0, 0, 0, 0, 0, 0)
    )

    // Actions
    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleWardenMode() {
        _isWardenMode.value = !_isWardenMode.value
    }

    fun setActiveStudentId(id: Long) {
        _activeStudentId.value = id
    }

    fun setDate(date: String) {
        _selectedDate.value = date
    }

    fun setSession(session: String) {
        _selectedSession.value = session
    }

    fun setBlockFilter(block: String) {
        _selectedBlockFilter.value = block
    }

    fun setAttendanceStatusFilter(filter: String) {
        _attendanceStatusFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCanteenCategory(category: String) {
        _canteenCategory.value = category
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun dismissLastOrder() {
        _lastPlacedOrder.value = null
    }

    // Attendance Actions
    fun markAttendance(studentId: Long, status: String, remarks: String = "") {
        viewModelScope.launch {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val currentTime = timeFormat.format(Date())
            val record = AttendanceRecordEntity(
                studentId = studentId,
                date = _selectedDate.value,
                session = _selectedSession.value,
                status = status,
                markedTime = currentTime,
                remarks = remarks
            )
            repository.recordAttendance(record)
        }
    }

    fun markAllUnmarkedPresent() {
        viewModelScope.launch {
            val students = allStudents.value
            val existing = currentAttendanceRecords.value.associateBy { it.studentId }
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val currentTime = timeFormat.format(Date())
            val newRecords = mutableListOf<AttendanceRecordEntity>()

            for (student in students) {
                if (!existing.containsKey(student.id)) {
                    newRecords.add(
                        AttendanceRecordEntity(
                            studentId = student.id,
                            date = _selectedDate.value,
                            session = _selectedSession.value,
                            status = "PRESENT",
                            markedTime = currentTime,
                            remarks = "Bulk marked"
                        )
                    )
                }
            }

            if (newRecords.isNotEmpty()) {
                repository.recordBulkAttendance(newRecords)
                _userMessage.value = "Marked ${newRecords.size} girls as Present"
            } else {
                _userMessage.value = "All residents already marked for this session"
            }
        }
    }

    // Gate Pass Actions
    fun issueGatePass(
        student: StudentEntity,
        destination: String,
        reason: String,
        outDate: String,
        outTime: String,
        returnDate: String,
        returnTime: String,
        parentApproved: Boolean,
        notes: String
    ) {
        viewModelScope.launch {
            val gatePass = GatePassEntity(
                studentId = student.id,
                studentName = student.name,
                roomNo = student.roomNo,
                destination = destination,
                reason = reason,
                outDate = outDate,
                outTime = outTime,
                expectedReturnDate = returnDate,
                expectedReturnTime = returnTime,
                parentApproved = parentApproved,
                wardenNotes = notes,
                status = "ACTIVE_OUT"
            )
            repository.insertGatePass(gatePass)
            _userMessage.value = "Gate Pass issued for ${student.name} (${student.roomNo})"
        }
    }

    fun markGatePassReturned(gatePassId: Long) {
        viewModelScope.launch {
            val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val currentTime = timeFormat.format(Date())
            repository.markGatePassReturned(gatePassId, currentTime)
            _userMessage.value = "Resident checked back IN at $currentTime"
        }
    }

    // Canteen Actions
    fun addToCart(item: CanteenItemEntity) {
        val current = _cart.value.toMutableMap()
        val existing = current[item.id]
        if (existing == null) {
            current[item.id] = CartItem(item, 1)
        } else {
            current[item.id] = existing.copy(quantity = existing.quantity + 1)
        }
        _cart.value = current
    }

    fun removeFromCart(itemId: Long) {
        val current = _cart.value.toMutableMap()
        val existing = current[itemId] ?: return
        if (existing.quantity > 1) {
            current[itemId] = existing.copy(quantity = existing.quantity - 1)
        } else {
            current.remove(itemId)
        }
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun setOrderStudentId(studentId: Long?) {
        _orderStudentId.value = studentId
    }

    fun checkoutOrder(student: StudentEntity, paymentMethod: String) {
        val cartItems = _cart.value.values.toList()
        if (cartItems.isEmpty()) return

        val total = cartItems.sumOf { it.item.price * it.quantity }

        viewModelScope.launch {
            if (paymentMethod == "WALLET" && student.canteenWalletBalance < total) {
                _userMessage.value = "Insufficient wallet balance! (Has ₹${student.canteenWalletBalance}, requires ₹$total)"
                return@launch
            }

            if (paymentMethod == "WALLET") {
                repository.deductWallet(student.id, total)
            }

            val itemsSummary = cartItems.joinToString(", ") { "${it.quantity}x ${it.item.name} (₹${(it.item.price * it.quantity).toInt()})" }
            val tokenNum = 100 + Random().nextInt(900)
            val token = "CAN-$tokenNum"
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())

            val order = CanteenOrderEntity(
                orderToken = token,
                studentId = student.id,
                studentName = student.name,
                roomNo = student.roomNo,
                itemsDescription = itemsSummary,
                totalAmount = total,
                paymentMethod = paymentMethod,
                dateFormatted = sdf.format(Date()),
                status = "SERVED"
            )

            repository.placeOrder(order)
            _lastPlacedOrder.value = order
            _cart.value = emptyMap()
            _userMessage.value = "Order Token $token generated! Total: ₹$total"
        }
    }

    fun topUpStudentWallet(studentId: Long, amount: Double) {
        viewModelScope.launch {
            repository.topUpWallet(studentId, amount)
            _userMessage.value = "Added ₹$amount to student's canteen tab!"
        }
    }

    fun toggleMealAttendance(studentId: Long, mealType: String, currentAttending: Boolean) {
        viewModelScope.launch {
            val record = MealAttendanceEntity(
                studentId = studentId,
                date = _selectedDate.value,
                mealType = mealType,
                isAttending = !currentAttending
            )
            repository.setMealAttendance(record)
        }
    }

    fun addNewStudent(
        name: String,
        rollNo: String,
        roomNo: String,
        block: String,
        floor: Int,
        phone: String,
        guardianName: String,
        guardianPhone: String,
        initialBalance: Double
    ) {
        viewModelScope.launch {
            val colorList = listOf("#E91E63", "#9C27B0", "#3F51B5", "#009688", "#4CAF50", "#FF9800", "#673AB7", "#D81B60")
            val randomColor = colorList.random()
            val student = StudentEntity(
                name = name,
                rollNo = rollNo,
                roomNo = roomNo,
                block = block,
                floor = floor,
                phone = phone,
                guardianName = guardianName,
                guardianPhone = guardianPhone,
                canteenWalletBalance = initialBalance,
                avatarColorHex = randomColor
            )
            repository.insertStudent(student)
            _userMessage.value = "Added resident: $name ($roomNo)"
        }
    }
}
