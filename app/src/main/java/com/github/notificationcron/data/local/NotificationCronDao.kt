package com.github.notificationcron.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.notificationcron.data.model.NotificationCron

@Dao
interface NotificationCronDao {

    @Query("SELECT * FROM notification_cron")
    fun findAll(): List<NotificationCron>

    @Query("SELECT * FROM notification_cron ORDER BY id")
    fun findAllOrderedAndLive(): LiveData<List<NotificationCron>>

    @Query("SELECT * FROM notification_cron WHERE id=:notificationCronId")
    fun findById(notificationCronId: Long): NotificationCron

    @Insert
    fun insert(notificationCron: NotificationCron): Long

    @Update
    fun update(notificationCron: NotificationCron)

    @Delete
    fun delete(notificationCron: NotificationCron)
}