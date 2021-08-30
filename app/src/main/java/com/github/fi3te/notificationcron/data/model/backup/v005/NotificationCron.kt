package com.github.fi3te.notificationcron.data.model.backup.v005

import java.time.LocalDateTime

data class NotificationCron(
    val cron: String,
    val notificationTitle: String,
    val notificationText: String,
    val timeDisplay: Boolean,
    val onClickUri: String,
    val nextNotification: LocalDateTime?,
    val enabled: Boolean,
    val position: Long?
)