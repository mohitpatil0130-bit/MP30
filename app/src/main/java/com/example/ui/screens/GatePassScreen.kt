package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.InitialData
import com.example.data.model.GatePassEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.QuickCallAction
import com.example.ui.theme.HostelPrimary
import com.example.ui.theme.HostelTertiary
import com.example.ui.theme.StatusLateBg
import com.example.ui.theme.StatusLateText
import com.example.ui.theme.StatusPassActiveBg
import com.example.ui.theme.StatusPassActiveText
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentText
import com.example.ui.viewmodel.HostelViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GatePassScreen(
    viewModel: HostelViewModel,
    modifier: Modifier = Modifier
) {
    val allPasses by viewModel.allGatePasses.collectAsState()
    val activePasses by viewModel.activeGatePasses.collectAsState()
    val students by viewModel.allStudents.collectAsState()
    val isWardenMode by viewModel.isWardenMode.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showNewPassDialog by remember { mutableStateOf(false) }

    val returnedPasses = remember(allPasses) {
        allPasses.filter { it.status == "RETURNED" }
    }

    val displayPasses = when (selectedTabIndex) {
        0 -> activePasses
        1 -> returnedPasses
        else -> allPasses
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("gate_pass_screen"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Curfew Safety Banner
            item {
                CurfewSafetyBanner(activeCount = activePasses.size)
            }

            // Tabs: Currently Outside vs Returned Log
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { selectedTabIndex = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Currently Out")
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(StatusPassActiveBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "${activePasses.size}",
                                        color = StatusPassActiveText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        modifier = Modifier.testTag("tab_currently_out")
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { selectedTabIndex = 1 },
                        text = { Text("Returned History (${returnedPasses.size})") },
                        modifier = Modifier.testTag("tab_returned_history")
                    )
                    Tab(
                        selected = selectedTabIndex == 2,
                        onClick = { selectedTabIndex = 2 },
                        text = { Text("All Passes") },
                        modifier = Modifier.testTag("tab_all_passes")
                    )
                }
            }

            // Empty state if no passes
            if (displayPasses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = HostelTertiary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (selectedTabIndex == 0) "All girls are inside the hostel!" else "No pass records found",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (selectedTabIndex == 0) "No active gate passes currently outside" else "Entries will appear when issued",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Gate Pass Cards
            items(displayPasses, key = { it.id }) { pass ->
                val student = students.firstOrNull { it.id == pass.studentId }
                GatePassCard(
                    pass = pass,
                    student = student,
                    isWardenMode = isWardenMode,
                    onMarkReturned = { viewModel.markGatePassReturned(pass.id) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Floating Action Button to issue new pass
        FloatingActionButton(
            onClick = { showNewPassDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("issue_gate_pass_fab"),
            containerColor = HostelPrimary,
            contentColor = Color.White
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Issue Pass", fontWeight = FontWeight.Bold)
            }
        }
    }

    // New Gate Pass Dialog
    if (showNewPassDialog) {
        NewGatePassDialog(
            students = students,
            onDismiss = { showNewPassDialog = false },
            onSubmit = { student, dest, reason, outDate, outTime, retDate, retTime, parentOk, notes ->
                viewModel.issueGatePass(
                    student = student,
                    destination = dest,
                    reason = reason,
                    outDate = outDate,
                    outTime = outTime,
                    returnDate = retDate,
                    returnTime = retTime,
                    parentApproved = parentOk,
                    notes = notes
                )
                showNewPassDialog = false
            }
        )
    }
}

@Composable
fun CurfewSafetyBanner(activeCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (activeCount > 0) StatusLateBg else StatusPresentBg
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (activeCount > 0) StatusLateText else StatusPresentText),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (activeCount > 0) Icons.Default.Security else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (activeCount > 0) "$activeCount Girls Currently on Gate Pass" else "Safe & Sound: All Residents Inside",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (activeCount > 0) StatusLateText else StatusPresentText
                )
                Text(
                    text = "Night Gate Closes at 08:30 PM. Parents notified upon exit & return.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun GatePassCard(
    pass: GatePassEntity,
    student: StudentEntity?,
    isWardenMode: Boolean,
    onMarkReturned: () -> Unit
) {
    val isActive = pass.status == "ACTIVE_OUT"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("pass_card_${pass.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Student Name + Room + Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = pass.studentName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = pass.roomNo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) StatusPassActiveBg else StatusPresentBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isActive) "OUTSIDE" else "RETURNED",
                        color = if (isActive) StatusPassActiveText else StatusPresentText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Destination and Reason
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = HostelPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = pass.destination,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
            if (pass.reason.isNotEmpty()) {
                Text(
                    text = "Reason: ${pass.reason}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 22.dp, top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Timings Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Departed", fontSize = 10.sp, color = MaterialTheme.colorScheme.outline)
                    Text(text = "${pass.outDate} ${pass.outTime}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text(
                        text = if (isActive) "Expected By" else "Returned At",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Text(
                        text = if (isActive) "${pass.expectedReturnDate} ${pass.expectedReturnTime}" else (pass.actualReturnTime ?: pass.expectedReturnTime),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isActive) StatusLateText else StatusPresentText
                    )
                }
            }

            // Parent Permission & Contact Actions
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (pass.parentApproved) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = if (pass.parentApproved) StatusPresentText else StatusLateText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (pass.parentApproved) "Parent Consent Verified" else "Pending Consent",
                        fontSize = 11.sp,
                        color = if (pass.parentApproved) StatusPresentText else StatusLateText
                    )
                }

                if (student != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        QuickCallAction(phoneNumber = student.phone, label = "Call ${student.name}")
                        QuickCallAction(phoneNumber = student.guardianPhone, label = "Call Guardian")
                    }
                }
            }

            // Mark In Button for Active Passes
            if (isActive && isWardenMode) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onMarkReturned,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mark_returned_btn_${pass.id}"),
                    colors = ButtonDefaults.buttonColors(containerColor = HostelTertiary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Check In / Mark Returned")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGatePassDialog(
    students: List<StudentEntity>,
    onDismiss: () -> Unit,
    onSubmit: (StudentEntity, String, String, String, String, String, String, Boolean, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf(students.firstOrNull()) }
    var destination by remember { mutableStateOf("") }
    var reason by remember { mutableStateOf("") }
    val today = remember { InitialData.getTodayDateString() }
    var outDate by remember { mutableStateOf(today) }
    var outTime by remember {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        mutableStateOf(sdf.format(Date()))
    }
    var returnDate by remember { mutableStateOf(today) }
    var returnTime by remember { mutableStateOf("08:00 PM") }
    var parentConsent by remember { mutableStateOf(true) }
    var wardenNotes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Issue Hostel Gate Pass", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Resident Selector
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedStudent?.let { "${it.name} (${it.roomNo})" } ?: "Select Resident",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Hostel Resident") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("select_student_dropdown")
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        students.forEach { student ->
                            DropdownMenuItem(
                                text = { Text("${student.name} • Room ${student.roomNo}") },
                                onClick = {
                                    selectedStudent = student
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = destination,
                    onValueChange = { destination = it },
                    label = { Text("Destination") },
                    placeholder = { Text("e.g., Central Library, Hometown, Apollo Clinic") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_destination")
                )

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text("Purpose / Reason") },
                    placeholder = { Text("e.g., Semester Exam prep, Family event") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_reason")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = outTime,
                        onValueChange = { outTime = it },
                        label = { Text("Out Time") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_out_time")
                    )
                    OutlinedTextField(
                        value = returnTime,
                        onValueChange = { returnTime = it },
                        label = { Text("Return Time") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_return_time")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = parentConsent,
                        onCheckedChange = { parentConsent = it },
                        modifier = Modifier.testTag("checkbox_parent_consent")
                    )
                    Text("Guardian Consent Verified (via phone/letter)", fontSize = 12.sp)
                }

                OutlinedTextField(
                    value = wardenNotes,
                    onValueChange = { wardenNotes = it },
                    label = { Text("Warden Notes (optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedStudent?.let { student ->
                        if (destination.isNotBlank()) {
                            onSubmit(student, destination, reason, outDate, outTime, returnDate, returnTime, parentConsent, wardenNotes)
                        }
                    }
                },
                enabled = selectedStudent != null && destination.isNotBlank(),
                modifier = Modifier.testTag("submit_gate_pass_button")
            ) {
                Text("Issue Pass")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
