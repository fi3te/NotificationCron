package com.github.notificationcron.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NotificationServiceTest {

    @Test
    fun testNewNotificationId() {
        for (e in 1..3) {
            for (i in 1..21) {
                assertThat("${newNotificationId()}").startsWith("$i")
            }
        }
    }
}