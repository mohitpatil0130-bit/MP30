package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceRecordEntity
import com.example.data.model.StudentEntity
import com.example.ui.components.AttendanceStatusBadge
import com.example.ui.components.MetricSummaryCard
import com.example.ui.components.QuickCallAction
import com.example.ui.components.StudentAvatar
import com.example.ui.theme.HostelPrimary
import com.example.ui.theme.HostelTertiary
import com.example.ui.theme.StatusAbsentText
import com.example.ui.theme.StatusLateText
import com.example.ui.theme.StatusLeaveText
import com.example.ui.theme.StatusPresentText
import com.example.ui.viewmodel.AttendanceSummary
import com.example.ui.viewmodel.HostelViewModel

@Composable
fun AttendanceScreen(
    viewModel: HostelViewModel,
    modifier: Modifier = Modifier
) {
    val students by viewModel.allStudents.collectAsState()
    val attendanceRecords by viewModel.currentAttendanceRecords.collectAsState()
    val summary by viewModel.attendanceSummary.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedSession by viewModel.selectedSession.collectAsState()
    val blockFilter by viewModel.selectedBlockFilter.collectAsState()
    val statusFilter by viewModel.attendanceStatusFilter.collectAsState()
    val isWardenMode by viewModel.isWardenMode.collectAsState()
    val activeStudentId by viewModel.activeStudentId.collectAsState()

    var showRemarkDialogForStudent by remember { mutableStateOf<StudentEntity?>(null) }
    var remarkText by remember { mutableStateOf("") }
    var remarkStatus by remember { mutableStateOf("LATE") }

    val recordsByStudent = remember(attendanceRecords) {
        attendanceRecords.associateBy { it.studentId }
    }

    // Filter students
    val filteredStudents = remember(students, blockFilter, statusFilter, recordsByStudent) {
        students.filter { student ->
            val matchBlock = when (blockFilter) {
                "Block A" -> student.block == "Block A"
                "Block B" -> student.block == "Block B"
                "Floor 1" -> student.floor == 1
                "Floor 2" -> student.floor == 2
                else -> true
            }
            val record = recordsByStudent[student.id]
            val matchStatus = when (statusFilter) {
                "Unmarked" -> record == null
                "Present" -> record?.status == "PRESENT"
                "Late" -> record?.status == "LATE"
                "Absent" -> record?.status == "ABSENT"
                "On Leave" -> record?.status == "ON_LEAVE"
                else -> true
            }
            matchBlock && matchStatus
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("attendance_screen"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Curfew & Session Header Banner
        item {
            CurfewHeaderCard(
                selectedSession = selectedSession,
                selectedDate = selectedDate,
                summary = summary,
                onSessionChanged = { viewModel.setSession(it) },
                onMarkAllPresent = { viewModel.markAllUnmarkedPresent() },
                isWardenMode = isWardenMode
            )
        }

        // 2. Summary Metrics Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricSummaryCard(
                    title = "Present",
                    count = "${summary.presentCount}/${summary.totalStudents}",
                    subtitle = "Checked In",
                    accentColor = StatusPresentText,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "Late/Out",
                    count = "${summary.lateCount + summary.onActiveGatePassCount}",
                    subtitle = "${summary.onActiveGatePassCount} on pass",
                    accentColor = StatusLateText,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    title = "Pending",
                    count = "${summary.unrecordedCount}",
                    subtitle = "Unmarked",
                    accentColor = HostelPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Block and Status Filters
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Block Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
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
                            ),
                            modifier = Modifier.testTag("filter_chip_$block")
                        )
                    }
                }

                // Status Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val statuses = listOf("All", "Unmarked", "Present", "Late", "On Leave", "Absent")
                    statuses.forEach { status ->
                        FilterChip(
                            selected = statusFilter == status,
                            onClick = { viewModel.setAttendanceStatusFilter(status) },
                            label = { Text(status, fontSize = 12.sp) }
                        )
                    }
                }
            }
        }

        // 4. Roll Call Section Title
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Resident Roll Call (${filteredStudents.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = if (isWardenMode) "Tap status to mark" else "Hostel View",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 5. Student Attendance Cards
        items(filteredStudents, key = { it.id }) { student ->
            val record = recordsByStudent[student.id]
            StudentAttendanceCard(
                student = student,
                record = record,
                isWardenMode = isWardenMode,
                onMarkStatus = { status ->
                    viewModel.markAttendance(student.id, status)
                },
                onOpenRemarkDialog = { initialStatus ->
                    remarkStatus = initialStatus
                    remarkText = record?.remarks ?: ""
                    showRemarkDialogForStudent = student
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog to add custom remark for Late / Absent / Leave
    if (showRemarkDialogForStudent != null) {
        val student = showRemarkDialogForStudent!!
        AlertDialog(
            onDismissRequest = { showRemarkDialogForStudent = null },
            title = {
                Text("Add Remark: ${student.name} (${student.roomNo})")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Marking status: $remarkStatus",
                        fontWeight = FontWeight.SemiBold,
                        color = when (remarkStatus) {
                            "LATE" -> StatusLateText
                            "ABSENT" -> StatusAbsentText
                            "ON_LEAVE" -> StatusLeaveText
                            else -> StatusPresentText
                        }
                    )
                    OutlinedTextField(
                        value = remarkText,
                        onValueChange = { remarkText = it },
                        label = { Text("Reason / Warden note") },
                        placeholder = { Text("e.g., Delayed in college lab, informed mother") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("remark_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.markAttendance(student.id, remarkStatus, remarkText)
                        showRemarkDialogForStudent = null
                    },
                    modifier = Modifier.testTag("save_remark_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemarkDialogForStudent = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun CurfewHeaderCard(
    selectedSession: String,
    selectedDate: String,
    summary: AttendanceSummary,
    onSessionChanged: (String) -> Unit,
    onMarkAllPresent: () -> Unit,
    isWardenMode: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedSession == "NIGHT_CURFEW") Icons.Default.Bedtime else Icons.Default.WbSunny,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (selectedSession == "NIGHT_CURFEW") "Night Curfew Check" else "Morning Roll Call",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (selectedSession == "NIGHT_CURFEW") "Curfew Deadline: 08:30 PM • $selectedDate" else "Assembly: 07:30 AM • $selectedDate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Session selector tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val isNight = selectedSession == "NIGHT_CURFEW"
                SessionPill(
                    title = "Night Curfew (8:30 PM)",
                    icon = Icons.Default.NightlightRound,
                    isSelected = isNight,
                    modifier = Modifier.weight(1f),
                    onClick = { onSessionChanged("NIGHT_CURFEW") },
                    testTagKey = "session_night"
                )
                SessionPill(
                    title = "Morning (7:30 AM)",
                    icon = Icons.Default.WbSunny,
                    isSelected = !isNight,
                    modifier = Modifier.weight(1f),
                    onClick = { onSessionChanged("MORNING") },
                    testTagKey = "session_morning"
                )
            }

            if (isWardenMode && summary.unrecordedCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onMarkAllPresent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mark_all_present_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HostelTertiary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark All ${summary.unrecordedCount} Remaining as Present")
                }
            }
        }
    }
}

@Composable
fun SessionPill(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTagKey: String = ""
) {
    Surface(
        onClick = onClick,
        modifier = modifier.testTag(testTagKey),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium)
        }
    }
}

@Composable
fun StudentAttendanceCard(
    student: StudentEntity,
    record: AttendanceRecordEntity?,
    isWardenMode: Boolean,
    onMarkStatus: (String) -> Unit,
    onOpenRemarkDialog: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("student_card_${student.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
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
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = student.roomNo,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "${student.rollNo} • ${student.block} (Fl ${student.floor})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Current recorded status badge
                AttendanceStatusBadge(status = record?.status ?: "UNMARKED")
            }

            // If there's an existing record with time or remarks
            if (record != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Marked at ${record.markedTime}${if (record.remarks.isNotEmpty()) " • ${record.remarks}" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    QuickCallAction(phoneNumber = student.phone, label = "Call ${student.name}")
                }
            }

            // Quick Status Buttons for Warden Mode
            if (isWardenMode) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AttendanceActionButton(
                        label = "Present",
                        icon = Icons.Default.Check,
                        isSelected = record?.status == "PRESENT",
                        color = StatusPresentText,
                        modifier = Modifier.weight(1f),
                        onClick = { onMarkStatus("PRESENT") },
                        testTagKey = "mark_present_${student.id}"
                    )
                    AttendanceActionButton(
                        label = "Late",
                        icon = Icons.Default.Schedule,
                        isSelected = record?.status == "LATE",
                        color = StatusLateText,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenRemarkDialog("LATE") },
                        testTagKey = "mark_late_${student.id}"
                    )
                    AttendanceActionButton(
                        label = "Leave",
                        icon = Icons.Default.ExitToApp,
                        isSelected = record?.status == "ON_LEAVE",
                        color = StatusLeaveText,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenRemarkDialog("ON_LEAVE") },
                        testTagKey = "mark_leave_${student.id}"
                    )
                    AttendanceActionButton(
                        label = "Absent",
                        icon = Icons.Default.Close,
                        isSelected = record?.status == "ABSENT",
                        color = StatusAbsentText,
                        modifier = Modifier.weight(1f),
                        onClick = { onOpenRemarkDialog("ABSENT") },
                        testTagKey = "mark_absent_${student.id}"
                    )
                }
            }
        }
    }
}

@Composable
fun AttendanceActionButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTagKey: String = ""
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .testTag(testTagKey)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) color else color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) color.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = color
            )
        }
    }
}
