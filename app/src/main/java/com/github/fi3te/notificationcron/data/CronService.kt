package com.github.fi3te.notificationcron.data

import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import java.time.ZonedDateTime
import java.util.*

fun createQuartzCronParser(): CronParser {
    val cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)
    return CronParser(cronDefinition)
}

fun parseCron(cronString: String): Cron {
    val cronParser = createQuartzCronParser()
    return try {
        cronParser.parse(cronString).validate()
    } catch (e: Exception) {
        throw IllegalArgumentException("Invalid cron expression")
    }
}

fun isCronValid(cronString: String): Boolean {
    return try {
        parseCron(cronString)
        true
    } catch (e: IllegalArgumentException) {
        false
    }
}

fun computeNextExecution(cron: Cron, now: ZonedDateTime): Optional<ZonedDateTime> {
    val executionTime = ExecutionTime.forCron(cron)
    return executionTime.nextExecution(now)
}

fun computeNextExecution(notificationCron: NotificationCron) {
    val nextExecution =
        computeNextExecution(parseCron(notificationCron.cron), ZonedDateTime.now())
    notificationCron.nextNotification = if (nextExecution.isPresent) {
        nextExecution.get().toLocalDateTime()
    } else {
        null
    }
}
