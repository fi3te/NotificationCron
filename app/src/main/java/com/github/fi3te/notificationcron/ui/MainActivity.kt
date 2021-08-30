package com.github.fi3te.notificationcron.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fi3te.notificationcron.R
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import com.github.fi3te.notificationcron.databinding.ActivityMainBinding
import com.github.fi3te.notificationcron.ui.licenses.LicensesActivity
import com.github.fi3te.notificationcron.ui.settings.SettingsActivity
import com.github.fi3te.notificationcron.ui.settings.loadTheme
import java.util.*

class MainActivity : AppCompatActivity(), NotificationCronAdapter.ViewListener {

    private lateinit var notificationCronViewModel: NotificationCronViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationCronAdapter: NotificationCronAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        loadTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        layoutManager = LinearLayoutManager(this)
        notificationCronAdapter = NotificationCronAdapter(Collections.emptyList(), this)

        recyclerView = findViewById(R.id.cronRecyclerView)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = notificationCronAdapter
        val itemTouchHelper = ItemTouchHelper(NotificationCronDragCallback(notificationCronAdapter))
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )

        notificationCronViewModel =
            ViewModelProvider(this).get(NotificationCronViewModel::class.java)
        notificationCronViewModel.allNotificationCrons.observe(
            this,
            { notificationCrons ->
                notificationCronAdapter.setData(notificationCrons)
            })

        binding.addButton.setOnClickListener {
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
            R.id.settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.repairSchedule -> {
                notificationCronViewModel.repairSchedule(this)
                return true
            }
            R.id.licenses -> {
                startActivity(Intent(this, LicensesActivity::class.java))
                return true
            }
            R.id.imprint -> {
                showImprintDialog(this)
                return true
            }
            R.id.help -> {
                showHelpDialog(this)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun testNotificationCron(notificationCron: NotificationCron) {
        showNotification(this, notificationCron)
    }

    override fun enableNotificationCron(notificationCron: NotificationCron) {
        notificationCron.enabled = true
        notificationCronViewModel.update(this, notificationCron)
    }

    override fun disableNotificationCron(notificationCron: NotificationCron) {
        notificationCron.enabled = false
        notificationCronViewModel.update(this, notificationCron)
    }

    override fun editNotificationCron(notificationCron: NotificationCron) {
        showUpdateDialog(this, notificationCron) {
            notificationCronViewModel.update(this, it)
        }
    }

    override fun deleteNotificationCron(notificationCron: NotificationCron) {
        showDeleteDialog(this) {
            notificationCronViewModel.delete(this, notificationCron)
        }
    }

    override fun moveNotificationCrons(notificationCrons: List<NotificationCron>) {
        notificationCronViewModel.updateAfterMove(notificationCrons)
    }
}
