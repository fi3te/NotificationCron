package com.github.notificationcron.ui

import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.github.notificationcron.R
import com.github.notificationcron.data.makeCronHumanReadable
import com.github.notificationcron.data.model.NotificationCron

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.time.LocalDateTime
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val notificationCronViewModel = ViewModelProviders.of(this).get(NotificationCronViewModel::class.java)

        val notificationCronObserver = Observer<List<NotificationCron>> { notificationCrons ->
            Log.i("MainActivity", "" + notificationCrons.size)
        }
        notificationCronViewModel.allNotificationCrons.observe(this, notificationCronObserver)

        fab.setOnClickListener { view ->
            val cronString = cronText.text.toString()

            val alertText = try {
                val result = makeCronHumanReadable(cronString, Locale.US)
                // TODO remove demo
                notificationCronViewModel.insert(NotificationCron(cron = cronString, nextNotification = LocalDateTime.now()))
                result
            } catch (e: IllegalArgumentException) {
                getString(R.string.invalid_cron_entered)
            }

            Snackbar.make(view, alertText, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
