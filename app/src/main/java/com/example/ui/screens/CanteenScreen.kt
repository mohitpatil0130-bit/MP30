package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CanteenItemEntity
import com.example.data.model.CanteenOrderEntity
import com.example.data.model.DailyMessMenuEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.HostelPrimary
import com.example.ui.theme.HostelTertiary
import com.example.ui.theme.StatusAbsentBg
import com.example.ui.theme.StatusAbsentText
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentText
import com.example.ui.viewmodel.HostelViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CanteenScreen(
    viewModel: HostelViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.allCanteenItems.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val messMenus by viewModel.allMessMenus.collectAsState()
    val cart by viewModel.cart.collectAsState()
    val students by viewModel.allStudents.collectAsState()
    val selectedCategory by viewModel.canteenCategory.collectAsState()
    val isWardenMode by viewModel.isWardenMode.collectAsState()
    val activeStudentId by viewModel.activeStudentId.collectAsState()
    val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsState()

    var activeSubTab by remember { mutableIntStateOf(0) } // 0: Canteen Store, 1: Daily Mess Menu, 2: Orders Log
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val totalCartAmount = remember(cart) {
        cart.values.sumOf { it.item.price * it.quantity }
    }
    val totalCartCount = remember(cart) {
        cart.values.sumOf { it.quantity }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Sub-tabs: Canteen Store, Mess Menu, Orders
            TabRow(
                selectedTabIndex = activeSubTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Tab(
                    selected = activeSubTab == 0,
                    onClick = { activeSubTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Canteen Store")
                        }
                    },
                    modifier = Modifier.testTag("tab_canteen_store")
                )
                Tab(
                    selected = activeSubTab == 1,
                    onClick = { activeSubTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Restaurant, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mess Menu")
                        }
                    },
                    modifier = Modifier.testTag("tab_mess_menu")
                )
                Tab(
                    selected = activeSubTab == 2,
                    onClick = { activeSubTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Orders (${orders.size})")
                        }
                    },
                    modifier = Modifier.testTag("tab_orders")
                )
            }

            when (activeSubTab) {
                0 -> CanteenStoreContent(
                    items = items,
                    cart = cart,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { viewModel.setCanteenCategory(it) },
                    onAddToCart = { viewModel.addToCart(it) },
                    onRemoveFromCart = { viewModel.removeFromCart(it) }
                )
                1 -> MessMenuContent(
                    messMenus = messMenus,
                    students = students,
                    isWardenMode = isWardenMode,
                    activeStudentId = activeStudentId,
                    onToggleMealAttendance = { studentId, meal, attending ->
                        viewModel.toggleMealAttendance(studentId, meal, attending)
                    }
                )
                2 -> CanteenOrdersContent(orders = orders)
            }
        }

        // Floating Cart Summary Bar
        if (activeSubTab == 0 && totalCartCount > 0) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = HostelPrimary,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$totalCartCount Items in Cart",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Total: ₹${totalCartAmount.toInt()}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            onClick = { viewModel.clearCart() },
                            colors = ButtonDefaults.textButtonColors(contentColor = Color.White.copy(alpha = 0.8f))
                        ) {
                            Text("Clear")
                        }
                        Button(
                            onClick = { showCheckoutDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = HostelPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("checkout_cart_button")
                        ) {
                            Text("Checkout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Checkout Dialog
    if (showCheckoutDialog) {
        CanteenCheckoutDialog(
            students = students,
            cart = cart,
            totalAmount = totalCartAmount,
            onDismiss = { showCheckoutDialog = false },
            onConfirmOrder = { student, paymentMethod ->
                viewModel.checkoutOrder(student, paymentMethod)
                showCheckoutDialog = false
            }
        )
    }

    // Order Placed Success Token Dialog
    if (lastPlacedOrder != null) {
        OrderSuccessDialog(
            order = lastPlacedOrder!!,
            onDismiss = { viewModel.dismissLastOrder() }
        )
    }
}

@Composable
fun CanteenStoreContent(
    items: List<CanteenItemEntity>,
    cart: Map<Long, com.example.ui.viewmodel.CartItem>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onAddToCart: (CanteenItemEntity) -> Unit,
    onRemoveFromCart: (Long) -> Unit
) {
    val categories = listOf("All", "Breakfast", "Snacks & Bites", "Meals & Thali", "Beverages", "Desserts")
    val filteredItems = remember(items, selectedCategory) {
        if (selectedCategory == "All") items else items.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("canteen_store_list"),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategorySelected(category) },
                        label = { Text(category, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.testTag("canteen_category_$category")
                    )
                }
            }
        }

        items(filteredItems, key = { it.id }) { item ->
            val cartItem = cart[item.id]
            CanteenItemCard(
                item = item,
                quantityInCart = cartItem?.quantity ?: 0,
                onAdd = { onAddToCart(item) },
                onRemove = { onRemoveFromCart(item.id) }
            )
        }
    }
}

