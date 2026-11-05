package app.raihanlazuardi.whatif.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.SizeF
import android.view.View
import android.widget.RemoteViews
import app.raihanlazuardi.whatif.MainActivity
import app.raihanlazuardi.whatif.R
import app.raihanlazuardi.whatif.data.model.FastingCalculator
import app.raihanlazuardi.whatif.data.model.FastingPhase
import app.raihanlazuardi.whatif.data.model.FastingPhaseStatus
import app.raihanlazuardi.whatif.data.model.FastingSchedule
import app.raihanlazuardi.whatif.data.storage.WhatIFRepository
import java.time.LocalDateTime

class FastingAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val repository = WhatIFRepository.getInstance(context)
        val data = repository.dataFlow.value
        val schedule = data.schedule
        val now = LocalDateTime.now()
        val status = FastingCalculator.calculateStatus(schedule, now)
        val todayMokels = repository.getTodayMokels()

        for (appWidgetId in appWidgetIds) {
            updateWidget(
                context = context,
                appWidgetManager = appWidgetManager,
                appWidgetId = appWidgetId,
                status = status,
                schedule = schedule,
                mokelCount = todayMokels.size
            )
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        val repository = WhatIFRepository.getInstance(context)
        val data = repository.dataFlow.value
        val schedule = data.schedule
        val now = LocalDateTime.now()
        val status = FastingCalculator.calculateStatus(schedule, now)
        val todayMokels = repository.getTodayMokels()

        updateWidget(
            context = context,
            appWidgetManager = appWidgetManager,
            appWidgetId = appWidgetId,
            status = status,
            schedule = schedule,
            mokelCount = todayMokels.size
        )
    }

    private fun updateWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        status: FastingPhaseStatus,
        schedule: FastingSchedule,
        mokelCount: Int
    ) {
        // Multi-size responsive layout map (supporting 2x1, 2x2, 2x3, 3x2, and 3x3)
        val views2x1 = buildLayout(context, R.layout.widget_fasting_2x1, status, schedule, mokelCount)
        val views2x2 = buildLayout(context, R.layout.widget_fasting_2x2, status, schedule, mokelCount)
        val views2x3 = buildLayout(context, R.layout.widget_fasting_2x3, status, schedule, mokelCount)
        val views3x2 = buildLayout(context, R.layout.widget_fasting_3x2, status, schedule, mokelCount)
        val views3x3 = buildLayout(context, R.layout.widget_fasting_3x3, status, schedule, mokelCount)

        val sizeMapping = mapOf(
            SizeF(110f, 40f) to views2x1,   // 2x1
            SizeF(110f, 110f) to views2x2,  // 2x2
            SizeF(110f, 190f) to views2x3,  // 2x3
            SizeF(190f, 110f) to views3x2,  // 3x2
            SizeF(190f, 190f) to views3x3   // 3x3
        )

        val responsiveRemoteViews = RemoteViews(sizeMapping)
        appWidgetManager.updateAppWidget(appWidgetId, responsiveRemoteViews)
    }

    private fun buildLayout(
        context: Context,
        layoutId: Int,
        status: FastingPhaseStatus,
        schedule: FastingSchedule,
        mokelCount: Int
    ): RemoteViews {
        val views = RemoteViews(context.packageName, layoutId)

        // Launch app on tap
        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        val isFasting = status.phase == FastingPhase.FASTING

        // Background & Icons
        if (isFasting) {
            views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_fasting)
            views.setImageViewResource(R.id.widget_icon, R.drawable.ic_widget_moon)
            views.setTextViewText(R.id.widget_phase_title, "WAKTU PUASA 🌙")
            views.setTextViewText(R.id.widget_target_time, "Selesai pukul ${schedule.formattedEndTime}")
        } else {
            views.setInt(R.id.widget_root, "setBackgroundResource", R.drawable.widget_bg_eating)
            views.setImageViewResource(R.id.widget_icon, R.drawable.ic_widget_food)
            views.setTextViewText(R.id.widget_phase_title, "WAKTU MAKAN 🥗")
            views.setTextViewText(R.id.widget_target_time, "Mulai puasa pukul ${schedule.formattedStartTime}")
        }

        // Live Countdown via Android OS Chronometer
        // IMPORTANT: Chronometer uses SystemClock.elapsedRealtime(), NOT System.currentTimeMillis()!
        val remainingMillis = status.secondsRemaining * 1000L

        if (remainingMillis > 0) {
            val chronometerBase = SystemClock.elapsedRealtime() + remainingMillis
            views.setViewVisibility(R.id.widget_chronometer, View.VISIBLE)
            views.setViewVisibility(R.id.widget_countdown_text, View.GONE)
            views.setChronometerCountDown(R.id.widget_chronometer, true)
            views.setChronometer(R.id.widget_chronometer, chronometerBase, null, true)
        } else {
            views.setViewVisibility(R.id.widget_chronometer, View.GONE)
            views.setViewVisibility(R.id.widget_countdown_text, View.VISIBLE)
            views.setTextViewText(R.id.widget_countdown_text, "00:00")
        }

        // Mokel badge
        val mokelText = if (mokelCount > 0) "Mokel: ${mokelCount}x" else "Mokel: 0x"
        views.setTextViewText(R.id.widget_mokel_badge, mokelText)

        // Schedule info (present in 2x3, 3x2, 3x3)
        val scheduleStr = "IF ${schedule.fastingDurationHours.toInt()}:${schedule.eatingDurationHours.toInt()} (${schedule.formattedStartTime} - ${schedule.formattedEndTime})"
        try {
            views.setTextViewText(R.id.widget_schedule_info, scheduleStr)
        } catch (_: Exception) {}

        return views
    }

    companion object {
        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val ids = appWidgetManager.getAppWidgetIds(
                    ComponentName(context, FastingAppWidgetProvider::class.java)
                )
                if (ids != null && ids.isNotEmpty()) {
                    val intent = Intent(context, FastingAppWidgetProvider::class.java).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    }
                    context.sendBroadcast(intent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
