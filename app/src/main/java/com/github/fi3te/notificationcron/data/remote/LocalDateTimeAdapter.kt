package com.github.fi3te.notificationcron.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime

class LocalDateTimeAdapter {
    @ToJson fun toJson(localDateTime: LocalDateTime?): String? {
        return localDateTime?.toString()
    }

    @FromJson fun fromJson(json: String?): LocalDateTime? {
        return json?.let { LocalDateTime.parse(json) }
    }
}