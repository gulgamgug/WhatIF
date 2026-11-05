package app.raihanlazuardi.whatif

import app.raihanlazuardi.whatif.data.model.MokelRecord
import app.raihanlazuardi.whatif.data.model.WhatIFData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class CalendarDataModelTest {

    @Test
    fun testNewlyInstalledApp_hasEmptyData() {
        // A freshly created WhatIFData must be completely empty (true to database)
        val data = WhatIFData()
        assertTrue("Newly installed app must have no mokel records", data.mokelRecords.isEmpty())
        assertTrue("Newly installed app must have no completed fast dates", data.completedFastDates.isEmpty())
    }

    @Test
    fun testToggleFastSuccess() {
        val today = LocalDate.now()
        var completedDates = emptyList<LocalDate>()

        // Toggle on
        completedDates = if (completedDates.contains(today)) completedDates.filterNot { it == today } else completedDates + today
        assertTrue(completedDates.contains(today))
        assertEquals(1, completedDates.size)

        // Toggle off
        completedDates = if (completedDates.contains(today)) completedDates.filterNot { it == today } else completedDates + today
        assertFalse(completedDates.contains(today))
        assertTrue(completedDates.isEmpty())
    }

    @Test
    fun testMokelRecordsByDateClassification() {
        val today = LocalDate.of(2026, 11, 5)
        val yesterday = LocalDate.of(2026, 11, 4)

        val mokelToday = MokelRecord(foodDescription = "Gorengan", timestamp = LocalDateTime.of(today, java.time.LocalTime.of(10, 0)))
        val records = listOf(mokelToday)

        val mokelsByDate = records.groupBy { it.localDate }
        assertTrue("Today has mokel", mokelsByDate.containsKey(today))
        assertFalse("Yesterday has no mokel", mokelsByDate.containsKey(yesterday))
    }
}
