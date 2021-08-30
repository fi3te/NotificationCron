package com.github.fi3te.notificationcron.data.remote

import com.github.fi3te.notificationcron.data.model.backup.BackupV005
import com.github.fi3te.notificationcron.data.model.backup.v005.NotificationCron
import com.github.fi3te.notificationcron.data.model.backup.v005.Settings
import com.squareup.moshi.JsonDataException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class BackupJsonServiceTest {

    companion object {
        private const val BACKUP = "backup.json"
        private const val BACKUP_INCOMPLETE_DATA = "backup_incomplete_data.json"
        private const val BACKUP_UNKNOWN_VERSION = "backup_unknown_version.json"
        private const val BACKUP_MINIFIED = "backup_minified.json"
    }

    @Test
    fun testReadJson() {
        val service = BackupJsonService()
        val json = readJsonFile(BACKUP)

        val backup = service.readJson(json)

        assertEquals("5", backup?.version)
        assertTrue(backup is BackupV005)
        if (backup is BackupV005) {
            assertEquals(1, backup.notificationCrons.size)
            assertEquals(5, backup.settings.displayDurationInSeconds)
        }
    }

    @Test(expected = JsonDataException::class)
    fun testReadJsonWithIncompleteData() {
        val service = BackupJsonService()
        val json = readJsonFile(BACKUP_INCOMPLETE_DATA)

        service.readJson(json)
    }

    @Test(expected = JsonDataException::class)
    fun testReadJsonWithUnknownVersion() {
        val service = BackupJsonService()
        val json = readJsonFile(BACKUP_UNKNOWN_VERSION)

        service.readJson(json)
    }

    @Test
    fun testWriteJson() {
        val service = BackupJsonService()
        val backup = BackupV005(
            listOf(
                NotificationCron(
                    "0 0 7-22 * * ?", "title", "text", true,
                    "", LocalDateTime.of(
                        LocalDate.of(2022, 1, 1), LocalTime.of(0, 0)
                    ), true, null
                )
            ), Settings(1, true, 5)
        )

        val json = service.writeJson(backup)

        assertEquals(readJsonFile(BACKUP_MINIFIED), json)
    }

    private fun readJsonFile(fileName: String): String {
        return javaClass.getResource("/$fileName")!!.readText()
    }
}