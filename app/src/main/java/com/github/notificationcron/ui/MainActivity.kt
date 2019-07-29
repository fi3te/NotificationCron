package com.github.notificationcron.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.notificationcron.R
import com.github.notificationcron.data.*
import com.github.notificationcron.data.model.NotificationCron

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationCronAdapter: NotificationCronAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        layoutManager = LinearLayoutManager(this)
        notificationCronAdapter = NotificationCronAdapter(Collections.emptyList())

        recyclerView = findViewById(R.id.cronRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = notificationCronAdapter
        recyclerView.addItemDecoration(DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL))

        val notificationCronViewModel = ViewModelProviders.of(this).get(NotificationCronViewModel::class.java)
        notificationCronViewModel.allNotificationCrons.observe(this, Observer<List<NotificationCron>> { notificationCrons ->
            notificationCronAdapter.setData(notificationCrons)
        })








        saveButton.setOnClickListener {
            val cronString = cronInput.text.toString()
            val notificationTitle = notificationTitleInput.text.toString()
            val notificationText = notificationTextInput.text.toString()

            val alertText = try {
                var result = makeCronHumanReadable(cronString, Locale.US)
                // TODO remove demo

                if (cronIntervalIsBigEnough(cronString)) {
                    val notificationCron = NotificationCron(cron = cronString, notificationTitle = notificationTitle, notificationText = notificationText)
                    computeNextExecution(notificationCron)
                    notificationCronViewModel.insert(notificationCron)
                } else {
                    result = "cron interval is not big enough"
                }
                result
            } catch (e: IllegalArgumentException) {
                getString(R.string.invalid_cron_entered)
            }

            Snackbar.make(it, alertText, Snackbar.LENGTH_LONG).show()
        }

        startButton.setOnClickListener {
            notificationCronViewModel.scheduleAlarms(this)
            Snackbar.make(it, "Started", Snackbar.LENGTH_LONG).show()
        }

        stopButton.setOnClickListener {
            notificationCronViewModel.removeAlarms(this)
            Snackbar.make(it, "Stopped", Snackbar.LENGTH_LONG).show()
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
