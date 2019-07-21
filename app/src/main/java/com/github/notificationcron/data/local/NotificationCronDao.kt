package com.github.notificationcron.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.notificationcron.data.model.NotificationCron

@Dao
interface NotificationCronDao {

    @Query("SELECT * FROM notification_cron ORDER BY id")
    fun findAll(): LiveData<List<NotificationCron>>

    @Query("SELECT * FROM notification_cron WHERE id IN (:notificationCronIds)")
    fun findAllByIds(notificationCronIds: IntArray): List<NotificationCron>

    @Insert
    fun insertAll(vararg notificationCrons: NotificationCron)

    @Delete
    fun delete(notificationCron: NotificationCron)

    @Query("DELETE FROM notification_cron WHERE id=:notificationCronId")
    fun deleteById(notificationCronId: Int)
}