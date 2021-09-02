package com.github.fi3te.notificationcron.data.remote

import com.github.fi3te.notificationcron.data.model.backup.Backup
import com.github.fi3te.notificationcron.data.model.backup.BackupV00500
import com.github.fi3te.notificationcron.data.model.backup.v00500.Settings as SettingsV00500
import com.github.fi3te.notificationcron.data.model.backup.v00500.NotificationCron as NotificationCronV00500
import com.github.fi3te.notificationcron.data.model.db.Database
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import com.github.fi3te.notificationcron.data.model.db.Settings

fun map(backup: Backup): Database {
    return when (backup) {
        is BackupV00500 -> map(backup)
    }
}

private fun map(backup: BackupV00500): Database = backup.run {
    return Database(
        notificationCrons.map { map(it) },
        map(settings)
    )
}

private fun map(notificationCron: NotificationCronV00500): NotificationCron = notificationCron.run {
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

private fun map(settings: SettingsV00500): Settings = settings.run {
    return Settings(
        theme, notificationCancellation, displayDurationInSeconds
    )
}