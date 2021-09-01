package com.github.fi3te.notificationcron.data.remote

import com.github.fi3te.notificationcron.data.model.backup.Backup
import com.github.fi3te.notificationcron.data.model.backup.BackupV005
import com.github.fi3te.notificationcron.data.model.backup.v005.Settings as SettingsV005
import com.github.fi3te.notificationcron.data.model.backup.v005.NotificationCron as NotificationCronV005
import com.github.fi3te.notificationcron.data.model.db.Database
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import com.github.fi3te.notificationcron.data.model.db.Settings

fun map(backup: Backup): Database {
    return when (backup) {
        is BackupV005 -> map(backup)
    }
}

private fun map(backup: BackupV005): Database = backup.run {
    return Database(
        notificationCrons.map { map(it) },
        map(settings)
    )
}

private fun map(notificationCron: NotificationCronV005): NotificationCron = notificationCron.run {
    return NotificationCron(
        0,
        cron,
        notificationTitle,
        notificationText,
        timeDisplay,
        onClickUri,
        nextNotification,
        enabled,
        position
    )
}

private fun map(settings: SettingsV005): Settings = settings.run {
    return Settings(
        theme, notificationCancellation, displayDurationInSeconds
    )
}