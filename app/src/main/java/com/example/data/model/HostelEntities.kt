package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val rollNo: String,
    val roomNo: String,
    val block: String, // "Block A", "Block B"
    val floor: Int,
    val phone: String,
    val guardianName: String,
    val guardianPhone: String,
    val canteenWalletBalance: Double = 500.0,
    val avatarColorHex: String = "#E91E63",
    val isActive: Boolean = true
)

@Entity(
    tableName = "attendance_records",
    indices = [Index(value = ["studentId", "date", "session"], unique = true)]
)
data class AttendanceRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val date: String, // "YYYY-MM-DD"
    val session: String, // "NIGHT_CURFEW", "MORNING"
    val status: String, // "PRESENT", "ABSENT", "ON_LEAVE", "LATE"
    val markedTime: String, // e.g. "08:45 PM"
    val remarks: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "gate_passes")
data class GatePassEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val roomNo: String,
    val destination: String,
    val reason: String,
    val outDate: String,
    val outTime: String,
    val expectedReturnDate: String,
    val expectedReturnTime: String,
    val actualReturnTime: String? = null,
    val status: String = "ACTIVE_OUT", // "ACTIVE_OUT", "RETURNED", "OVERDUE", "PENDING_APPROVAL"
    val parentApproved: Boolean = true,
    val wardenNotes: String = "",
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "canteen_items")
data class CanteenItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Breakfast", "Snacks & Bites", "Meals & Thali", "Beverages", "Desserts"
    val price: Double,
    val isVeg: Boolean = true,
    val description: String = "",
    val available: Boolean = true,
    val prepTimeMinutes: Int = 10
)

@Entity(tableName = "canteen_orders")
data class CanteenOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderToken: String, // e.g. "CAN-104"
    val studentId: Long,
    val studentName: String,
    val roomNo: String,
    val itemsDescription: String,
    val totalAmount: Double,
    val paymentMethod: String, // "WALLET", "CASH", "UPI"
    val timestamp: Long = System.currentTimeMillis(),
    val dateFormatted: String,
    val status: String = "SERVED" // "PREPARING", "SERVED", "CANCELLED"
)

@Entity(tableName = "daily_mess_menus")
data class DailyMessMenuEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayOfWeek: String, // "Monday", "Tuesday", etc.
    val breakfast: String,
    val lunch: String,
    val snacks: String,
    val dinner: String,
    val specialDietNote: String = ""
)

@Entity(
    tableName = "meal_attendances",
    indices = [Index(value = ["studentId", "date", "mealType"], unique = true)]
)
data class MealAttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentId: Long,
    val date: String,
    val mealType: String, // "BREAKFAST", "LUNCH", "SNACKS", "DINNER"
    val isAttending: Boolean = true // true: eating in mess, false: skipping
)
