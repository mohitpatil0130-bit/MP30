package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.CanteenItemEntity
import com.example.data.model.CanteenOrderEntity
import com.example.data.model.DailyMessMenuEntity
import com.example.data.model.GatePassEntity
import com.example.data.model.MealAttendanceEntity
import com.example.data.model.StudentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        StudentEntity::class,
        AttendanceRecordEntity::class,
        GatePassEntity::class,
        CanteenItemEntity::class,
        CanteenOrderEntity::class,
        DailyMessMenuEntity::class,
        MealAttendanceEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HostelDatabase : RoomDatabase() {

    abstract fun hostelDao(): HostelDao

    companion object {
        @Volatile
        private var INSTANCE: HostelDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): HostelDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HostelDatabase::class.java,
                    "girls_hostel_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(HostelDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class HostelDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.hostelDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        if (database.hostelDao().getStudentCount() == 0) {
                            populateInitialData(database.hostelDao())
                        }
                    }
                }
            }
        }

        private suspend fun populateInitialData(dao: HostelDao) {
            val today = InitialData.getTodayDateString()
            dao.insertStudents(InitialData.students)
            dao.insertCanteenItems(InitialData.canteenItems)
            dao.insertMessMenus(InitialData.messMenus)
            dao.insertGatePasses(InitialData.getInitialGatePasses(today))
            dao.insertAttendanceRecords(InitialData.getInitialAttendance(today))
            for (order in InitialData.getInitialOrders(today)) {
                dao.insertOrder(order)
            }
        }
    }
}
