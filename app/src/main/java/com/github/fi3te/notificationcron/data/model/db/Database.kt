package com.github.fi3te.notificationcron.data.model.db

data class Database(
    val notificationCrons: List<NotificationCron>,
    val settings: Settings
)