package com.github.fi3te.notificationcron.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.preference.PreferenceManager
import com.github.fi3te.notificationcron.R
import com.github.fi3te.notificationcron.data.TIME_FORMATTER
import com.github.fi3te.notificationcron.data.getDayAndTimeString
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
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

fun getDisplayDurationsInMilliseconds(context: Context): Long? {
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val notificationCancellation = sharedPreferences.getBoolean("notification_cancellation", false)
    val displayDurationsInSeconds = sharedPreferences.getString("display_duration_in_seconds", null)
    if (notificationCancellation) {
        displayDurationsInSeconds?.let {
            return try {
                Integer.parseInt(displayDurationsInSeconds) * 1000L
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
    return null
}

fun initNotificationBuilder(context: Context, builder: NotificationCompat.Builder) {
    builder.setSmallIcon(R.drawable.notification_icon)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setGroup(NOTIFICATION_GROUP_KEY)

    getDisplayDurationsInMilliseconds(context)?.let {
        builder.setTimeoutAfter(it)
    }
}

fun createNotificationGroupSummary(context: Context, notificationManager: NotificationManager) {
    val notificationGroupSummary = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .apply {
            initNotificationBuilder(context, this)
        }
        .setStyle(NotificationCompat.InboxStyle())
        .setGroupSummary(true)
        .build()
    notificationManager.notify(NOTIFICATION_GROUP_SUMMARY_ID, notificationGroupSummary)
}

fun createNotification(context: Context, notificationCron: NotificationCron): Notification {
    val time = notificationCron.nextNotification?.format(TIME_FORMATTER) ?: ""
    val titlePrefix = if (notificationCron.timeDisplay) "$time " else ""
    val title = "$titlePrefix${notificationCron.notificationTitle}".trim()
    val text =
        if (notificationCron.notificationText.isNotBlank()) notificationCron.notificationText else null
    val uri = if (notificationCron.onClickUri.isNotBlank()) notificationCron.onClickUri else null

    var bigTextStyle = NotificationCompat.BigTextStyle()
        .setBigContentTitle(title)

    text?.let { bigTextStyle = bigTextStyle.bigText(text) }

    val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .apply {
            initNotificationBuilder(context, this)
        }
        .setContentTitle(title)
        .apply {
            text?.let { setContentText(text) }
        }
        .apply {
            uri?.let {
                val intent = Intent.parseUri(uri, 0)
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT)
                setContentIntent(pendingIntent)
            }
        }
        .setStyle(bigTextStyle)
    return builder.build()
}

fun showNotification(context: Context, notificationCron: NotificationCron) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    createNotificationChannel(context, notificationManager)
    createNotificationGroupSummary(context, notificationManager)

    val notification = createNotification(context, notificationCron)
    notificationManager.notify(newNotificationId(), notification)
}