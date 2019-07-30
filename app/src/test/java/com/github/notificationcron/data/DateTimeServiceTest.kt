package com.github.notificationcron.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDateTime

class DateTimeServiceTest {

    @Test
    fun testGetDayAndTimeStringAsInt() {
        assertThat(getDayAndTimeString(LocalDateTime.parse("2019-07-01T21:34:15"))).isEqualTo("01213415")
        assertThat(getDayAndTimeString(LocalDateTime.parse("2019-07-31T21:34:15"))).isEqualTo("31213415")
    }
}