@Composable
fun CanteenItemCard(
    item: CanteenItemEntity,
    quantityInCart: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("canteen_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Veg badge & Category Icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (item.category) {
                    "Breakfast" -> Icons.Default.FreeBreakfast
                    "Snacks & Bites" -> Icons.Default.Fastfood
                    "Meals & Thali" -> Icons.Default.LunchDining
                    "Beverages" -> Icons.Default.FreeBreakfast
                    else -> Icons.Default.Restaurant
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HostelPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Veg Indicator dot
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .border(1.dp, StatusPresentText, RoundedCornerShape(2.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(StatusPresentText)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (item.description.isNotEmpty()) {
                    Text(
                        text = item.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${item.price.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = HostelPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${item.prepTimeMinutes} mins",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Stepper or Add button
            if (quantityInCart == 0) {
                Button(
                    onClick = onAdd,
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("add_item_btn_${item.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ADD +",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Text(
                        text = "$quantityInCart",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )
                    IconButton(onClick = onAdd, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessMenuContent(
    messMenus: List<DailyMessMenuEntity>,
    students: List<StudentEntity>,
    isWardenMode: Boolean,
    activeStudentId: Long?,
    onToggleMealAttendance: (Long, String, Boolean) -> Unit
) {
    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
    val todayName = remember {
        val calendar = Calendar.getInstance()
        SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
    }
    var selectedDay by remember { mutableStateOf(if (daysOfWeek.contains(todayName)) todayName else "Monday") }

    val menuForDay = remember(messMenus, selectedDay) {
        messMenus.firstOrNull { it.dayOfWeek.equals(selectedDay, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("mess_menu_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        // Day selector chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                daysOfWeek.forEach { day ->
                    val isToday = day.equals(todayName, ignoreCase = true)
                    FilterChip(
                        selected = selectedDay == day,
                        onClick = { selectedDay = day },
                        label = {
                            Text(
                                text = if (isToday) "$day (Today)" else day,
                                fontSize = 12.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("mess_day_$day")
                    )
                }
            }
        }

        // Food Wastage Prevention / Meal Headcount Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = HostelTertiary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Kitchen Headcount & Waste Prevention",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            text = "Expected headcount: ${students.size} hostelites. Mark meal skipping if eating outside.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        if (menuForDay != null) {
            item {
                MealTimeCard(
                    title = "Breakfast",
                    timing = "08:00 AM – 09:30 AM",
                    menu = menuForDay.breakfast,
                    icon = Icons.Default.FreeBreakfast
                )
            }
            item {
                MealTimeCard(
                    title = "Lunch",
                    timing = "12:30 PM – 02:30 PM",
                    menu = menuForDay.lunch,
                    icon = Icons.Default.LunchDining
                )
            }
            item {
                MealTimeCard(
                    title = "Evening Snacks & Chai",
                    timing = "05:00 PM – 06:00 PM",
                    menu = menuForDay.snacks,
                    icon = Icons.Default.Fastfood
                )
            }
            item {
                MealTimeCard(
                    title = "Dinner",
                    timing = "08:00 PM – 09:45 PM",
                    menu = menuForDay.dinner,
                    icon = Icons.Default.Nightlife,
                    note = menuForDay.specialDietNote
                )
            }
        } else {
            item {
                Text("No menu loaded for $selectedDay")
            }
        }
    }
}

@Composable
fun MealTimeCard(
    title: String,
    timing: String,
    menu: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    note: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = timing, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline, fontSize = 11.sp)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = StatusPresentBg
                ) {
                    Text(
                        text = "Included in Mess",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusPresentText,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = menu,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            if (note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Chef Note: $note",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = HostelPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CanteenOrdersContent(orders: List<CanteenOrderEntity>) {
    if (orders.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("No orders placed yet", style = MaterialTheme.typography.titleMedium)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("orders_log_list"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = HostelPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = order.orderToken,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = HostelPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${order.studentName} (${order.roomNo})",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                            Text(
                                text = "₹${order.totalAmount.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = HostelPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = order.itemsDescription,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${order.dateFormatted} • Paid via ${order.paymentMethod}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                text = order.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusPresentText
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenCheckoutDialog(
    students: List<StudentEntity>,
    cart: Map<Long, com.example.ui.viewmodel.CartItem>,
    totalAmount: Double,
    onDismiss: () -> Unit,
    onConfirmOrder: (StudentEntity, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var paymentMode by remember { mutableStateOf("WALLET") } // "WALLET" or "CASH"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Checkout Canteen Order", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Select Resident
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedStudent?.let { "${it.name} (${it.roomNo})" } ?: "Select Resident",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Bill to Resident") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("checkout_student_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        students.forEach { student ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("${student.name} • ${student.roomNo}")
                                        Text("₹${student.canteenWalletBalance.toInt()}", color = HostelPrimary, fontWeight = FontWeight.Bold)
                                    }
                                },
                                onClick = {
                                    selectedStudent = student
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Resident Tab Balance
                if (selectedStudent != null) {
                    val balance = selectedStudent!!.canteenWalletBalance
                    val canAfford = balance >= totalAmount
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (canAfford) StatusPresentBg else StatusAbsentBg,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hostel Tab Balance:",
                                fontSize = 12.sp,
                                color = if (canAfford) StatusPresentText else StatusAbsentText
                            )
                            Text(
                                text = "₹${balance.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (canAfford) StatusPresentText else StatusAbsentText
                            )
                        }
                    }
                }

                // Payment Method
                Text(text = "Payment Method:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = paymentMode == "WALLET",
                        onClick = { paymentMode = "WALLET" },
                        modifier = Modifier.testTag("radio_wallet")
                    )
                    Text("Prepaid Tab / Wallet", fontSize = 13.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(
                        selected = paymentMode == "CASH",
                        onClick = { paymentMode = "CASH" },
                        modifier = Modifier.testTag("radio_cash")
                    )
                    Text("Cash / UPI", fontSize = 13.sp)
                }

                // Order summary
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        cart.values.forEach { ci ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${ci.quantity}x ${ci.item.name}", fontSize = 12.sp)
                                Text("₹${(ci.item.price * ci.quantity).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("₹${totalAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = HostelPrimary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedStudent?.let {
                        onConfirmOrder(it, paymentMode)
                    }
                },
                enabled = selectedStudent != null,
                modifier = Modifier.testTag("confirm_order_button")
            ) {
                Text("Confirm & Generate Token")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun OrderSuccessDialog(
    order: CanteenOrderEntity,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = HostelTertiary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Order Token Generated!", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = order.orderToken,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                    )
                }
                Text(
                    text = "Show this token at the Canteen counter",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "${order.studentName} (${order.roomNo})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = order.itemsDescription,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Total Paid: ₹${order.totalAmount.toInt()} (${order.paymentMethod})",
                    fontWeight = FontWeight.Bold,
                    color = HostelPrimary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.testTag("dismiss_order_success_btn")
            ) {
                Text("Done")
            }
        }
    )
}
