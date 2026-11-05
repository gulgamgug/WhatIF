package app.raihanlazuardi.whatif.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.raihanlazuardi.whatif.data.model.MokelRecord
import app.raihanlazuardi.whatif.ui.theme.Coral100
import app.raihanlazuardi.whatif.ui.theme.Coral500
import app.raihanlazuardi.whatif.ui.theme.Coral600
import app.raihanlazuardi.whatif.ui.theme.Emerald500
import app.raihanlazuardi.whatif.ui.theme.Emerald600
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    mokelRecords: List<MokelRecord>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val today = remember { LocalDate.now() }

    // Map of Date -> List of Mokels
    val mokelsByDate = remember(mokelRecords) {
        mokelRecords.groupBy { it.localDate }
    }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = currentMonth.atDay(1).dayOfWeek
    // Offset for Monday start (Monday = 1, ..., Sunday = 7 -> offset = dayOfWeek.value - 1)
    val leadingEmptyDays = firstDayOfMonth.value - 1

    // Monthly summary stats
    val (successDaysCount, mokelDaysCount) = remember(currentMonth, mokelsByDate) {
        var success = 0
        var mokel = 0
        val maxDay = if (currentMonth == YearMonth.now()) today.dayOfMonth else daysInMonth
        for (day in 1..maxDay) {
            val date = currentMonth.atDay(day)
            if (!date.isAfter(today)) {
                if (mokelsByDate.containsKey(date) && mokelsByDate[date]!!.isNotEmpty()) {
                    mokel++
                } else {
                    success++
                }
            }
        }
        Pair(success, mokel)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Month Navigation Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(imageVector = Icons.Rounded.ChevronLeft, contentDescription = "Bulan Sebelumnya")
                }

                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("id-ID"))),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { currentMonth = currentMonth.plusMonths(1) },
                    enabled = currentMonth < YearMonth.now().plusMonths(6)
                ) {
                    Icon(imageVector = Icons.Rounded.ChevronRight, contentDescription = "Bulan Berikutnya")
                }
            }
        }

        // Monthly Stats Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$successDaysCount Hari",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Emerald600
                        )
                        Text(
                            text = "Berhasil IF 🟢",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(1.dp, 36.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$mokelDaysCount Hari",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = Coral600
                        )
                        Text(
                            text = "Ada Mokel 🍩",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Calendar Grid Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Day of week labels (Sen, Sel, Rab, Kam, Jum, Sab, Min)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min").forEach { dayLabel ->
                            Text(
                                text = dayLabel,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calendar Day Cells
                    val totalCells = leadingEmptyDays + daysInMonth
                    val rowsCount = (totalCells + 6) / 7

                    for (row in 0 until rowsCount) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (col in 0 until 7) {
                                val cellIndex = row * 7 + col
                                val dayNumber = cellIndex - leadingEmptyDays + 1

                                if (dayNumber in 1..daysInMonth) {
                                    val cellDate = currentMonth.atDay(dayNumber)
                                    val isPastOrToday = !cellDate.isAfter(today)
                                    val isToday = cellDate == today
                                    val dayMokels = mokelsByDate[cellDate] ?: emptyList()
                                    val hasMokel = dayMokels.isNotEmpty()

                                    CalendarDayCell(
                                        dayNumber = dayNumber,
                                        isToday = isToday,
                                        isPastOrToday = isPastOrToday,
                                        hasMokel = hasMokel,
                                        mokelCount = dayMokels.size,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (isPastOrToday) {
                                                onDayClick(cellDate)
                                            }
                                        }
                                    )
                                } else {
                                    // Empty placeholder
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Legend
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Emerald500)
                    )
                    Text(
                        text = "Berhasil IF",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.size(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Coral500)
                    )
                    Text(
                        text = "Ada Mokel",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CalendarDayCell(
    dayNumber: Int,
    isToday: Boolean,
    isPastOrToday: Boolean,
    hasMokel: Boolean,
    mokelCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cellColor = when {
        !isPastOrToday -> Color.Transparent
        hasMokel -> Coral100
        else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
    }

    val textColor = when {
        !isPastOrToday -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        hasMokel -> Coral600
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(cellColor)
            .then(
                if (isToday) {
                    Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                } else Modifier
            )
            .clickable(enabled = isPastOrToday, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = dayNumber.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isToday || hasMokel) FontWeight.Bold else FontWeight.Medium
                ),
                color = textColor
            )

            if (isPastOrToday) {
                if (hasMokel) {
                    Text(
                        text = "${mokelCount}x",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = Coral600
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Emerald500)
                    )
                }
            }
        }
    }
}
