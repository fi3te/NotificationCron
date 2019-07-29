package com.github.notificationcron.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.notificationcron.R
import com.github.notificationcron.data.computeNextExecution
import com.github.notificationcron.data.model.NotificationCron
import com.github.notificationcron.data.parseCron
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var notificationCronViewModel: NotificationCronViewModel
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

        notificationCronViewModel = ViewModelProviders.of(this).get(NotificationCronViewModel::class.java)
        notificationCronViewModel.allNotificationCrons.observe(
            this,
            Observer<List<NotificationCron>> { notificationCrons ->
                notificationCronAdapter.setData(notificationCrons)
            })

        addButton.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.create_scheduled_notifications)

                customView(R.layout.dialog_add_notification_cron)
                val customView = getCustomView()
                val cronInput = customView.findViewById<EditText>(R.id.cronInput)
                val notificationTitleInput = customView.findViewById<EditText>(R.id.notificationTitleInput)
                val notificationTextInput = customView.findViewById<EditText>(R.id.notificationTextInput)
                cronInput.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(p0: Editable?) {
                        val cronString = cronInput.text.toString()
                        try {
                            parseCron(cronString)
                            setActionButtonEnabled(WhichButton.POSITIVE, true)
                        } catch (e: IllegalArgumentException) {
                            setActionButtonEnabled(WhichButton.POSITIVE, false)
                        }
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }
                })

                positiveButton(R.string.create) {
                    val notificationCron = NotificationCron(
                        cron = cronInput.text.toString(),
                        notificationTitle = notificationTitleInput.text.toString(),
                        notificationText = notificationTextInput.text.toString()
                    )
                    createNotificationCron(notificationCron)
                }
                negativeButton(R.string.cancel)
                setActionButtonEnabled(WhichButton.POSITIVE, false)
            }
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

    fun createNotificationCron(notificationCron: NotificationCron) {
        computeNextExecution(notificationCron)
        notificationCronViewModel.insert(notificationCron)
    }
}
