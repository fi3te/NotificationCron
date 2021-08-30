package com.github.fi3te.notificationcron.data.model

import com.github.fi3te.notificationcron.data.model.db.NotificationCron

data class NotificationCronInput(
    val cron: String,
    val notificationTitle: String,
    val notificationText: String,
    val timeDisplay: Boolean,
    val onClickUri: String
)

fun NotificationCronInput.toNotificationCron() = NotificationCron(
    cron = cron,
    notificationTitle = notificationTitle,
    notificationText = notificationText,
    timeDisplay = timeDisplay,
    onClickUri = onClickUri
)
