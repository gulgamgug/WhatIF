package app.raihanlazuardi.whatif.data.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

enum class FastingPhase(val title: String, val subtitle: String) {
    FASTING("WAKTU PUASA", "Sedang Masa Puasa 🌙"),
    EATING("WAKTU MAKAN", "Jendela Makan Terbuka 🥗")
}

data class FastingSchedule(
    val startHour: Int = 20,
    val startMinute: Int = 0,
    val endHour: Int = 12,
    val endMinute: Int = 0
) {
    val formattedStartTime: String
        get() = String.format("%02d:%02d", startHour, startMinute)

    val formattedEndTime: String
        get() = String.format("%02d:%02d", endHour, endMinute)

    /**
     * Total duration of fasting in minutes
     */
    val fastingDurationMinutes: Int
        get() {
            val startTotal = startHour * 60 + startMinute
            val endTotal = endHour * 60 + endMinute
            return if (endTotal >= startTotal) {
                endTotal - startTotal
            } else {
                (24 * 60 - startTotal) + endTotal
            }
        }

    val fastingDurationHours: Float
        get() = fastingDurationMinutes / 60f

    val eatingDurationMinutes: Int
        get() = (24 * 60) - fastingDurationMinutes

    val eatingDurationHours: Float
        get() = eatingDurationMinutes / 60f
}

data class FastingPhaseStatus(
    val phase: FastingPhase,
    val progress: Float, // 0.0f to 1.0f
    val secondsRemaining: Long,
    val phaseStartTime: LocalDateTime,
    val phaseEndTime: LocalDateTime
) {
    val hoursRemaining: Long
        get() = secondsRemaining / 3600

    val minutesRemaining: Long
        get() = (secondsRemaining % 3600) / 60

    val secondsOfMinuteRemaining: Long
        get() = secondsRemaining % 60

    val formattedRemaining: String
        get() = if (hoursRemaining > 0) {
            String.format("%dj %02dm", hoursRemaining, minutesRemaining)
        } else {
            String.format("%02dm %02dd", minutesRemaining, secondsOfMinuteRemaining)
        }
}

data class MokelRecord(
    val id: String = UUID.randomUUID().toString(),
    val foodDescription: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val note: String = ""
) {
    val formattedTime: String
        get() = timestamp.format(DateTimeFormatter.ofPattern("HH:mm"))

    val formattedDate: String
        get() = timestamp.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))

    val localDate: LocalDate
        get() = timestamp.toLocalDate()
}

data class UserProfile(
    val name: String = "Sobat IF",
    val targetFastingHours: Int = 16,
    val notificationsEnabled: Boolean = true,
    val useDynamicColor: Boolean = true
)

data class WhatIFData(
    val profile: UserProfile = UserProfile(),
    val schedule: FastingSchedule = FastingSchedule(),
    val mokelRecords: List<MokelRecord> = emptyList(),
    val completedFastDates: List<LocalDate> = emptyList()
)
