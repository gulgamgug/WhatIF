package app.raihanlazuardi.whatif.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import app.raihanlazuardi.whatif.data.notification.FastingScheduler
import app.raihanlazuardi.whatif.data.notification.NotificationHelper
import app.raihanlazuardi.whatif.data.storage.WhatIFRepository

class FastingAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val repository = WhatIFRepository.getInstance(context)
        val currentData = repository.dataFlow.value

        // Only fire notifications if enabled by user
        if (!currentData.profile.notificationsEnabled) {
            return
        }

        when (intent.action) {
            FastingScheduler.ACTION_FAST_START -> {
                NotificationHelper.showNotification(
                    context = context,
                    title = "Waktunya Puasa Dimulai! 🌙",
                    message = "Jendela makan ditutup. Waktunya berpuasa dan menjaga hidrasi tubuh hingga pukul ${currentData.schedule.formattedEndTime}!",
                    notificationId = NotificationHelper.NOTIFICATION_ID_FAST_START
                )
                // Reschedule for next day
                FastingScheduler.scheduleAlarms(context, currentData.schedule)
                app.raihanlazuardi.whatif.widget.FastingAppWidgetProvider.updateAllWidgets(context)
            }

            FastingScheduler.ACTION_EAT_START -> {
                val today = java.time.LocalDate.now()
                val mokelsToday = repository.getMokelsForDate(today)
                val isCleanFast = mokelsToday.isEmpty()

                if (isCleanFast) {
                    // Otomatis catat ke database kalender sebagai Berhasil IF
                    repository.markFastSuccess(today)
                    NotificationHelper.showNotification(
                        context = context,
                        title = "Selamat! Puasa IF Berhasil 🎉",
                        message = "Target puasa ${currentData.schedule.fastingDurationHours.toInt()} jam tercapai tanpa mokel! Jendela makan dibuka sampai pukul ${currentData.schedule.formattedStartTime}.",
                        notificationId = NotificationHelper.NOTIFICATION_ID_EAT_START
                    )
                } else {
                    NotificationHelper.showNotification(
                        context = context,
                        title = "Waktunya Makan Dibuka! 🥗",
                        message = "Jendela makan Anda telah dibuka sampai pukul ${currentData.schedule.formattedStartTime}. Selamat menikmati makanan bernutrisi!",
                        notificationId = NotificationHelper.NOTIFICATION_ID_EAT_START
                    )
                }
                // Reschedule for next day
                FastingScheduler.scheduleAlarms(context, currentData.schedule)
                app.raihanlazuardi.whatif.widget.FastingAppWidgetProvider.updateAllWidgets(context)
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                // Re-register alarms on device boot
                if (currentData.profile.notificationsEnabled) {
                    FastingScheduler.scheduleAlarms(context, currentData.schedule)
                }
            }
        }
    }
}
