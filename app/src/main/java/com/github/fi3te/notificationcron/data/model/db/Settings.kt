package com.github.fi3te.notificationcron.data.model.db

data class Settings (
    val theme: Int,
    val notificationCancellation: Boolean,
    val displayDurationInSeconds: Int
)