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
            }

            FastingScheduler.ACTION_EAT_START -> {
                NotificationHelper.showNotification(
                    context = context,
                    title = "Waktunya Makan Dibuka! 🥗",
                    message = "Jendela makan Anda telah dibuka sampai pukul ${currentData.schedule.formattedStartTime}. Selamat menikmati makanan bernutrisi!",
                    notificationId = NotificationHelper.NOTIFICATION_ID_EAT_START
                )
                // Reschedule for next day
                FastingScheduler.scheduleAlarms(context, currentData.schedule)
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
