package com.github.fi3te.notificationcron.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notification_cron")
data class NotificationCron(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "cron") val cron: String,
    @ColumnInfo(name = "notification_title") val notificationTitle: String = "",
    @ColumnInfo(name = "notification_text") val notificationText: String = "",
    @ColumnInfo(name = "next_notification") var nextNotification: LocalDateTime? = null,
    @ColumnInfo(name = "enabled") var enabled: Boolean = true,
    @ColumnInfo(name = "position") var position: Long? = null
)