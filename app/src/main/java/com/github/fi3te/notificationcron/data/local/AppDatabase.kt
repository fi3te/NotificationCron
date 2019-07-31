package com.github.fi3te.notificationcron.data.local

import android.content.Context
import androidx.room.*
import com.github.fi3te.notificationcron.data.model.NotificationCron

@Database(entities = [NotificationCron::class], version = 1)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationCronDao(): NotificationCronDao

    companion object {

        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tmp = instance
            if (tmp != null) {
                return tmp
            } else {
                synchronized(this) {
                    val newInstance =
                        Room.databaseBuilder(context, AppDatabase::class.java, "notification_cron_database").build()
                    instance = newInstance
                    return newInstance
                }
            }
        }
    }
}