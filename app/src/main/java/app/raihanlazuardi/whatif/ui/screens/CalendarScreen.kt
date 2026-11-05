package app.raihanlazuardi.whatif.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun CalendarScreen(
    mokelRecords: List<MokelRecord>,
    completedFastDates: List<LocalDate>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    val currentYearMonth = remember { YearMonth.now() }
    val coroutineScope = rememberCoroutineScope()

    // Map of Date -> List of Mokels
    val mokelsByDate = remember(mokelRecords) {
        mokelRecords.groupBy { it.localDate }
    }

    val completedDatesSet = remember(completedFastDates) {
        completedFastDates.toSet()
    }

    // Generate list of months for vertical scrolling:
    // Index 0: Next Month (1 month ahead for planning)
    // Index 1: Current Month (Bulan Ini)
    // Index 2..25: Past 24 months (2 full years of history)
    val monthsList = remember {
        val list = mutableListOf<YearMonth>()
        list.add(currentYearMonth.plusMonths(1)) // future
        for (offset in 0..24) {
            list.add(currentYearMonth.minusMonths(offset.toLong()))
        }
        list
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = 1)

    val isScrolledAwayFromCurrentMonth = remember {
        derivedStateOf { listState.firstVisibleItemIndex != 1 }
    }

    // Total stats true to database
    val totalSuccessCount = completedDatesSet.size
    val totalMokelDaysCount = mokelsByDate.keys.size

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Screen Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Riwayat Puasa",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Scroll ke bawah untuk melihat riwayat bulan lalu",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Stats Summary Card (True to database)
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
                                text = "$totalSuccessCount Hari",
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
                                text = "$totalMokelDaysCount Hari",
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

            // Legend Indicator
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        Text(
                            text = "Tanpa Data",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Vertically Scrollable Months
            items(monthsList, key = { it.toString() }) { month ->
                val isCurrentMonth = month == currentYearMonth
                MonthCalendarCard(
                    month = month,
                    isCurrentMonth = isCurrentMonth,
                    today = today,
                    mokelsByDate = mokelsByDate,
                    completedDates = completedDatesSet,
                    onDayClick = onDayClick
                )
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Quick Jump Floating Button to "Bulan Ini"
        AnimatedVisibility(
            visible = isScrolledAwayFromCurrentMonth.value,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            FilledTonalButton(
                onClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(index = 1)
                    }
                },
                shape = RoundedCornerShape(percent = 50),
                elevation = androidx.compose.material3.ButtonDefaults.filledTonalButtonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Kembali ke Bulan Ini",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun MonthCalendarCard(
    month: YearMonth,
    isCurrentMonth: Boolean,
    today: LocalDate,
    mokelsByDate: Map<LocalDate, List<MokelRecord>>,
    completedDates: Set<LocalDate>,
    onDayClick: (LocalDate) -> Unit
) {
    val daysInMonth = month.lengthOfMonth()
    val firstDayOfMonth = month.atDay(1).dayOfWeek
    // Monday start: Monday=1 -> 0 offset, Sunday=7 -> 6 offset
    val leadingEmptyDays = firstDayOfMonth.value - 1

    // Calculate month stats based strictly on database records
    val (monthSuccessCount, monthMokelCount) = remember(month, mokelsByDate, completedDates) {
        var success = 0
        var mokel = 0
        for (day in 1..daysInMonth) {
            val date = month.atDay(day)
            val dayMokels = mokelsByDate[date]
            if (!dayMokels.isNullOrEmpty()) {
                mokel++
            } else if (completedDates.contains(date)) {
                success++
            }
        }
        Pair(success, mokel)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Month Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("id-ID"))),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (isCurrentMonth) {
                        Surface(
                            shape = RoundedCornerShape(percent = 50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "Bulan Ini",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Month stats badge
                Text(
                    text = if (monthSuccessCount == 0 && monthMokelCount == 0) {
                        "Belum ada data"
                    } else {
                        "🟢 $monthSuccessCount • 🍩 $monthMokelCount"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

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

            Spacer(modifier = Modifier.height(10.dp))

            // Month Grid
            val totalCells = leadingEmptyDays + daysInMonth
            val rowsCount = (totalCells + 6) / 7

            for (row in 0 until rowsCount) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (col in 0 until 7) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - leadingEmptyDays + 1

                        if (dayNumber in 1..daysInMonth) {
                            val cellDate = month.atDay(dayNumber)
                            val isPastOrToday = !cellDate.isAfter(today)
                            val isToday = cellDate == today
                            val dayMokels = mokelsByDate[cellDate] ?: emptyList()
                            val hasMokel = dayMokels.isNotEmpty()
                            val isSuccess = completedDates.contains(cellDate) && !hasMokel

                            CalendarDayCell(
                                dayNumber = dayNumber,
                                isToday = isToday,
                                isPastOrToday = isPastOrToday,
                                hasMokel = hasMokel,
                                mokelCount = dayMokels.size,
                                isSuccess = isSuccess,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    if (isPastOrToday) {
                                        onDayClick(cellDate)
                                    }
                                }
                            )
                        } else {
                            // Blank placeholder
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
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
    isSuccess: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    // True to database colors:
    // If no data, neutral transparent/light surface (NO green dot, NO red dot!)
    val cellColor = when {
        !isPastOrToday -> Color.Transparent
        hasMokel -> Coral100
        isSuccess -> Emerald600.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    val textColor = when {
        !isPastOrToday -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.28f)
        hasMokel -> Coral600
        isSuccess -> Emerald600
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.5.dp)
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
                    fontWeight = if (isToday || hasMokel || isSuccess) FontWeight.Bold else FontWeight.Normal
                ),
                color = textColor
            )

            if (isPastOrToday) {
                when {
                    hasMokel -> {
                        Text(
                            text = "${mokelCount}x",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Coral600
                        )
                    }
                    isSuccess -> {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Emerald600)
                        )
                    }
                    else -> {
                        // NO DATA: Leave completely empty below the number!
                        Spacer(modifier = Modifier.size(5.dp))
                    }
                }
            }
        }
    }
}
