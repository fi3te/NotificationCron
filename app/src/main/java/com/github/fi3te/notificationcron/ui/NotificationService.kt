package com.github.fi3te.notificationcron.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.github.fi3te.notificationcron.R
import com.github.fi3te.notificationcron.data.TIME_FORMATTER
import com.github.fi3te.notificationcron.data.getDayAndTimeString
import com.github.fi3te.notificationcron.data.model.NotificationCron
import java.util.concurrent.atomic.AtomicInteger


private const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
private const val NOTIFICATION_GROUP_KEY = "NOTIFICATION_GROUP_KEY"
private const val NOTIFICATION_GROUP_SUMMARY_ID = 524195436
private val NOTIFICATION_ID_COUNTER = AtomicInteger()

fun newNotificationId(): Int {
    var newValue: Int
    do {
        val existingValue = NOTIFICATION_ID_COUNTER.get()
        // restriction to compute an id smaller than 2147483647
        newValue = (existingValue % 21) + 1
    } while (!NOTIFICATION_ID_COUNTER.compareAndSet(existingValue, newValue))
    return "$newValue${getDayAndTimeString()}".toInt()
}

fun createNotificationChannel(context: Context, notificationManager: NotificationManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.notification_channel_name)
        val descriptionText = context.getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }
}

fun createNotificationGroupSummary(context: Context, notificationManager: NotificationManager) {
    val notificationGroupSummary = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_icon)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setGroup(NOTIFICATION_GROUP_KEY)
        .setStyle(NotificationCompat.InboxStyle())
        .setGroupSummary(true)
        .build()
    notificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID, notificationGroupSummary)
}

fun createNotification(context: Context, title: String, text: String): Notification {
    val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(title)
        .setContentText(text)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setGroup(NOTIFICATION_GROUP_KEY)
    return builder.build()
}

fun showNotification(context: Context, title: String, text: String) {
    val notification = createNotification(context, title, text)
    val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    createNotificationChannel(context, notificationManager)
    createNotificationGroupSummary(context, notificationManager)
    notificationManager.notify(newNotificationId(), notification)
}

fun showNotification(context: Context, notificationCron: NotificationCron) {
    val timePrefix: String = notificationCron.nextNotification?.let {
        "${it.format(TIME_FORMATTER)} "
    } ?: ""
    showNotification(
        context,
        timePrefix + notificationCron.notificationTitle,
        notificationCron.notificationText
    )
}