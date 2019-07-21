package com.github.notificationcron.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.notificationcron.data.local.AppDatabase
import com.github.notificationcron.data.local.NotificationCronDao
import com.github.notificationcron.data.model.NotificationCron
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationCronViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationCronDao: NotificationCronDao

    val allNotificationCrons: LiveData<List<NotificationCron>>

    init {
        val database = AppDatabase.getDatabase(application)
        notificationCronDao = database.notificationCronDao()
        allNotificationCrons = notificationCronDao.findAll()
    }

    fun insert(notificationCron: NotificationCron) = viewModelScope.launch(Dispatchers.IO) {
        notificationCronDao.insertAll(notificationCron)
    }
}