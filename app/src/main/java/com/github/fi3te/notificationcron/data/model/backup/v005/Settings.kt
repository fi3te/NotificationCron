package com.github.fi3te.notificationcron.data.model.backup.v005

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Settings(
    val theme: Int,
    val notificationCancellation: Boolean,
    val displayDurationInSeconds: Int
)