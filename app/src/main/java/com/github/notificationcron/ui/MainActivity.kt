package com.github.notificationcron.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.notificationcron.R
import com.github.notificationcron.data.model.NotificationCron
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NotificationCronAdapter.ButtonListener {

    private lateinit var notificationCronViewModel: NotificationCronViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationCronAdapter: NotificationCronAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        layoutManager = LinearLayoutManager(this)
        notificationCronAdapter = NotificationCronAdapter(Collections.emptyList(), this)

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
            showCreateDialog(this) {
                notificationCronViewModel.create(this, it)
            }
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
            R.id.repairSchedule -> {
                notificationCronViewModel.repairSchedule(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun editNotificationCron(notificationCron: NotificationCron) {
        showUpdateDialog(this, notificationCron) {
            notificationCronViewModel.update(this, it)
        }
    }

    override fun deleteNotificationCron(notificationCron: NotificationCron) {
        notificationCronViewModel.delete(this, notificationCron)
    }
}
