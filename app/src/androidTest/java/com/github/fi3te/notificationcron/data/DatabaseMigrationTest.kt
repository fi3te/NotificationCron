package com.github.fi3te.notificationcron.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.fi3te.notificationcron.data.local.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime
import kotlin.random.Random

private const val DB_NAME = "AppDatabase"
private const val TABLE_NAME = "notification_cron"
private const val ID_COLUMN_NAME = "id"
private const val CRON_COLUMN_NAME = "cron"
private const val TITLE_COLUMN_NAME = "notification_title"
private const val TEXT_COLUMN_NAME = "notification_text"
private const val TIME_DISPLAY_COLUMN_NAME = "time_display"
private const val ON_CLICK_URI_COLUMN_NAME = "on_click_uri"
private const val NEXT_COLUMN_NAME = "next_notification"
private const val ENABLED_COLUMN_NAME = "enabled"
private const val POSITION_COLUMN_NAME = "position"
private const val SELECT_ALL_QUERY = "SELECT * FROM $TABLE_NAME ORDER BY $ID_COLUMN_NAME"
private const val TEST_DATA_SIZE = 50

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    @Rule
    @JvmField
    val testHelper = MigrationTestHelper(
        getInstrumentation(),
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    private fun insertTestData(db: SupportSQLiteDatabase, testData: List<ContentValues>) {
        for (element in testData) {
            db.insert(TABLE_NAME, SQLiteDatabase.CONFLICT_FAIL, element)
        }
    }

    private fun createTestData(length: Int, dbVersion: Int): List<ContentValues> {
        return List(length) { createTestEntry(it.toLong(), dbVersion) }
    }

    private fun createTestEntry(index: Long, dbVersion: Int): ContentValues {
        return ContentValues().apply {
            put(ID_COLUMN_NAME, index)
            put(
                CRON_COLUMN_NAME,
                "${Random.nextInt(0, 59)} " +
                        "${Random.nextInt(0, 59)} " +
                        "${Random.nextInt(0, 23)} " +
                        "${Random.nextInt(1, 31)} " +
                        "${Random.nextInt(1, 12)} " +
                        "? " +
                        "${Random.nextInt(1970, 2099)}"
            )
            put(TITLE_COLUMN_NAME, "Test title no ${index + 1}")
            put(TEXT_COLUMN_NAME, "Test text no ${index + 1}")
            if (dbVersion >= 5) put(TIME_DISPLAY_COLUMN_NAME, true)
            if (dbVersion >= 4) put(ON_CLICK_URI_COLUMN_NAME, "https://www.google.de/")
            put(NEXT_COLUMN_NAME, LocalDateTime.now().format(DATE_TIME_FORMATTER))
            if (dbVersion >= 2) put(ENABLED_COLUMN_NAME, Random.nextBoolean())
            if (dbVersion >= 3) put(POSITION_COLUMN_NAME, index)
        }
    }

    private fun validateData(expectedValues: List<ContentValues>, cursor: Cursor) {
        val idColumn = cursor.getColumnIndexOrThrow(ID_COLUMN_NAME)
        val cronColumn = cursor.getColumnIndexOrThrow(CRON_COLUMN_NAME)
        val titleColumn = cursor.getColumnIndexOrThrow(TITLE_COLUMN_NAME)
        val textColumn = cursor.getColumnIndexOrThrow(TEXT_COLUMN_NAME)
        val timeDisplayColumn = cursor.getColumnIndex(TIME_DISPLAY_COLUMN_NAME)
        val onClickUriColumn = cursor.getColumnIndex(ON_CLICK_URI_COLUMN_NAME)
        val nextColumn = cursor.getColumnIndexOrThrow(NEXT_COLUMN_NAME)
        val enabledColumn = cursor.getColumnIndex(ENABLED_COLUMN_NAME)
        val positionColumn = cursor.getColumnIndex(POSITION_COLUMN_NAME)
        var i = 0
        while (cursor.moveToNext()) {
            assertEquals(expectedValues[i].get(ID_COLUMN_NAME), cursor.getLong(idColumn))
            assertEquals(expectedValues[i].get(CRON_COLUMN_NAME), cursor.getString(cronColumn))
            assertEquals(expectedValues[i].get(TITLE_COLUMN_NAME), cursor.getString(titleColumn))
            assertEquals(expectedValues[i].get(TEXT_COLUMN_NAME), cursor.getString(textColumn))
            if (timeDisplayColumn != -1) {
                // 'time_display' is set to true by default
                if (expectedValues[i].get(TIME_DISPLAY_COLUMN_NAME) == null) {
                    assertTrue(cursor.getInt(timeDisplayColumn) > 0)
                } else {
                    assertEquals(
                        expectedValues[i].get(TIME_DISPLAY_COLUMN_NAME),
                        cursor.getInt(timeDisplayColumn) > 0
                    )
                }
            }
            if (onClickUriColumn != -1) {
                // 'on_click_uri' is set to empty string by default
                if (expectedValues[i].get(ON_CLICK_URI_COLUMN_NAME) == null) {
                    assertEquals("", cursor.getString(onClickUriColumn))
                } else {
                    assertEquals(
                        expectedValues[i].get(ON_CLICK_URI_COLUMN_NAME),
                        cursor.getString(onClickUriColumn)
                    )
                }
            }
            assertEquals(expectedValues[i].get(NEXT_COLUMN_NAME), cursor.getString(nextColumn))
            if (enabledColumn != -1) {
                // 'enabled' is set to true by default
                if (expectedValues[i].get(ENABLED_COLUMN_NAME) == null) {
                    assertTrue(cursor.getInt(enabledColumn) > 0)
                } else {
                    assertEquals(
                        expectedValues[i].get(ENABLED_COLUMN_NAME),
                        cursor.getInt(enabledColumn) > 0
                    )
                }
            }
            if (positionColumn != -1) {
                // 'position' is set to id by default when migrating database
                if (expectedValues[i].get(POSITION_COLUMN_NAME) == null) {
                    assertEquals(
                        expectedValues[i].get(ID_COLUMN_NAME),
                        cursor.getLong(positionColumn)
                    )
                } else {
                    assertEquals(
                        expectedValues[i].get(POSITION_COLUMN_NAME),
                        cursor.getLong(positionColumn)
                    )
                }
            }
            i++
        }
    }

    private fun validateMigrations(firstVersion: Int, secondVersion: Int, vararg migrations: Migration) {
        var db: SupportSQLiteDatabase = testHelper.createDatabase(DB_NAME, firstVersion)
        val testValues = createTestData(TEST_DATA_SIZE, firstVersion)
        insertTestData(db, testValues)
        var cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
        db.close()
        db = testHelper.runMigrationsAndValidate(
            DB_NAME, secondVersion, true,
            *migrations
        )
        cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
    }

    @Test
    fun validateMigrations1_2() {
        validateMigrations(1, 2, AppDatabase.MIGRATION_1_2)
    }

    @Test
    fun validateMigrations1_3() {
        validateMigrations(1, 3, AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
    }

    @Test
    fun validateMigrations2_3() {
        validateMigrations(2, 3, AppDatabase.MIGRATION_2_3)
    }

    @Test
    fun validateMigrations3_4() {
        validateMigrations(3, 4, AppDatabase.MIGRATION_3_4)
    }

    @Test
    fun validateMigration4_5() {
        validateMigrations(4, 5, AppDatabase.MIGRATION_4_5)
    }
}
