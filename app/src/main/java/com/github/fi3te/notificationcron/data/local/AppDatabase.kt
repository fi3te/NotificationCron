package com.github.fi3te.notificationcron.data.local

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.fi3te.notificationcron.data.model.NotificationCron

@Database(entities = [NotificationCron::class], version = 2)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationCronDao(): NotificationCronDao

    companion object {

        private var instance: AppDatabase? = null

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notification_cron ADD COLUMN enabled INTEGER DEFAULT 1 NOT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            val tmp = instance
            if (tmp != null) {
                return tmp
            } else {
                synchronized(this) {
                    val newInstance =
                        Room.databaseBuilder(context, AppDatabase::class.java, "notification_cron_database")
                            .addMigrations(MIGRATION_1_2)
                            .build()
                    instance = newInstance
                    return newInstance
                }
            }
        }
    }
}