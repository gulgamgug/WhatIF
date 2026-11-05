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

class FastingCalculatorTest {

    @Test
    fun testOvernightFastingSchedule_calculatesDurationsCorrectly() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)
        // 20:00 to 12:00 is 16 hours fasting, 8 hours eating
        assertEquals(16f, schedule.fastingDurationHours, 0.01f)
        assertEquals(8f, schedule.eatingDurationHours, 0.01f)
    }

    @Test
    fun testOvernightFasting_inFastingPhase_evening() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)
        val now = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(21, 0))

        val status = FastingCalculator.calculateStatus(schedule, now)
        assertEquals(FastingPhase.FASTING, status.phase)
        // 21:00 to 12:00 next day = 15 hours remaining
        assertEquals(15L, status.hoursRemaining)
        assertEquals(0L, status.minutesRemaining)
    }

    @Test
    fun testOvernightFasting_inFastingPhase_morning() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)
        val now = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(8, 30))

        val status = FastingCalculator.calculateStatus(schedule, now)
        assertEquals(FastingPhase.FASTING, status.phase)
        // 8:30 to 12:00 = 3 hours 30 min remaining
        assertEquals(3L, status.hoursRemaining)
        assertEquals(30L, status.minutesRemaining)
    }

    @Test
    fun testOvernightFasting_inEatingPhase_afternoon() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)
        val now = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(15, 0))

        val status = FastingCalculator.calculateStatus(schedule, now)
        assertEquals(FastingPhase.EATING, status.phase)
        // 15:00 to 20:00 = 5 hours remaining
        assertEquals(5L, status.hoursRemaining)
        assertEquals(0L, status.minutesRemaining)
    }

    @Test
    fun testProgressRange() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)
        val now = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(4, 0))

        val status = FastingCalculator.calculateStatus(schedule, now)
        assertTrue(status.progress in 0f..1f)
    }

    @Test
    fun testSameDayFasting_daytimeFast() {
        val schedule = FastingSchedule(startHour = 5, startMinute = 0, endHour = 19, endMinute = 0)
        assertEquals(14f, schedule.fastingDurationHours, 0.01f)
        assertEquals(10f, schedule.eatingDurationHours, 0.01f)

        // At 10:00 -> Fasting
        val morning = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(10, 0))
        val statusMorning = FastingCalculator.calculateStatus(schedule, morning)
        assertEquals(FastingPhase.FASTING, statusMorning.phase)

        // At 21:00 -> Eating
        val night = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(21, 0))
        val statusNight = FastingCalculator.calculateStatus(schedule, night)
        assertEquals(FastingPhase.EATING, statusNight.phase)
    }

    @Test
    fun testExactBoundaryTimes() {
        val schedule = FastingSchedule(startHour = 20, startMinute = 0, endHour = 12, endMinute = 0)

        // Exactly at start time (20:00) -> Fasting begins
        val atStart = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(20, 0))
        val statusStart = FastingCalculator.calculateStatus(schedule, atStart)
        assertEquals(FastingPhase.FASTING, statusStart.phase)

        // Exactly at end time (12:00) -> Eating window begins
        val atEnd = LocalDateTime.of(LocalDate.of(2026, 11, 5), LocalTime.of(12, 0))
        val statusEnd = FastingCalculator.calculateStatus(schedule, atEnd)
        assertEquals(FastingPhase.EATING, statusEnd.phase)
    }
}
