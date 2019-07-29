package com.github.notificationcron.data

import android.app.AlarmManager
import android.content.Context
import com.github.notificationcron.data.local.AppDatabase
import com.github.notificationcron.data.local.NotificationCronDao
import com.github.notificationcron.data.model.NotificationCron
import com.github.notificationcron.data.receiver.AlarmReceiver
import java.time.ZoneId
import java.util.*

fun scheduleAlarms(context: Context) {
    val database = AppDatabase.getDatabase(context)
    val allNotificationCrons = database.notificationCronDao().findAll()

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val zoneId = TimeZone.getDefault().toZoneId()

    for (notificationCron in allNotificationCrons) {
        scheduleAlarm(context, notificationCron, alarmManager, zoneId)
    }
}

fun scheduleAlarm(context: Context, notificationCron: NotificationCron, alarmManager: AlarmManager, zoneId: ZoneId) {
    notificationCron.nextNotification?.let {
        val zonedDateTime = it.atZone(zoneId)
        val triggerAtMillis = zonedDateTime.toInstant().toEpochMilli()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            AlarmReceiver.getPendingIntent(context, notificationCron.id)
        )
    }
}

fun scheduleAlarm(context: Context, notificationCron: NotificationCron) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    scheduleAlarm(context, notificationCron, alarmManager, TimeZone.getDefault().toZoneId())
}

fun scheduleNextAlarm(context: Context, notificationCronDao: NotificationCronDao, notificationCron: NotificationCron) {
    computeNextExecution(notificationCron)
    notificationCronDao.update(notificationCron)
    if (notificationCron.nextNotification != null) {
        scheduleAlarm(context, notificationCron)
    }
}

fun removeAlarm(context: Context, notificationCronId: Long) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.cancel(AlarmReceiver.getPendingIntent(context, notificationCronId))
}