package com.github.fi3te.notificationcron.data.model.backup.v5

import java.time.LocalDateTime

data class NotificationCronV5(
    val cron: String,
    val notificationTitle: String,
    val notificationText: String,
    val timeDisplay: Boolean,
    val onClickUri: String,
    val nextNotification: LocalDateTime?,
    val enabled: Boolean,
    val position: Long?
)