package com.example.ui.screens

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HostelPrimary
import com.example.ui.theme.StatusLateBg
import com.example.ui.theme.StatusLateText
import com.example.ui.theme.StatusPresentBg
import com.example.ui.theme.StatusPresentText
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.HostelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostelMainScreen(
    viewModel: HostelViewModel
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isWardenMode by viewModel.isWardenMode.collectAsState()
    val activePasses by viewModel.activeGatePasses.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(HostelPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Hostel & Canteen",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isWardenMode) "Warden Dashboard" else "Student View",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // Mode Switcher Pill
                    Surface(
                        onClick = { viewModel.toggleWardenMode() },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isWardenMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .testTag("mode_toggle_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isWardenMode) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isWardenMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isWardenMode) "Warden" else "Resident",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isWardenMode) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.ATTENDANCE,
                    onClick = { viewModel.setTab(AppTab.ATTENDANCE) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.ATTENDANCE) Icons.Filled.EventNote else Icons.Outlined.EventNote,
                            contentDescription = "Roll Call"
                        )
                    },
                    label = { Text("Roll Call", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HostelPrimary,
                        selectedTextColor = HostelPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_attendance")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.GATE_PASS,
                    onClick = { viewModel.setTab(AppTab.GATE_PASS) },
                    icon = {
                        Box {
                            Icon(
                                imageVector = if (currentTab == AppTab.GATE_PASS) Icons.Filled.ExitToApp else Icons.Outlined.ExitToApp,
                                contentDescription = "Gate Pass"
                            )
                            if (activePasses.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(StatusLateText)
                                )
                            }
                        }
                    },
                    label = { Text("Gate Pass", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HostelPrimary,
                        selectedTextColor = HostelPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_gate_pass")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.CANTEEN,
                    onClick = { viewModel.setTab(AppTab.CANTEEN) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.CANTEEN) Icons.Filled.Restaurant else Icons.Outlined.Restaurant,
                            contentDescription = "Canteen"
                        )
                    },
                    label = { Text("Canteen", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HostelPrimary,
                        selectedTextColor = HostelPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_canteen")
                )

                NavigationBarItem(
                    selected = currentTab == AppTab.RESIDENTS,
                    onClick = { viewModel.setTab(AppTab.RESIDENTS) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == AppTab.RESIDENTS) Icons.Filled.People else Icons.Outlined.People,
                            contentDescription = "Residents"
                        )
                    },
                    label = { Text("Residents", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = HostelPrimary,
                        selectedTextColor = HostelPrimary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_residents")
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = currentTab,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { tab ->
            when (tab) {
                AppTab.ATTENDANCE -> AttendanceScreen(viewModel = viewModel)
                AppTab.GATE_PASS -> GatePassScreen(viewModel = viewModel)
                AppTab.CANTEEN -> CanteenScreen(viewModel = viewModel)
                AppTab.RESIDENTS -> ResidentsScreen(viewModel = viewModel)
            }
        }
    }
}
