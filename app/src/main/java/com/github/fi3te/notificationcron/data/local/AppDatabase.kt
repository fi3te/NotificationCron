package com.github.fi3te.notificationcron.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.fi3te.notificationcron.data.model.db.NotificationCron

@Database(entities = [NotificationCron::class], version = 5)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun notificationCronDao(): NotificationCronDao

    companion object {

        private var instance: AppDatabase? = null

        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notification_cron ADD COLUMN enabled INTEGER DEFAULT 1 NOT NULL")
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notification_cron ADD COLUMN position INTEGER")
                database.execSQL("UPDATE notification_cron SET position = id WHERE position IS NULL")
            }
        }

        val MIGRATION_3_4: Migration = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notification_cron ADD COLUMN on_click_uri TEXT DEFAULT '' NOT NULL")
            }
        }

        val MIGRATION_4_5: Migration = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE notification_cron ADD COLUMN time_display INTEGER DEFAULT 1 NOT NULL")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            val tmp = instance
            if (tmp != null) {
                return tmp
            } else {
                synchronized(this) {
                    val newInstance =
                        Room.databaseBuilder(
                            context, AppDatabase::class.java, "notification_cron_database"
                        ).addMigrations(
                            MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5
                        ).build()
                    instance = newInstance
                    return newInstance
                }
            }
        }
    }
}
