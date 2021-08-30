package com.github.fi3te.notificationcron.data

import android.app.AlarmManager
import android.content.Context
import com.github.fi3te.notificationcron.data.local.AppDatabase
import com.github.fi3te.notificationcron.data.local.NotificationCronDao
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import com.github.fi3te.notificationcron.data.receiver.AlarmReceiver
import java.time.ZoneId
import java.util.*

fun scheduleAlarms(context: Context) {
    scheduleAlarms(context, true)
}

fun scheduleNextAlarms(context: Context) {
    scheduleAlarms(context, false)
}

private fun scheduleAlarms(context: Context, reschedule: Boolean) {
    val database = AppDatabase.getDatabase(context)
    val notificationCronDao = database.notificationCronDao()
    val allNotificationCrons = notificationCronDao.findAll()

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    val zoneId = TimeZone.getDefault().toZoneId()

    for (notificationCron in allNotificationCrons) {
        if (!notificationCron.enabled) {
            continue
        }
        if (!reschedule) {
            computeNextExecution(notificationCron)
            notificationCronDao.update(notificationCron)
        }
        scheduleAlarmWithoutEnabledPropertyCheck(context, notificationCron, alarmManager, zoneId)
    }
}

private fun scheduleAlarmWithoutEnabledPropertyCheck(context: Context, notificationCron: NotificationCron, alarmManager: AlarmManager, zoneId: ZoneId) {
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
    if (!notificationCron.enabled) {
        return
    }
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    scheduleAlarmWithoutEnabledPropertyCheck(context, notificationCron, alarmManager, TimeZone.getDefault().toZoneId())
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