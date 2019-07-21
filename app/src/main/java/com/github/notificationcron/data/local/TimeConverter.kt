package com.github.notificationcron.data.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

class TimeConverter {

    @TypeConverter
    fun timestampToLocalDateTime(value: Long?): LocalDateTime? {
        return value?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC)
        }
    }

    @TypeConverter
    fun localDateTimeToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }
}