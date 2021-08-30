package com.github.fi3te.notificationcron.data.model.backup.v5

import com.github.fi3te.notificationcron.data.model.backup.Data

data class DataV5(
    val notificationCrons: List<NotificationCronV5>,
    val settings: SettingsV5
) : Data()