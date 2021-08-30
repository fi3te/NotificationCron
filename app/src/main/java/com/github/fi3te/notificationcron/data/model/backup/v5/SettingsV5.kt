package com.github.fi3te.notificationcron.data.model.backup.v5

data class SettingsV5(
    val theme: Int,
    val notificationCancellation: Boolean,
    val displayDurationInSeconds: Int
)