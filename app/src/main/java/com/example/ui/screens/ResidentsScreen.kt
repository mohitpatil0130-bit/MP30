package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentEntity
import com.example.ui.components.QuickCallAction
import com.example.ui.components.StudentAvatar
import com.example.ui.components.dialPhoneNumber
import com.example.ui.theme.HostelPrimary
import com.example.ui.theme.HostelTertiary
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentText
import com.example.ui.viewmodel.HostelViewModel

@Composable
fun ResidentsScreen(
    viewModel: HostelViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allStudents.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val blockFilter by viewModel.selectedBlockFilter.collectAsState()
    val isWardenMode by viewModel.isWardenMode.collectAsState()

    var showAddStudentDialog by remember { mutableStateOf(false) }
    var selectedStudentForDetail by remember { mutableStateOf<StudentEntity?>(null) }
    var showTopUpDialogForStudent by remember { mutableStateOf<StudentEntity?>(null) }

    val filteredStudents = remember(students, searchQuery, blockFilter) {
        students.filter { student ->
            val matchQuery = searchQuery.isBlank() ||
                student.name.contains(searchQuery, ignoreCase = true) ||
                student.roomNo.contains(searchQuery, ignoreCase = true) ||
                student.rollNo.contains(searchQuery, ignoreCase = true)
            val matchBlock = when (blockFilter) {
                "Block A" -> student.block == "Block A"
                "Block B" -> student.block == "Block B"
                "Floor 1" -> student.floor == 1
                "Floor 2" -> student.floor == 2
                else -> true
            }
            matchQuery && matchBlock
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("residents_screen"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Search Input
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("search_residents_input"),
                    placeholder = { Text("Search by name, room (e.g. A-101), or roll no") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Block filter chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val blocks = listOf("All", "Block A", "Block B", "Floor 1", "Floor 2")
                    blocks.forEach { block ->
                        FilterChip(
                            selected = blockFilter == block,
                            onClick = { viewModel.setBlockFilter(block) },
                            label = { Text(block, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // Stats / Total count header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hostelites Directory (${filteredStudents.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap for profile & wallet",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Resident Cards
            items(filteredStudents, key = { it.id }) { student ->
                ResidentDirectoryCard(
                    student = student,
                    onClick = { selectedStudentForDetail = student },
                    onTopUp = { showTopUpDialogForStudent = student }
                )
            }
        }

        // FAB to Add New Resident
        FloatingActionButton(
            onClick = { showAddStudentDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_resident_fab"),
            containerColor = HostelPrimary,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Hostelite", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Detail Dialog
    if (selectedStudentForDetail != null) {
        ResidentDetailDialog(
            student = selectedStudentForDetail!!,
            onDismiss = { selectedStudentForDetail = null },
            onTopUp = {
                val s = selectedStudentForDetail
                selectedStudentForDetail = null
                showTopUpDialogForStudent = s
            }
        )
    }

    // Top up wallet dialog
    if (showTopUpDialogForStudent != null) {
        TopUpWalletDialog(
            student = showTopUpDialogForStudent!!,
            onDismiss = { showTopUpDialogForStudent = null },
            onConfirmTopUp = { amount ->
                viewModel.topUpStudentWallet(showTopUpDialogForStudent!!.id, amount)
                showTopUpDialogForStudent = null
            }
        )
    }

    // Add New Student Dialog
    if (showAddStudentDialog) {
        AddNewStudentDialog(
            onDismiss = { showAddStudentDialog = false },
            onAdd = { name, roll, room, block, floor, phone, gName, gPhone, bal ->
                viewModel.addNewStudent(name, roll, room, block, floor, phone, gName, gPhone, bal)
                showAddStudentDialog = false
            }
        )
    }
}

@Composable
fun ResidentDirectoryCard(
    student: StudentEntity,
    onClick: () -> Unit,
    onTopUp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() }
            .testTag("resident_card_${student.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StudentAvatar(name = student.name, hexColor = student.avatarColorHex, sizeDp = 44)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = student.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = student.roomNo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                    Text(
                        text = "${student.rollNo} • ${student.block} (Floor ${student.floor})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Canteen balance pill
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StatusPresentBg,
                    modifier = Modifier.clickable { onTopUp() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = StatusPresentText,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "₹${student.canteenWalletBalance.toInt()}",
                            color = StatusPresentText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Guardian: ${student.guardianName}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    QuickCallAction(phoneNumber = student.phone, label = "Call ${student.name}")
                    QuickCallAction(phoneNumber = student.guardianPhone, label = "Call Guardian")
                }
            }
        }
    }
}

@Composable
fun ResidentDetailDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onTopUp: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                StudentAvatar(name = student.name, hexColor = student.avatarColorHex, sizeDp = 38)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(student.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Room ${student.roomNo} • ${student.block}", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info block
                DetailItemRow(label = "Student ID", value = student.rollNo)
                DetailItemRow(label = "Floor", value = "${student.floor}")
                DetailItemRow(label = "Phone", value = student.phone, onCall = { dialPhoneNumber(context, student.phone) })
                DetailItemRow(label = "Guardian", value = student.guardianName)
                DetailItemRow(label = "Guardian Contact", value = student.guardianPhone, onCall = { dialPhoneNumber(context, student.guardianPhone) })

                Spacer(modifier = Modifier.height(6.dp))

                // Canteen balance card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Canteen Tab Balance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text("₹${student.canteenWalletBalance.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                        Button(
                            onClick = onTopUp,
                            colors = ButtonDefaults.buttonColors(containerColor = HostelPrimary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("+ Top Up", fontSize = 12.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DetailItemRow(
    label: String,
    value: String,
    onCall: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            if (onCall != null) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onCall, modifier = Modifier.size(24.dp)) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = HostelPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun TopUpWalletDialog(
    student: StudentEntity,
    onDismiss: () -> Unit,
    onConfirmTopUp: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("200") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Top Up Canteen Wallet", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Student: ${student.name} (Room ${student.roomNo})")
                Text("Current Balance: ₹${student.canteenWalletBalance.toInt()}")

                // Quick amount chips
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("100", "200", "500", "1000").forEach { preset ->
                        Surface(
                            onClick = { amountText = preset },
                            shape = RoundedCornerShape(8.dp),
                            color = if (amountText == preset) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "+₹$preset",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Custom Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("top_up_amount_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0) onConfirmTopUp(amt)
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                modifier = Modifier.testTag("confirm_top_up_button")
            ) {
                Text("Add Funds")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddNewStudentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String, String, Int, String, String, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rollNo by remember { mutableStateOf("") }
    var roomNo by remember { mutableStateOf("") }
    var block by remember { mutableStateOf("Block A") }
    var floorText by remember { mutableStateOf("1") }
    var phone by remember { mutableStateOf("") }
    var guardianName by remember { mutableStateOf("") }
    var guardianPhone by remember { mutableStateOf("") }
    var initialBalanceText by remember { mutableStateOf("500") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Hostel Resident", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth().testTag("input_student_name")
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = rollNo,
                            onValueChange = { rollNo = it },
                            label = { Text("Student ID / Roll") },
                            placeholder = { Text("GH-2024-013") },
                            modifier = Modifier.weight(1f).testTag("input_roll_no")
                        )
                        OutlinedTextField(
                            value = roomNo,
                            onValueChange = { roomNo = it },
                            label = { Text("Room No") },
                            placeholder = { Text("A-104") },
                            modifier = Modifier.weight(1f).testTag("input_room_no")
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = block,
                            onValueChange = { block = it },
                            label = { Text("Block") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = floorText,
                            onValueChange = { floorText = it },
                            label = { Text("Floor") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Student Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("input_student_phone")
                    )
                }
                item {
                    OutlinedTextField(
                        value = guardianName,
                        onValueChange = { guardianName = it },
                        label = { Text("Guardian Name & Relation") },
                        placeholder = { Text("e.g. Ramesh Sharma (Father)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_guardian_name")
                    )
                }
                item {
                    OutlinedTextField(
                        value = guardianPhone,
                        onValueChange = { guardianPhone = it },
                        label = { Text("Guardian Phone") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth().testTag("input_guardian_phone")
                    )
                }
                item {
                    OutlinedTextField(
                        value = initialBalanceText,
                        onValueChange = { initialBalanceText = it },
                        label = { Text("Initial Canteen Balance (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val floor = floorText.toIntOrNull() ?: 1
                    val bal = initialBalanceText.toDoubleOrNull() ?: 500.0
                    if (name.isNotBlank() && roomNo.isNotBlank()) {
                        onAdd(name, rollNo, roomNo, block, floor, phone, guardianName, guardianPhone, bal)
                    }
                },
                enabled = name.isNotBlank() && roomNo.isNotBlank(),
                modifier = Modifier.testTag("submit_add_student_button")
            ) {
                Text("Save Resident")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
