package app.raihanlazuardi.whatif

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import app.raihanlazuardi.whatif.ui.WhatIFViewModel
import app.raihanlazuardi.whatif.ui.components.DayDetailBottomSheet
import app.raihanlazuardi.whatif.ui.components.FastingScheduleBottomSheet
import app.raihanlazuardi.whatif.ui.components.MokelLoggerBottomSheet
import app.raihanlazuardi.whatif.ui.screens.CalendarScreen
import app.raihanlazuardi.whatif.ui.screens.HomeScreen
import app.raihanlazuardi.whatif.ui.screens.ProfileScreen
import app.raihanlazuardi.whatif.ui.theme.WhatIFTheme
import java.time.LocalDate
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WhatIFViewModel = viewModel()
            val appData by viewModel.uiState.collectAsState()
            WhatIFTheme(dynamicColor = appData.profile.useDynamicColor) {
                WhatIFApp(viewModel = viewModel, appData = appData)
            }
        }
    }
}

enum class AppDestination(
    val label: String,
    val icon: ImageVector
) {
    HOME("Beranda", Icons.Rounded.Timer),
    CALENDAR("Kalender", Icons.Rounded.CalendarMonth),
    PROFILE("Profil", Icons.Rounded.AccountCircle)
}

@Composable
fun WhatIFApp(
    viewModel: WhatIFViewModel,
    appData: app.raihanlazuardi.whatif.data.model.WhatIFData
) {
    val pagerState = rememberPagerState(
        initialPage = AppDestination.HOME.ordinal,
        pageCount = { AppDestination.entries.size }
    )
    val coroutineScope = rememberCoroutineScope()

    var showScheduleSheet by rememberSaveable { mutableStateOf(false) }
    var showMokelSheet by rememberSaveable { mutableStateOf(false) }
    var selectedCalendarDate by remember { mutableStateOf<LocalDate?>(null) }
    var targetDateForNewMokel by remember { mutableStateOf<LocalDate?>(null) }

    // Predictive back gesture: when on sub-screens (Calendar or Profile), back smoothly scrolls to Home first
    BackHandler(enabled = pagerState.currentPage != AppDestination.HOME.ordinal) {
        coroutineScope.launch {
            pagerState.animateScrollToPage(
                page = AppDestination.HOME.ordinal,
                animationSpec = tween(durationMillis = 200, easing = EaseOut)
            )
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppDestination.entries.forEach { destination ->
                    val isSelected = (if (pagerState.isScrollInProgress) pagerState.targetPage else pagerState.currentPage) == destination.ordinal
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (pagerState.currentPage != destination.ordinal) {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(
                                        page = destination.ordinal,
                                        animationSpec = tween(
                                            durationMillis = 200,
                                            easing = EaseOut
                                        )
                                    )
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = { Text(destination.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            beyondViewportPageCount = 1,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            key = { page -> AppDestination.entries[page] }
        ) { page ->
            when (AppDestination.entries[page]) {
                AppDestination.HOME -> {
                    HomeScreen(
                        userName = appData.profile.name,
                        schedule = appData.schedule,
                        todayMokels = appData.mokelRecords.filter { it.localDate == LocalDate.now() },
                        onRecordMokelClick = { showMokelSheet = true },
                        onEditScheduleClick = { showScheduleSheet = true },
                        onDeleteMokel = { id -> viewModel.deleteMokel(id) }
                    )
                }
                AppDestination.CALENDAR -> {
                    CalendarScreen(
                        mokelRecords = appData.mokelRecords,
                        onDayClick = { date -> selectedCalendarDate = date }
                    )
                }
                AppDestination.PROFILE -> {
                    ProfileScreen(
                        appData = appData,
                        onUpdateName = { newName -> viewModel.updateProfileName(newName) },
                        onToggleDynamicColor = { enabled ->
                            viewModel.updateProfile(appData.profile.copy(useDynamicColor = enabled))
                        },
                        onToggleNotifications = { enabled ->
                            viewModel.updateNotificationsEnabled(enabled)
                        },
                        onSendTestNotification = {
                            viewModel.sendTestNotification()
                        },
                        onExportJson = { viewModel.exportDatabaseJson() },
                        onImportJson = { jsonStr -> viewModel.importDatabaseJson(jsonStr) },
                        onResetData = { viewModel.resetDatabase() }
                    )
                }
            }
        }
    }

    if (showScheduleSheet) {
        FastingScheduleBottomSheet(
            currentSchedule = appData.schedule,
            onDismissRequest = { showScheduleSheet = false },
            onSaveSchedule = { newSchedule ->
                viewModel.updateSchedule(newSchedule)
            }
        )
    }

    if (showMokelSheet) {
        MokelLoggerBottomSheet(
            targetDate = targetDateForNewMokel ?: LocalDate.now(),
            onDismissRequest = {
                showMokelSheet = false
                targetDateForNewMokel = null
            },
            onSaveMokel = { food, note, dateTime ->
                viewModel.addMokel(food = food, note = note, dateTime = dateTime)
                targetDateForNewMokel = null
            }
        )
    }

    selectedCalendarDate?.let { date ->
        DayDetailBottomSheet(
            date = date,
            mokelRecords = appData.mokelRecords.filter { it.localDate == date },
            onDismissRequest = { selectedCalendarDate = null },
            onAddMokelClick = { selectedDate ->
                targetDateForNewMokel = selectedDate
                showMokelSheet = true
            },
            onDeleteMokel = { id -> viewModel.deleteMokel(id) }
        )
    }
}
