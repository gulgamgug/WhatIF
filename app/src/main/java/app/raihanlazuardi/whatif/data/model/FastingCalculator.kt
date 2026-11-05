package app.raihanlazuardi.whatif.data.model

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object FastingCalculator {

    fun calculateStatus(
        schedule: FastingSchedule,
        now: LocalDateTime = LocalDateTime.now()
    ): FastingPhaseStatus {
        val today = now.toLocalDate()
        val currentTime = now.toLocalTime()

        val fastStart = LocalTime.of(schedule.startHour, schedule.startMinute)
        val fastEnd = LocalTime.of(schedule.endHour, schedule.endMinute)

        val isOvernight = fastEnd.isBefore(fastStart)

        val (phase, phaseStartTime, phaseEndTime) = if (isOvernight) {
            // Case 1: Fasting window crosses midnight (e.g., 20:00 to 12:00)
            if (!currentTime.isBefore(fastStart)) {
                // Today between fastStart and 23:59:59 -> Fasting
                Triple(
                    FastingPhase.FASTING,
                    LocalDateTime.of(today, fastStart),
                    LocalDateTime.of(today.plusDays(1), fastEnd)
                )
            } else if (currentTime.isBefore(fastEnd)) {
                // Today between 00:00:00 and fastEnd -> Fasting
                Triple(
                    FastingPhase.FASTING,
                    LocalDateTime.of(today.minusDays(1), fastStart),
                    LocalDateTime.of(today, fastEnd)
                )
            } else {
                // Today between fastEnd and fastStart -> Eating
                Triple(
                    FastingPhase.EATING,
                    LocalDateTime.of(today, fastEnd),
                    LocalDateTime.of(today, fastStart)
                )
            }
        } else {
            // Case 2: Fasting window is within the same day (e.g., 04:00 to 18:00)
            if (!currentTime.isBefore(fastStart) && currentTime.isBefore(fastEnd)) {
                // Fasting
                Triple(
                    FastingPhase.FASTING,
                    LocalDateTime.of(today, fastStart),
                    LocalDateTime.of(today, fastEnd)
                )
            } else if (!currentTime.isBefore(fastEnd)) {
                // Eating from today fastEnd to tomorrow fastStart
                Triple(
                    FastingPhase.EATING,
                    LocalDateTime.of(today, fastEnd),
                    LocalDateTime.of(today.plusDays(1), fastStart)
                )
            } else {
                // Eating from yesterday fastEnd to today fastStart
                Triple(
                    FastingPhase.EATING,
                    LocalDateTime.of(today.minusDays(1), fastEnd),
                    LocalDateTime.of(today, fastStart)
                )
            }
        }

        val totalSeconds = Duration.between(phaseStartTime, phaseEndTime).seconds.coerceAtLeast(1)
        val remainingSeconds = Duration.between(now, phaseEndTime).seconds.coerceAtLeast(0)
        val elapsedSeconds = (totalSeconds - remainingSeconds).coerceAtLeast(0)
        val progress = (elapsedSeconds.toFloat() / totalSeconds.toFloat()).coerceIn(0f, 1f)

        return FastingPhaseStatus(
            phase = phase,
            progress = progress,
            secondsRemaining = remainingSeconds,
            phaseStartTime = phaseStartTime,
            phaseEndTime = phaseEndTime
        )
    }
}
