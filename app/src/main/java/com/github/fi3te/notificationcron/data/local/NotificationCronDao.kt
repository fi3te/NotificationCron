package com.github.fi3te.notificationcron.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.github.fi3te.notificationcron.data.model.db.NotificationCron

@Dao
interface NotificationCronDao {

    @Query("SELECT * FROM notification_cron")
    fun findAll(): List<NotificationCron>

    @Query("SELECT * FROM notification_cron ORDER BY position")
    fun findAllOrderedAndLive(): LiveData<List<NotificationCron>>

    @Query("SELECT * FROM notification_cron WHERE id=:notificationCronId")
    fun findById(notificationCronId: Long): NotificationCron

    @Query("SELECT IFNULL(MAX(position) + 1, 0) FROM notification_cron")
    fun getNextPosition(): Long

    @Query("SELECT * FROM notification_cron WHERE position=:notificationCronPosition")
    fun findByPosition(notificationCronPosition: Long): NotificationCron

    @Insert
    fun insert(notificationCron: NotificationCron): Long

    @Update
    fun update(notificationCron: NotificationCron)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(notificationCrons: List<NotificationCron>)

    @Delete
    fun delete(notificationCron: NotificationCron)
}