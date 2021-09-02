package com.github.fi3te.notificationcron.data.remote

import com.github.fi3te.notificationcron.data.model.backup.Backup
import com.github.fi3te.notificationcron.data.model.backup.BackupV00500
import com.github.fi3te.notificationcron.data.model.backup.V00500
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory


class BackupJsonService {

    private val jsonAdapter: JsonAdapter<Backup>

    init {
        val moshi = Moshi.Builder()
            .add(LocalDateTimeAdapter())
            .add(
                PolymorphicJsonAdapterFactory.of(Backup::class.java, "version")
                    .withSubtype(BackupV00500::class.java, V00500)
            )
            .build()

        jsonAdapter = moshi.adapter(Backup::class.java)
    }

    @Throws(JsonDataException::class)
    fun readJson(json: String): Backup? {
        return jsonAdapter.fromJson(json)
    }

    fun writeJson(obj: Backup): String {
        return jsonAdapter.toJson(obj)
    }
}