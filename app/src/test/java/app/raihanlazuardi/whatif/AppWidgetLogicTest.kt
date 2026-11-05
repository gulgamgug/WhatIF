package app.raihanlazuardi.whatif

import app.raihanlazuardi.whatif.data.model.FastingCalculator
import app.raihanlazuardi.whatif.data.model.FastingPhase
import app.raihanlazuardi.whatif.data.model.FastingSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class AppWidgetLogicTest {

    @Test
    fun testWidgetChronometerTargetMillis_isAccurateForFastingPhase() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)
        // 22:00 -> in fasting phase, target is 12:00 tomorrow
        val now = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(22, 0))
        val status = FastingCalculator.calculateStatus(schedule, now)

        assertEquals(FastingPhase.FASTING, status.phase)
        assertEquals(12, status.phaseEndTime.hour)
        assertEquals(0, status.phaseEndTime.minute)
        assertEquals(6, status.phaseEndTime.dayOfMonth) // next day

        val zoneId = ZoneId.systemDefault()
        val targetMillis = status.phaseEndTime.atZone(zoneId).toInstant().toEpochMilli()
        val nowMillis = now.atZone(zoneId).toInstant().toEpochMilli()
        assertTrue("Target time must be in future", targetMillis > nowMillis)
        assertEquals(14 * 3600 * 1000L, targetMillis - nowMillis)
    }

    @Test
    fun testWidgetChronometerTargetMillis_isAccurateForEatingPhase() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)
        // 14:00 -> in eating phase, target is 20:00 today
        val now = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(14, 0))
        val status = FastingCalculator.calculateStatus(schedule, now)

        assertEquals(FastingPhase.EATING, status.phase)
        assertEquals(20, status.phaseEndTime.hour)
        assertEquals(0, status.phaseEndTime.minute)
        assertEquals(5, status.phaseEndTime.dayOfMonth) // today

        val zoneId = ZoneId.systemDefault()
        val targetMillis = status.phaseEndTime.atZone(zoneId).toInstant().toEpochMilli()
        val nowMillis = now.atZone(zoneId).toInstant().toEpochMilli()
        assertTrue("Target time must be in future", targetMillis > nowMillis)
        assertEquals(6 * 3600 * 1000L, targetMillis - nowMillis)
    }
}
