package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.CanteenItemEntity
import com.example.data.model.CanteenOrderEntity
import com.example.data.model.DailyMessMenuEntity
import com.example.data.model.GatePassEntity
import com.example.data.model.MealAttendanceEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HostelDao {

    // Students
    @Query("SELECT * FROM students WHERE isActive = 1 ORDER BY roomNo ASC")
    fun getAllStudentsFlow(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentById(studentId: Long): StudentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Update
    suspend fun updateStudent(student: StudentEntity)

    @Delete
    suspend fun deleteStudent(student: StudentEntity)

    @Query("UPDATE students SET canteenWalletBalance = canteenWalletBalance + :amount WHERE id = :studentId")
    suspend fun topUpWallet(studentId: Long, amount: Double)

    @Query("UPDATE students SET canteenWalletBalance = canteenWalletBalance - :amount WHERE id = :studentId")
    suspend fun deductWallet(studentId: Long, amount: Double)

    // Attendance
    @Query("SELECT * FROM attendance_records WHERE date = :date AND session = :session")
    fun getAttendanceRecordsFlow(date: String, session: String): Flow<List<AttendanceRecordEntity>>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId ORDER BY date DESC, session DESC")
    fun getStudentAttendanceHistoryFlow(studentId: Long): Flow<List<AttendanceRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(record: AttendanceRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendanceRecords(records: List<AttendanceRecordEntity>)

    @Query("DELETE FROM attendance_records WHERE date = :date AND session = :session")
    suspend fun clearAttendanceForDateAndSession(date: String, session: String)

    // Gate Passes
    @Query("SELECT * FROM gate_passes ORDER BY createdTimestamp DESC")
    fun getAllGatePassesFlow(): Flow<List<GatePassEntity>>

    @Query("SELECT * FROM gate_passes WHERE status = 'ACTIVE_OUT' ORDER BY createdTimestamp DESC")
    fun getActiveGatePassesFlow(): Flow<List<GatePassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGatePass(gatePass: GatePassEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGatePasses(gatePasses: List<GatePassEntity>)

    @Update
    suspend fun updateGatePass(gatePass: GatePassEntity)

    @Query("UPDATE gate_passes SET status = 'RETURNED', actualReturnTime = :returnTime WHERE id = :gatePassId")
    suspend fun markGatePassReturned(gatePassId: Long, returnTime: String)

    // Canteen Menu Items
    @Query("SELECT * FROM canteen_items ORDER BY category ASC, name ASC")
    fun getAllCanteenItemsFlow(): Flow<List<CanteenItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCanteenItems(items: List<CanteenItemEntity>)

    @Query("UPDATE canteen_items SET available = :available WHERE id = :itemId")
    suspend fun updateItemAvailability(itemId: Long, available: Boolean)

    // Canteen Orders
    @Query("SELECT * FROM canteen_orders ORDER BY timestamp DESC")
    fun getAllOrdersFlow(): Flow<List<CanteenOrderEntity>>

    @Query("SELECT * FROM canteen_orders WHERE studentId = :studentId ORDER BY timestamp DESC")
    fun getOrdersByStudentFlow(studentId: Long): Flow<List<CanteenOrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: CanteenOrderEntity): Long

    // Daily Mess Menus
    @Query("SELECT * FROM daily_mess_menus")
    fun getAllMessMenusFlow(): Flow<List<DailyMessMenuEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessMenus(menus: List<DailyMessMenuEntity>)

    // Meal Attendance (Eating vs Skipping)
    @Query("SELECT * FROM meal_attendances WHERE date = :date")
    fun getMealAttendancesForDateFlow(date: String): Flow<List<MealAttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setMealAttendance(mealAttendance: MealAttendanceEntity)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int
}
