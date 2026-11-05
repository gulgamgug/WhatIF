package app.raihanlazuardi.whatif

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import app.raihanlazuardi.whatif.ui.WhatIFViewModel
import app.raihanlazuardi.whatif.ui.components.FastingScheduleBottomSheet
import app.raihanlazuardi.whatif.ui.screens.HomeScreen
import app.raihanlazuardi.whatif.ui.theme.WhatIFTheme
import java.time.LocalDate

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
    var currentDestination by rememberSaveable { mutableStateOf(AppDestination.HOME) }
    var showScheduleSheet by rememberSaveable { mutableStateOf(false) }

    // Predictive back gesture: when on sub-screens (Calendar or Profile), back navigates to Home first
    BackHandler(enabled = currentDestination != AppDestination.HOME) {
        currentDestination = AppDestination.HOME
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                AppDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentDestination == destination,
                        onClick = { currentDestination = destination },
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
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.padding(innerPadding),
            label = "ScreenTransition"
        ) { destination ->
            when (destination) {
                AppDestination.HOME -> {
                    HomeScreen(
                        userName = appData.profile.name,
                        schedule = appData.schedule,
                        todayMokels = appData.mokelRecords.filter { it.localDate == LocalDate.now() },
                        onRecordMokelClick = { /* Wired in Sprint 5 */ },
                        onEditScheduleClick = { showScheduleSheet = true },
                        onDeleteMokel = { id -> viewModel.deleteMokel(id) }
                    )
                }
                AppDestination.CALENDAR -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Halaman ${destination.label}")
                    }
                }
                AppDestination.PROFILE -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Halaman ${destination.label}")
                    }
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
}
