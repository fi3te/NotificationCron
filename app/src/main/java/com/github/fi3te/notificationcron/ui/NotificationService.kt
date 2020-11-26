package com.github.fi3te.notificationcron.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.net.Uri
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
        .setAutoCancel(true)
        .setGroupSummary(true)
        .build()
    notificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID, notificationGroupSummary)
}

fun createNotification(context: Context, notificationCron: NotificationCron): Notification {
    val time = notificationCron.nextNotification?.format(TIME_FORMATTER) ?: ""
    val title = "$time ${notificationCron.notificationTitle}".trim()
    val text =
        if (notificationCron.notificationText.isNotBlank()) notificationCron.notificationText else null
    val uri = if (notificationCron.onClickURI.isNotBlank()) notificationCron.onClickURI else null

    var bigTextStyle = NotificationCompat.BigTextStyle()
        .setBigContentTitle(title)

    text?.let { bigTextStyle = bigTextStyle.bigText(text) }

    val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.notification_icon)
        .setContentTitle(title)
        .apply {
            text?.let { setContentText(text) }
        }
        .apply {
            uri?.let {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                val pendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)
                setContentIntent(pendingIntent)
            }
        }
        .setStyle(bigTextStyle)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setGroup(NOTIFICATION_GROUP_KEY)
    return builder.build()
}

fun showNotification(context: Context, notificationCron: NotificationCron) {
    val notification = createNotification(context, notificationCron)

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    createNotificationChannel(context, notificationManager)
    createNotificationGroupSummary(context, notificationManager)
    notificationManager.notify(newNotificationId(), notification)
}