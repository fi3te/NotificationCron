package com.github.fi3te.notificationcron.data.local

data class NotificationCronInput (
        val cron: String,
        val notificationTitle: String,
        val notificationText: String,
        val onClickURI: String
)