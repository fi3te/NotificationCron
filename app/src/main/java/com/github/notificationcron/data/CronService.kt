package com.github.notificationcron.data

import com.cronutils.descriptor.CronDescriptor
import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import com.github.notificationcron.data.model.NotificationCron
import java.time.ZonedDateTime
import java.util.*

fun createQuartzCronParser(): CronParser {
    val cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)
    return CronParser(cronDefinition)
}

fun parseCron(cronString: String): Cron {
    val cronParser = createQuartzCronParser()
    return cronParser.parse(cronString).validate()
}

fun makeCronHumanReadable(cronString: String, locale: Locale): String {
    val cron = parseCron(cronString)
    val descriptor = CronDescriptor.instance(locale)
    return descriptor.describe(cron)
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

fun cronIntervalIsBigEnough(cronString: String): Boolean {
    val cronComponents = cronString.split(" ")
    return cronComponents.isNotEmpty() && !cronComponents[0].contains("*")
}