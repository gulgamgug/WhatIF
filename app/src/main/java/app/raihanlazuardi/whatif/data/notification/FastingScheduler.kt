package app.raihanlazuardi.whatif.data.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import app.raihanlazuardi.whatif.data.model.FastingSchedule
import app.raihanlazuardi.whatif.data.receiver.FastingAlarmReceiver
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object FastingScheduler {

    const val ACTION_FAST_START = "app.raihanlazuardi.whatif.ACTION_FAST_START"
    const val ACTION_EAT_START = "app.raihanlazuardi.whatif.ACTION_EAT_START"

    private const val REQUEST_CODE_FAST_START = 2001
    private const val REQUEST_CODE_EAT_START = 2002

    fun scheduleAlarms(context: Context, schedule: FastingSchedule) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val now = LocalDateTime.now()
        val zoneId = ZoneId.systemDefault()

        // Schedule Fast Start Alarm
        val fastStartTime = calculateNextTrigger(now, schedule.startHour, schedule.startMinute)
        val fastStartMillis = fastStartTime.atZone(zoneId).toInstant().toEpochMilli()

        val fastStartIntent = Intent(context, FastingAlarmReceiver::class.java).apply {
            action = ACTION_FAST_START
        }
        val fastStartPending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_FAST_START,
            fastStartIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setAlarm(alarmManager, fastStartMillis, fastStartPending)

        // Schedule Eat Start Alarm
        val eatStartTime = calculateNextTrigger(now, schedule.endHour, schedule.endMinute)
        val eatStartMillis = eatStartTime.atZone(zoneId).toInstant().toEpochMilli()

        val eatStartIntent = Intent(context, FastingAlarmReceiver::class.java).apply {
            action = ACTION_EAT_START
        }
        val eatStartPending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_EAT_START,
            eatStartIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        setAlarm(alarmManager, eatStartMillis, eatStartPending)
    }

    fun cancelAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val fastStartIntent = Intent(context, FastingAlarmReceiver::class.java).apply {
            action = ACTION_FAST_START
        }
        val fastStartPending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_FAST_START,
            fastStartIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        fastStartPending?.let { alarmManager.cancel(it) }

        val eatStartIntent = Intent(context, FastingAlarmReceiver::class.java).apply {
            action = ACTION_EAT_START
        }
        val eatStartPending = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_EAT_START,
            eatStartIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        eatStartPending?.let { alarmManager.cancel(it) }
    }

    private fun calculateNextTrigger(now: LocalDateTime, hour: Int, minute: Int): LocalDateTime {
        val targetToday = LocalDateTime.of(now.toLocalDate(), LocalTime.of(hour, minute))
        return if (targetToday.isAfter(now)) {
            targetToday
        } else {
            targetToday.plusDays(1)
        }
    }

    private fun setAlarm(alarmManager: AlarmManager, triggerAtMillis: Long, pendingIntent: PendingIntent) {
        val canScheduleExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true
        }

        if (canScheduleExact) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
}
