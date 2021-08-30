package com.github.fi3te.notificationcron.data.model.backup

import com.github.fi3te.notificationcron.data.model.backup.v005.NotificationCron
import com.github.fi3te.notificationcron.data.model.backup.v005.Settings

const val V005 = "5"

data class BackupV005(
    val notificationCrons: List<NotificationCron>,
    val settings: Settings
) : Backup(V005)