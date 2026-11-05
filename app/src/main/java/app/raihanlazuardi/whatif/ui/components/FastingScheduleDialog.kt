package app.raihanlazuardi.whatif.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.raihanlazuardi.whatif.data.model.FastingSchedule

enum class FastingPreset(val title: String, val fastHours: Int, val eatHours: Int) {
    LEAN_GAINS("16:8 (Standar)", 16, 8),
    MODERATE("18:6 (Moderat)", 18, 6),
    WARRIOR("20:4 (Warrior)", 20, 4),
    BEGINNER("14:10 (Pemula)", 14, 10),
    CUSTOM("Kustom", 0, 0)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FastingScheduleBottomSheet(
    currentSchedule: FastingSchedule,
    onDismissRequest: () -> Unit,
    onSaveSchedule: (FastingSchedule) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var startHour by remember { mutableIntStateOf(currentSchedule.startHour) }
    var startMinute by remember { mutableIntStateOf(currentSchedule.startMinute) }
    var endHour by remember { mutableIntStateOf(currentSchedule.endHour) }
    var endMinute by remember { mutableIntStateOf(currentSchedule.endMinute) }

    // Dialog state for TimePicker
    var pickingForStart by remember { mutableStateOf<Boolean?>(null) }

    val tempSchedule = remember(startHour, startMinute, endHour, endMinute) {
        FastingSchedule(startHour, startMinute, endHour, endMinute)
    }

    val selectedPreset = remember(startHour, startMinute, endHour, endMinute) {
        val fastDuration = tempSchedule.fastingDurationHours.toInt()
        when {
            fastDuration == 16 && tempSchedule.eatingDurationHours.toInt() == 8 -> FastingPreset.LEAN_GAINS
            fastDuration == 18 && tempSchedule.eatingDurationHours.toInt() == 6 -> FastingPreset.MODERATE
            fastDuration == 20 && tempSchedule.eatingDurationHours.toInt() == 4 -> FastingPreset.WARRIOR
            fastDuration == 14 && tempSchedule.eatingDurationHours.toInt() == 10 -> FastingPreset.BEGINNER
            else -> FastingPreset.CUSTOM
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Atur Jadwal Puasa",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pilih preset atau tentukan jam kustom",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Tutup"
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Presets chips
            Text(
                text = "Preset Populer",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FastingPreset.entries.forEach { preset ->
                    val isSelected = selectedPreset == preset
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (preset != FastingPreset.CUSTOM) {
                                // Default anchor: fasting starts at 20:00
                                startHour = 20
                                startMinute = 0
                                val targetEndTotal = (20 * 60 + preset.fastHours * 60) % (24 * 60)
                                endHour = targetEndTotal / 60
                                endMinute = targetEndTotal % 60
                            }
                        },
                        label = { Text(preset.title) },
                        leadingIcon = if (isSelected) {
                            {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else null,
                        shape = RoundedCornerShape(percent = 50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Start & End Time Selector Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TimeCard(
                    title = "Mulai Puasa",
                    subtitle = "Tutup Jendela Makan",
                    timeText = String.format("%02d:%02d", startHour, startMinute),
                    modifier = Modifier.weight(1f),
                    onClick = { pickingForStart = true }
                )

                TimeCard(
                    title = "Selesai Puasa",
                    subtitle = "Buka Jendela Makan",
                    timeText = String.format("%02d:%02d", endHour, endMinute),
                    modifier = Modifier.weight(1f),
                    onClick = { pickingForStart = false }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calculated Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${tempSchedule.fastingDurationHours.toInt()} Jam",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Durasi Puasa",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(1.dp, 32.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${tempSchedule.eatingDurationHours.toInt()} Jam",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "Jendela Makan",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Save button
            Button(
                onClick = {
                    onSaveSchedule(tempSchedule)
                    onDismissRequest()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(percent = 50)
            ) {
                Text(
                    text = "Simpan Jadwal",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }

    // TimePicker Dialog for Start or End Time
    pickingForStart?.let { isStart ->
        val timePickerState = rememberTimePickerState(
            initialHour = if (isStart) startHour else endHour,
            initialMinute = if (isStart) startMinute else endMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { pickingForStart = null },
            confirmButton = {
                TextButton(onClick = {
                    if (isStart) {
                        startHour = timePickerState.hour
                        startMinute = timePickerState.minute
                    } else {
                        endHour = timePickerState.hour
                        endMinute = timePickerState.minute
                    }
                    pickingForStart = null
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { pickingForStart = null }) {
                    Text("Batal")
                }
            },
            title = {
                Text(
                    text = if (isStart) "Pilih Jam Mulai Puasa" else "Pilih Jam Selesai Puasa",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}

@Composable
private fun TimeCard(
    title: String,
    subtitle: String,
    timeText: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = timeText,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}
