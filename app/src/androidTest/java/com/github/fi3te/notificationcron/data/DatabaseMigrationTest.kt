package com.github.fi3te.notificationcron.data

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.github.fi3te.notificationcron.data.local.AppDatabase
import org.junit.Assert.assertEquals
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
private const val NEXT_COLUMN_NAME = "next_notification"
private const val ENABLED_COLUMN_NAME = "enabled"
private const val POSITION_COLUMN_NAME = "position"
private const val SELECT_ALL_QUERY = "SELECT * FROM $TABLE_NAME ORDER BY $ID_COLUMN_NAME"
private const val TEST_DATA_SIZE = 500

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {

    @Rule @JvmField
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
            put(CRON_COLUMN_NAME,
                "${Random.nextInt(0, 59)} " +
                "${Random.nextInt(0, 59)} " +
                "${Random.nextInt(0, 23)} " +
                "${Random.nextInt(1, 31)} " +
                "${Random.nextInt(1, 12)} " +
                "? " +
                "${Random.nextInt(1970, 2099)}")
            put(TITLE_COLUMN_NAME, "Test title no ${index + 1}")
            put(TEXT_COLUMN_NAME, "Test text no ${index + 1}")
            put(NEXT_COLUMN_NAME, LocalDateTime.now().format(DATE_TIME_FORMATTER))
            if (dbVersion >= 2) put(ENABLED_COLUMN_NAME, Random.nextBoolean())
            if (dbVersion >= 3) put(POSITION_COLUMN_NAME, index)
        }
    }

    private fun validateData(expectedValues: List<ContentValues>, cursor: Cursor) {
        val ID_COLUMN = cursor.getColumnIndexOrThrow(ID_COLUMN_NAME)
        val CRON_COLUMN = cursor.getColumnIndexOrThrow(CRON_COLUMN_NAME)
        val TITLE_COLUMN = cursor.getColumnIndexOrThrow(TITLE_COLUMN_NAME)
        val TEXT_COLUMN = cursor.getColumnIndexOrThrow(TEXT_COLUMN_NAME)
        val NEXT_COLUMN = cursor.getColumnIndexOrThrow(NEXT_COLUMN_NAME)
        val ENABLED_COLUMN = cursor.getColumnIndex(ENABLED_COLUMN_NAME)
        val POSITION_COLUMN = cursor.getColumnIndex(POSITION_COLUMN_NAME)
        var i = 0
        while (cursor.moveToNext()) {
            assertEquals(expectedValues[i].get(ID_COLUMN_NAME), cursor.getLong(ID_COLUMN))
            assertEquals(expectedValues[i].get(CRON_COLUMN_NAME), cursor.getString(CRON_COLUMN))
            assertEquals(expectedValues[i].get(TITLE_COLUMN_NAME), cursor.getString(TITLE_COLUMN))
            assertEquals(expectedValues[i].get(TEXT_COLUMN_NAME), cursor.getString(TEXT_COLUMN))
            assertEquals(expectedValues[i].get(NEXT_COLUMN_NAME), cursor.getString(NEXT_COLUMN))
            if (ENABLED_COLUMN != -1) {
                // Enabled is set to true by default in the database
                val expectEnabled = expectedValues[i].get(ENABLED_COLUMN_NAME)
                if (expectEnabled == false) {
                    assertEquals(false, cursor.getInt(ENABLED_COLUMN) > 0)
                } else {
                    assertEquals(true, cursor.getInt(ENABLED_COLUMN) > 0)
                }
            }
            if (POSITION_COLUMN != -1) {
                // Position is set to id by default when migrating database
                if (expectedValues[i].get(POSITION_COLUMN_NAME) == null) {
                    assertEquals(expectedValues[i].get(ID_COLUMN_NAME), cursor.getLong(POSITION_COLUMN))
                } else {
                    assertEquals(expectedValues[i].get(POSITION_COLUMN_NAME), cursor.getLong(POSITION_COLUMN))
                }
            }
            i++
        }
    }

    @Test
    fun validateMigrations1_2() {
        var db: SupportSQLiteDatabase = testHelper.createDatabase(DB_NAME, 1)
        val testValues = createTestData(TEST_DATA_SIZE, 1)
        insertTestData(db, testValues)
        var cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
        db.close()
        db = testHelper.runMigrationsAndValidate(DB_NAME, 2, true,
            AppDatabase.MIGRATION_1_2)
        cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
    }

    @Test
    fun validateMigrations2_3() {
        var db: SupportSQLiteDatabase = testHelper.createDatabase(DB_NAME, 2)
        val testValues = createTestData(TEST_DATA_SIZE, 2)
        insertTestData(db, testValues)
        var cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
        db.close()
        db = testHelper.runMigrationsAndValidate(DB_NAME, 3, true,
            AppDatabase.MIGRATION_2_3)
        cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
    }

    @Test
    fun validateMigrations1_3() {
        var db: SupportSQLiteDatabase = testHelper.createDatabase(DB_NAME, 1)
        val testValues = createTestData(TEST_DATA_SIZE, 1)
        insertTestData(db, testValues)
        var cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
        db.close()
        db = testHelper.runMigrationsAndValidate(DB_NAME, 3, true,
            AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
        cursor = db.query(SELECT_ALL_QUERY)
        validateData(testValues, cursor)
    }
}
