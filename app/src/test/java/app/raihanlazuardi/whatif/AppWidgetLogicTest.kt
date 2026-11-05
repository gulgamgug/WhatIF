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

        // Verify remaining time in seconds and millis
        assertEquals(14 * 3600L, status.secondsRemaining)
        val remainingMillis = status.secondsRemaining * 1000L
        assertEquals(14 * 3600 * 1000L, remainingMillis)
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

        // 6 hours remaining = 21,600 seconds
        assertEquals(6 * 3600L, status.secondsRemaining)
        val remainingMillis = status.secondsRemaining * 1000L
        assertEquals(6 * 3600 * 1000L, remainingMillis)
        // Under 24 hours, definitely not 498,000+ hours!
        assertTrue(status.hoursRemaining <= 24)
    }
}
