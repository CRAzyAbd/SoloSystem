package com.solosystem.app.worker

import android.app.*
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.solosystem.app.MainActivity
import java.util.concurrent.TimeUnit
import java.util.Calendar

class QuestReminderWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        sendNotification(
            title = "⚡ Quest Time, Hunter",
            body  = "Your daily quests are waiting. Don't break the streak."
        )
        return Result.success()
    }

    private fun sendNotification(title: String, body: String) {
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel("quest_reminders", "Quest Reminders", NotificationManager.IMPORTANCE_HIGH).apply {
            description = "Daily quest reminders"
        }
        manager.createNotificationChannel(channel)

        val intent = Intent(applicationContext, MainActivity::class.java)
        val pending = PendingIntent.getActivity(applicationContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(applicationContext, "quest_reminders")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()

        manager.notify(1001, notification)
    }

    companion object {
        fun scheduleDailyReminder(context: Context, hour: Int = 9, minute: Int = 0) {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, hour); set(Calendar.MINUTE, minute); set(Calendar.SECOND, 0) }
            if (target.before(now)) target.add(Calendar.DAY_OF_YEAR, 1)
            val delay = target.timeInMillis - now.timeInMillis

            val request = OneTimeWorkRequestBuilder<QuestReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "daily_reminder", ExistingWorkPolicy.REPLACE, request
            )
        }
    }
}