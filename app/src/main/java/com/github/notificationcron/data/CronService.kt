package com.github.notificationcron.data

import com.cronutils.descriptor.CronDescriptor
import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
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