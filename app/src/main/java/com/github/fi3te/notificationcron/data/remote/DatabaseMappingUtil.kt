package com.github.fi3te.notificationcron.data.remote

import com.github.fi3te.notificationcron.data.model.backup.Backup
import com.github.fi3te.notificationcron.data.model.backup.BackupV005
import com.github.fi3te.notificationcron.data.model.db.Database
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import com.github.fi3te.notificationcron.data.model.db.Settings
import com.github.fi3te.notificationcron.data.model.backup.v005.NotificationCron as NotificationCronV005
import com.github.fi3te.notificationcron.data.model.backup.v005.Settings as SettingsV005

fun map(database: Database): Backup = database.run {
    return BackupV005(
        notificationCrons.map { map(it) },
        map(settings)
    )
}

private fun map(notificationCron: NotificationCron): NotificationCronV005 = notificationCron.run {
    return NotificationCronV005(
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

private fun map(settings: Settings): SettingsV005 = settings.run {
    return SettingsV005(
        theme, notificationCancellation, displayDurationInSeconds
    )
}
