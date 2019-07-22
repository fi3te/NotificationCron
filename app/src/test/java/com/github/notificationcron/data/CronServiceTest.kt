package com.github.notificationcron.data

import org.junit.Test
import com.google.common.truth.Truth.assertThat

class CronServiceTest {

    @Test
    fun testCronIntervalIsBigEnough() {
        assertThat(cronIntervalIsBigEnough("0 0 0 22 JUL ? 2019")).isTrue()
        assertThat(cronIntervalIsBigEnough("0 0 0 22 JUL ? *")).isTrue()
        assertThat(cronIntervalIsBigEnough("0 0 0 22 * ? *")).isTrue()
        assertThat(cronIntervalIsBigEnough("0 0 0 * * ? *")).isTrue()
        assertThat(cronIntervalIsBigEnough("0 0 0 ? * * *")).isTrue()
        assertThat(cronIntervalIsBigEnough("0 0 * * * ? *")).isTrue()
        assertThat(cronIntervalIsBigEnough("0 * * * * ? *")).isTrue()
        assertThat(cronIntervalIsBigEnough("* * * * * ? *")).isFalse()
    }
}