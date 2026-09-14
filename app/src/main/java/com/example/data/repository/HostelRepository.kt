package com.example.data.repository

import com.example.data.local.HostelDao
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.CanteenItemEntity
import com.example.data.model.CanteenOrderEntity
import com.example.data.model.DailyMessMenuEntity
import com.example.data.model.GatePassEntity
import com.example.data.model.MealAttendanceEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.flow.Flow

class HostelRepository(private val dao: HostelDao) {

    // Students
    val allStudents: Flow<List<StudentEntity>> = dao.getAllStudentsFlow()

    suspend fun getStudentById(id: Long): StudentEntity? = dao.getStudentById(id)

    suspend fun insertStudent(student: StudentEntity): Long = dao.insertStudent(student)

    suspend fun updateStudent(student: StudentEntity) = dao.updateStudent(student)

    suspend fun deleteStudent(student: StudentEntity) = dao.deleteStudent(student)

    suspend fun topUpWallet(studentId: Long, amount: Double) = dao.topUpWallet(studentId, amount)

    suspend fun deductWallet(studentId: Long, amount: Double) = dao.deductWallet(studentId, amount)

    // Attendance
    fun getAttendanceRecords(date: String, session: String): Flow<List<AttendanceRecordEntity>> =
        dao.getAttendanceRecordsFlow(date, session)

    fun getStudentAttendanceHistory(studentId: Long): Flow<List<AttendanceRecordEntity>> =
        dao.getStudentAttendanceHistoryFlow(studentId)

    suspend fun recordAttendance(record: AttendanceRecordEntity): Long =
        dao.insertOrUpdateAttendance(record)

    suspend fun recordBulkAttendance(records: List<AttendanceRecordEntity>) =
        dao.insertAttendanceRecords(records)

    // Gate Passes
    val allGatePasses: Flow<List<GatePassEntity>> = dao.getAllGatePassesFlow()

    val activeGatePasses: Flow<List<GatePassEntity>> = dao.getActiveGatePassesFlow()

    suspend fun insertGatePass(gatePass: GatePassEntity): Long = dao.insertGatePass(gatePass)

    suspend fun updateGatePass(gatePass: GatePassEntity) = dao.updateGatePass(gatePass)

    suspend fun markGatePassReturned(gatePassId: Long, returnTime: String) =
        dao.markGatePassReturned(gatePassId, returnTime)

    // Canteen Menu
    val allCanteenItems: Flow<List<CanteenItemEntity>> = dao.getAllCanteenItemsFlow()

    suspend fun updateItemAvailability(itemId: Long, available: Boolean) =
        dao.updateItemAvailability(itemId, available)

    // Canteen Orders
    val allOrders: Flow<List<CanteenOrderEntity>> = dao.getAllOrdersFlow()

    fun getOrdersByStudent(studentId: Long): Flow<List<CanteenOrderEntity>> =
        dao.getOrdersByStudentFlow(studentId)

    suspend fun placeOrder(order: CanteenOrderEntity): Long = dao.insertOrder(order)

    // Mess Menus
    val allMessMenus: Flow<List<DailyMessMenuEntity>> = dao.getAllMessMenusFlow()

    // Meal Attendances (Headcount planning)
    fun getMealAttendances(date: String): Flow<List<MealAttendanceEntity>> =
        dao.getMealAttendancesForDateFlow(date)

    suspend fun setMealAttendance(mealAttendance: MealAttendanceEntity) =
        dao.setMealAttendance(mealAttendance)
}
