package com.github.fi3te.notificationcron.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.fi3te.notificationcron.data.scheduleAlarms

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            scheduleAlarms(context)
        }
    }
}