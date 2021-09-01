package com.github.fi3te.notificationcron.data.local

import android.content.Context
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.github.fi3te.notificationcron.data.model.db.Settings

class SettingsDao(val context: Context) {

    companion object {
        const val THEME_KEY = "theme"
        const val NOTIFICATION_CANCELLATION_KEY = "notification_cancellation"
        const val DISPLAY_DURATION_IN_SECONDS_KEY = "display_duration_in_seconds"
    }

    fun readSettings(): Settings {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val theme = Integer.parseInt(sharedPreferences.getString(THEME_KEY, "-1")!!)
        val notificationCancellation =
            sharedPreferences.getBoolean(NOTIFICATION_CANCELLATION_KEY, false)
        val displayDurationsInSeconds = Integer.parseInt(
            sharedPreferences.getString(
                DISPLAY_DURATION_IN_SECONDS_KEY, "10"
            )!!
        )
        return Settings(
            theme,
            notificationCancellation,
            displayDurationsInSeconds
        )
    }

    fun writeSettings(settings: Settings) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit {
            putString(THEME_KEY, "" + settings.theme)
            putBoolean(NOTIFICATION_CANCELLATION_KEY, settings.notificationCancellation)
            putString(
                DISPLAY_DURATION_IN_SECONDS_KEY,
                "" + settings.displayDurationInSeconds
            )
            commit()
        }
    }
}