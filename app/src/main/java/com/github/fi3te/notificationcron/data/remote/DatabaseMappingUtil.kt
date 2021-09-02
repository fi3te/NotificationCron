package com.github.fi3te.notificationcron.data.remote

import com.github.fi3te.notificationcron.data.model.backup.Backup
import com.github.fi3te.notificationcron.data.model.backup.BackupV00500
import com.github.fi3te.notificationcron.data.model.db.Database
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import com.github.fi3te.notificationcron.data.model.db.Settings
import com.github.fi3te.notificationcron.data.model.backup.v00500.NotificationCron as NotificationCronV00500
import com.github.fi3te.notificationcron.data.model.backup.v00500.Settings as SettingsV00500

fun map(database: Database): Backup = database.run {
    return BackupV00500(
        notificationCrons.map { map(it) },
        map(settings)
    )
}

private fun map(notificationCron: NotificationCron): NotificationCronV00500 = notificationCron.run {
    return NotificationCronV00500(
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

private fun map(settings: Settings): SettingsV00500 = settings.run {
    return SettingsV00500(
        theme, notificationCancellation, displayDurationInSeconds
    )
}
