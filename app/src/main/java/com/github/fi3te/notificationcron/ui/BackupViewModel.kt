package com.github.fi3te.notificationcron.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.fi3te.notificationcron.data.local.AppDatabase
import com.github.fi3te.notificationcron.data.local.NotificationCronDao
import com.github.fi3te.notificationcron.data.local.SettingsDao
import com.github.fi3te.notificationcron.data.model.db.Database
import com.github.fi3te.notificationcron.data.remote.BackupJsonService
import com.github.fi3te.notificationcron.data.remote.copyFile
import com.github.fi3te.notificationcron.data.remote.map
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class BackupViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationCronDao: NotificationCronDao
    private val settingsDao: SettingsDao
    private val backupJsonService: BackupJsonService
    var backupRunning = false

    init {
        val database = AppDatabase.getDatabase(application)
        notificationCronDao = database.notificationCronDao()
        settingsDao = SettingsDao(application)
        backupJsonService = BackupJsonService()
    }

    fun createBackup(uri: Uri): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            backupRunning = true

            val notificationCrons = notificationCronDao.findAll()
            val settings = settingsDao.readSettings()
            val database = Database(notificationCrons, settings)
            val backup = map(database)
            val json = backupJsonService.writeJson(backup)
            val successful = try {
                val tmpFile = File.createTempFile("Backup", null)
                tmpFile.writeText(json)
                copyFile(tmpFile, uri, getApplication())
            } catch (e: IOException) {
                false
            }

            backupRunning = false
            result.postValue(successful)
        }
        return result
    }

    fun restoreBackup(uri: Uri): LiveData<Boolean> {
        val result = MutableLiveData<Boolean>()
        viewModelScope.launch(Dispatchers.IO) {
            backupRunning = true

            var tmpFile: File? = null
            var successful = try {
                tmpFile = File.createTempFile("Backup", null)
                copyFile(uri, tmpFile, getApplication())
            } catch (e: IOException) {
                false
            }

            if (successful) {
                val json = tmpFile!!.readText()
                val backup = try {
                    backupJsonService.readJson(json)
                } catch (e: JsonDataException) {
                    null
                }
                if (backup == null) {
                    successful = false
                } else {
                    val database = map(backup)
                    settingsDao.writeSettings(database.settings)
                    notificationCronDao.deleteAll()
                    notificationCronDao.insertAll(database.notificationCrons)
                }
            }

            backupRunning = false
            result.postValue(successful)
        }
        return result
    }
}