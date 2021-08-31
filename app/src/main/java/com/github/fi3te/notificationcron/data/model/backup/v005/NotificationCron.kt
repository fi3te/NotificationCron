package com.github.fi3te.notificationcron.data.model.backup.v005

import com.squareup.moshi.JsonClass
import java.time.LocalDateTime

@JsonClass(generateAdapter = true)
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