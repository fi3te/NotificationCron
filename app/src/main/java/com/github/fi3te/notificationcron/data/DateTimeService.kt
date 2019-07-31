@file:Suppress("SpellCheckingInspection")

package com.github.fi3te.notificationcron.data

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")
val TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

fun getDayAndTimeString(): String {
    return getDayAndTimeString(LocalDateTime.now())
}

fun getDayAndTimeString(localDateTime: LocalDateTime): String {
    return localDateTime.format(DateTimeFormatter.ofPattern("ddHHmmss"))
}