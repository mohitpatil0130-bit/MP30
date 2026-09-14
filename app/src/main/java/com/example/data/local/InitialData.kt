package com.example.data.local

import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.CanteenItemEntity
import com.example.data.model.CanteenOrderEntity
import com.example.data.model.DailyMessMenuEntity
import com.example.data.model.GatePassEntity
import com.example.data.model.StudentEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object InitialData {

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    val students = listOf(
        StudentEntity(
            id = 1,
            name = "Aarohi Deshmukh",
            rollNo = "GH-2024-001",
            roomNo = "A-101",
            block = "Block A",
            floor = 1,
            phone = "+91 98231 44521",
            guardianName = "Sunil Deshmukh (Father)",
            guardianPhone = "+91 98231 11223",
            canteenWalletBalance = 650.0,
            avatarColorHex = "#E91E63"
        ),
        StudentEntity(
            id = 2,
            name = "Ananya Sharma",
            rollNo = "GH-2024-002",
            roomNo = "A-102",
            block = "Block A",
            floor = 1,
            phone = "+91 97654 88312",
            guardianName = "Meera Sharma (Mother)",
            guardianPhone = "+91 97654 00987",
            canteenWalletBalance = 320.0,
            avatarColorHex = "#9C27B0"
        ),
        StudentEntity(
            id = 3,
            name = "Diya Sengupta",
            rollNo = "GH-2024-003",
            roomNo = "A-103",
            block = "Block A",
            floor = 1,
            phone = "+91 98300 23451",
            guardianName = "Debashis Sengupta (Father)",
            guardianPhone = "+91 98300 99887",
            canteenWalletBalance = 840.0,
            avatarColorHex = "#3F51B5"
        ),
        StudentEntity(
            id = 4,
            name = "Ishita Verma",
            rollNo = "GH-2024-004",
            roomNo = "A-201",
            block = "Block A",
            floor = 2,
            phone = "+91 99112 55432",
            guardianName = "Rajesh Verma (Father)",
            guardianPhone = "+91 99112 88765",
            canteenWalletBalance = 410.0,
            avatarColorHex = "#009688"
        ),
        StudentEntity(
            id = 5,
            name = "Kavya Nair",
            rollNo = "GH-2024-005",
            roomNo = "A-202",
            block = "Block A",
            floor = 2,
            phone = "+91 94470 12389",
            guardianName = "Radha Nair (Mother)",
            guardianPhone = "+91 94470 77654",
            canteenWalletBalance = 520.0,
            avatarColorHex = "#4CAF50"
        ),
        StudentEntity(
            id = 6,
            name = "Meera Iyer",
            rollNo = "GH-2024-006",
            roomNo = "A-203",
            block = "Block A",
            floor = 2,
            phone = "+91 98412 66710",
            guardianName = "Venkatesh Iyer (Father)",
            guardianPhone = "+91 98412 44321",
            canteenWalletBalance = 290.0,
            avatarColorHex = "#FF9800"
        ),
        StudentEntity(
            id = 7,
            name = "Pooja Reddy",
            rollNo = "GH-2024-007",
            roomNo = "B-101",
            block = "Block B",
            floor = 1,
            phone = "+91 98490 33214",
            guardianName = "Sudhakar Reddy (Father)",
            guardianPhone = "+91 98490 88990",
            canteenWalletBalance = 730.0,
            avatarColorHex = "#E64A19"
        ),
        StudentEntity(
            id = 8,
            name = "Priya Patel",
            rollNo = "GH-2024-008",
            roomNo = "B-102",
            block = "Block B",
            floor = 1,
            phone = "+91 98250 88123",
            guardianName = "Hansaben Patel (Mother)",
            guardianPhone = "+91 98250 11445",
            canteenWalletBalance = 480.0,
            avatarColorHex = "#673AB7"
        ),
        StudentEntity(
            id = 9,
            name = "Rhea Kulkarni",
            rollNo = "GH-2024-009",
            roomNo = "B-201",
            block = "Block B",
            floor = 2,
            phone = "+91 98220 54312",
            guardianName = "Anand Kulkarni (Father)",
            guardianPhone = "+91 98220 99881",
            canteenWalletBalance = 610.0,
            avatarColorHex = "#00BCD4"
        ),
        StudentEntity(
            id = 10,
            name = "Sanya Kapoor",
            rollNo = "GH-2024-010",
            roomNo = "B-202",
            block = "Block B",
            floor = 2,
            phone = "+91 98111 77623",
            guardianName = "Vikram Kapoor (Father)",
            guardianPhone = "+91 98111 33221",
            canteenWalletBalance = 150.0,
            avatarColorHex = "#D81B60"
        ),
        StudentEntity(
            id = 11,
            name = "Tanvi Joshi",
            rollNo = "GH-2024-011",
            roomNo = "B-203",
            block = "Block B",
            floor = 2,
            phone = "+91 94220 44556",
            guardianName = "Prashant Joshi (Father)",
            guardianPhone = "+91 94220 11223",
            canteenWalletBalance = 920.0,
            avatarColorHex = "#8E24AA"
        ),
        StudentEntity(
            id = 12,
            name = "Zoya Khan",
            rollNo = "GH-2024-012",
            roomNo = "B-204",
            block = "Block B",
            floor = 2,
            phone = "+91 98901 22334",
            guardianName = "Farooq Khan (Father)",
            guardianPhone = "+91 98901 77889",
            canteenWalletBalance = 560.0,
            avatarColorHex = "#00897B"
        )
    )

    val canteenItems = listOf(
        CanteenItemEntity(
            id = 1,
            name = "Aloo Cheese Paratha",
            category = "Breakfast",
            price = 50.0,
            isVeg = true,
            description = "Served hot with curd and fresh mint chutney",
            available = true,
            prepTimeMinutes = 10
        ),
        CanteenItemEntity(
            id = 2,
            name = "Masala Dosa",
            category = "Breakfast",
            price = 60.0,
            isVeg = true,
            description = "Crispy golden crepe with potato masala, sambar & coconut chutney",
            available = true,
            prepTimeMinutes = 12
        ),
        CanteenItemEntity(
            id = 3,
            name = "Indori Poha with Sev",
            category = "Breakfast",
            price = 35.0,
            isVeg = true,
            description = "Light steamed flattened rice with mustard seeds, crunchy sev & lemon",
            available = true,
            prepTimeMinutes = 5
        ),
        CanteenItemEntity(
            id = 4,
            name = "Veg Cheese Grilled Sandwich",
            category = "Snacks & Bites",
            price = 55.0,
            isVeg = true,
            description = "Loaded with spiced cucumbers, tomatoes, bell peppers and mozzarella",
            available = true,
            prepTimeMinutes = 8
        ),
        CanteenItemEntity(
            id = 5,
            name = "Cheese Butter Maggi",
            category = "Snacks & Bites",
            price = 45.0,
            isVeg = true,
            description = "Hostel special double masala noodles topped with grated cheese",
            available = true,
            prepTimeMinutes = 7
        ),
        CanteenItemEntity(
            id = 6,
            name = "Paneer Tikka Roll",
            category = "Snacks & Bites",
            price = 75.0,
            isVeg = true,
            description = "Smoky grilled cottage cheese wrapped in flaky whole wheat paratha",
            available = true,
            prepTimeMinutes = 10
        ),
        CanteenItemEntity(
            id = 7,
            name = "Crispy Samosa Plate (2 pcs)",
            category = "Snacks & Bites",
            price = 30.0,
            isVeg = true,
            description = "Golden crisp potato & pea samosas with saunth & mint chutneys",
            available = true,
            prepTimeMinutes = 3
        ),
        CanteenItemEntity(
            id = 8,
            name = "Special Deluxe Thali",
            category = "Meals & Thali",
            price = 110.0,
            isVeg = true,
            description = "Paneer Butter Masala, Dal Makhani, 3 Butter Rotis, Jeera Rice, Salad & Gulab Jamun",
            available = true,
            prepTimeMinutes = 15
        ),
        CanteenItemEntity(
            id = 9,
            name = "Rajma Chawal Comfort Bowl",
            category = "Meals & Thali",
            price = 80.0,
            isVeg = true,
            description = "Slow cooked Punjabi red kidney beans curry over fragrant basmati rice",
            available = true,
            prepTimeMinutes = 8
        ),
        CanteenItemEntity(
            id = 10,
            name = "Chole Bhature Platter",
            category = "Meals & Thali",
            price = 85.0,
            isVeg = true,
            description = "2 fluffy bhature with zesty Amritsari chole, pickled onions and green chillies",
            available = true,
            prepTimeMinutes = 12
        ),
        CanteenItemEntity(
            id = 11,
            name = "Ginger Cardamom Chai",
            category = "Beverages",
            price = 15.0,
            isVeg = true,
            description = "Brewed milk tea with freshly crushed adrak and elaichi",
            available = true,
            prepTimeMinutes = 4
        ),
        CanteenItemEntity(
            id = 12,
            name = "Chilled Cold Coffee with Ice Cream",
            category = "Beverages",
            price = 55.0,
            isVeg = true,
            description = "Frothy blended espresso milk shake topped with rich vanilla ice cream",
            available = true,
            prepTimeMinutes = 5
        ),
        CanteenItemEntity(
            id = 13,
            name = "Fresh Seasonal Fruit Juice",
            category = "Beverages",
            price = 45.0,
            isVeg = true,
            description = "100% natural freshly squeezed orange or sweet lime juice without added sugar",
            available = true,
            prepTimeMinutes = 5
        ),
        CanteenItemEntity(
            id = 14,
            name = "Warm Brownie with Chocolate Drizzle",
            category = "Desserts",
            price = 60.0,
            isVeg = true,
            description = "Fudgy Belgian chocolate walnut brownie served warm",
            available = true,
            prepTimeMinutes = 3
        ),
        CanteenItemEntity(
            id = 15,
            name = "Hot Gulab Jamun (2 pcs)",
            category = "Desserts",
            price = 35.0,
            isVeg = true,
            description = "Soft melt-in-mouth milk dumplings soaked in rose saffron syrup",
            available = true,
            prepTimeMinutes = 2
        )
    )

    val messMenus = listOf(
        DailyMessMenuEntity(
            id = 1,
            dayOfWeek = "Monday",
            breakfast = "Idli Sambar, Coconut Chutney, Boiled Eggs / Banana, Tea / Coffee",
            lunch = "Lauki Kofta Curry, Arhar Dal Tadka, Steamed Rice, Roti, Cucumber Raita",
            snacks = "Veg Pakoda with Green Chutney, Adrak Chai",
            dinner = "Paneer Bhurji, Mixed Veg Khichdi, Roti, Kheer",
            specialDietNote = "High protein paneer dinner"
        ),
        DailyMessMenuEntity(
            id = 2,
            dayOfWeek = "Tuesday",
            breakfast = "Aloo Paratha with Curd, Pickle, Seasonal Fruit, Milk / Tea",
            lunch = "Rajma Masala, Jeera Rice, Chapati, Boondi Raita, Fresh Green Salad",
            snacks = "Bhel Puri / Sev Puri, Filter Coffee",
            dinner = "Bhindi Do Pyaza, Yellow Moong Dal, Phulkas, Steamed Rice, Fruit Custard",
            specialDietNote = "Gluten-free rice option available"
        ),
        DailyMessMenuEntity(
            id = 3,
            dayOfWeek = "Wednesday",
            breakfast = "Poha with Roasted Peanuts, Sprouted Moong Salad, Tea / Milk",
            lunch = "Kadhi Pakoda, Steamed Basmati Rice, Aloo Jeera, Roti, Roasted Papad",
            snacks = "Samosa / Bread Roll, Masala Chai",
            dinner = "Matar Paneer, Dal Fry, Jeera Rice, Tawa Butter Roti, Gulab Jamun",
            specialDietNote = "Chef's Special Wednesday sweet"
        ),
        DailyMessMenuEntity(
            id = 4,
            dayOfWeek = "Thursday",
            breakfast = "Upma with Coconut Chutney & Sambhar, Boiled Egg / Apple, Tea / Coffee",
            lunch = "Chole Masala, Bhature / Poori, Steamed Rice, Onion Mint Salad, Sweet Lassi",
            snacks = "Pasta in White Sauce, Sweet Corn, Hot Lemon Tea",
            dinner = "Aloo Gobi Mutter, Dal Tadka, Phulkas, Rice, Seviyan Kheer",
            specialDietNote = "Mild spice option for curry"
        ),
        DailyMessMenuEntity(
            id = 5,
            dayOfWeek = "Friday",
            breakfast = "Methi Thepla with Chhundo & Curd, Banana, Tea / Milk",
            lunch = "Veg Biryani with Mix Veg Raita, Baingan Bharta, Chapati, Papad",
            snacks = "Grilled Veg Sandwich, Filter Coffee / Chai",
            dinner = "Shahi Paneer, Dal Makhani, Garlic Naan / Tawa Roti, Jeera Pulao, Ice Cream",
            specialDietNote = "Friday Grand Feast Night"
        ),
        DailyMessMenuEntity(
            id = 6,
            dayOfWeek = "Saturday",
            breakfast = "Uttapam with Sambar & Tomato Chutney, Seasonal Fruit, Tea / Coffee",
            lunch = "Dal Khichdi with Pure Ghee, Gujarati Kadhi, Aloo Fry, Papad, Curd",
            snacks = "Maggi Noodles / Masala Corn, Tea",
            dinner = "Dum Aloo Kashmiri, Chana Dal, Steamed Rice, Rotis, Moong Dal Halwa",
            specialDietNote = "Comfort weekend light lunch"
        ),
        DailyMessMenuEntity(
            id = 7,
            dayOfWeek = "Sunday",
            breakfast = "Poori Bhaji with Halwa, Sprout Salad, Tea / Coffee",
            lunch = "Special Paneer Butter Masala, Veg Pulao, Dal Fry, Missi Roti, Dahi Vada",
            snacks = "Pani Puri / Chaat Counter, Cold Coffee",
            dinner = "Mushroom Masala / Mixed Veg, Dal Tadka, Phulka, Rice, Rasgulla",
            specialDietNote = "Sunday Special Chaat at 5:00 PM"
        )
    )

    fun getInitialGatePasses(today: String): List<GatePassEntity> = listOf(
        GatePassEntity(
            id = 1,
            studentId = 2,
            studentName = "Ananya Sharma",
            roomNo = "A-102",
            destination = "Central University Library",
            reason = "Late evening semester exam group study",
            outDate = today,
            outTime = "04:30 PM",
            expectedReturnDate = today,
            expectedReturnTime = "08:15 PM",
            actualReturnTime = null,
            status = "ACTIVE_OUT",
            parentApproved = true,
            wardenNotes = "Informed warden Mrs. Sunita via phone"
        ),
        GatePassEntity(
            id = 2,
            studentId = 8,
            studentName = "Priya Patel",
            roomNo = "B-102",
            destination = "Hometown (Ahmedabad)",
            reason = "Cousin's wedding function with family",
            outDate = today,
            outTime = "10:00 AM",
            expectedReturnDate = today,
            expectedReturnTime = "09:00 PM",
            actualReturnTime = null,
            status = "ACTIVE_OUT",
            parentApproved = true,
            wardenNotes = "Parent consent letter on record"
        ),
        GatePassEntity(
            id = 3,
            studentId = 10,
            studentName = "Sanya Kapoor",
            roomNo = "B-202",
            destination = "Apollo Clinic & Pharmacy",
            reason = "Dental checkup appointment",
            outDate = today,
            outTime = "02:00 PM",
            expectedReturnDate = today,
            expectedReturnTime = "05:00 PM",
            actualReturnTime = "04:45 PM",
            status = "RETURNED",
            parentApproved = true,
            wardenNotes = "Returned on time safely"
        )
    )

    fun getInitialAttendance(today: String): List<AttendanceRecordEntity> = listOf(
        AttendanceRecordEntity(id = 1, studentId = 1, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:15 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 2, studentId = 2, date = today, session = "NIGHT_CURFEW", status = "LATE", markedTime = "08:20 PM", remarks = "On Gate Pass till 8:15 PM"),
        AttendanceRecordEntity(id = 3, studentId = 3, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:10 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 4, studentId = 4, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:18 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 5, studentId = 5, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:12 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 6, studentId = 6, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:22 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 7, studentId = 7, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:05 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 8, studentId = 8, date = today, session = "NIGHT_CURFEW", status = "ON_LEAVE", markedTime = "08:00 PM", remarks = "Home Leave approved"),
        AttendanceRecordEntity(id = 9, studentId = 9, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:25 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 10, studentId = 10, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:08 PM", remarks = "Returned from clinic"),
        AttendanceRecordEntity(id = 11, studentId = 11, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:16 PM", remarks = "Room checked"),
        AttendanceRecordEntity(id = 12, studentId = 12, date = today, session = "NIGHT_CURFEW", status = "PRESENT", markedTime = "08:14 PM", remarks = "Room checked")
    )

    fun getInitialOrders(today: String): List<CanteenOrderEntity> = listOf(
        CanteenOrderEntity(
            id = 1,
            orderToken = "CAN-101",
            studentId = 1,
            studentName = "Aarohi Deshmukh",
            roomNo = "A-101",
            itemsDescription = "1x Aloo Cheese Paratha (₹50), 1x Ginger Chai (₹15)",
            totalAmount = 65.0,
            paymentMethod = "WALLET",
            timestamp = System.currentTimeMillis() - 3600000 * 3,
            dateFormatted = "$today 05:30 PM",
            status = "SERVED"
        ),
        CanteenOrderEntity(
            id = 2,
            orderToken = "CAN-102",
            studentId = 4,
            studentName = "Ishita Verma",
            roomNo = "A-201",
            itemsDescription = "1x Cheese Butter Maggi (₹45), 1x Cold Coffee (₹55)",
            totalAmount = 100.0,
            paymentMethod = "WALLET",
            timestamp = System.currentTimeMillis() - 3600000 * 2,
            dateFormatted = "$today 06:15 PM",
            status = "SERVED"
        ),
        CanteenOrderEntity(
            id = 3,
            orderToken = "CAN-103",
            studentId = 7,
            studentName = "Pooja Reddy",
            roomNo = "B-101",
            itemsDescription = "1x Special Deluxe Thali (₹110)",
            totalAmount = 110.0,
            paymentMethod = "UPI",
            timestamp = System.currentTimeMillis() - 3600000,
            dateFormatted = "$today 07:45 PM",
            status = "SERVED"
        )
    )
}
