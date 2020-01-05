package com.github.fi3te.notificationcron.ui

import android.content.res.Resources
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.fi3te.notificationcron.R
import com.github.fi3te.notificationcron.data.DATE_TIME_FORMATTER
import com.github.fi3te.notificationcron.data.model.NotificationCron

class NotificationCronAdapter(private var data: List<NotificationCron>, private val buttonListener: ButtonListener) :
    RecyclerView.Adapter<NotificationCronAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cronText: TextView = view.findViewById(R.id.cronText)
        val notificationTitleText: TextView = view.findViewById(R.id.notificationTitleText)
        val notificationTextText: TextView = view.findViewById(R.id.notificationTextText)
        val nextNotificationText: TextView = view.findViewById(R.id.nextNotificationText)
        val notificationCronOptionsButton: ImageButton = view.findViewById(R.id.notificationCronOptionsButton)
    }

    interface ButtonListener {
        fun testNotificationCron(notificationCron: NotificationCron)
        fun enableNotificationCron(notificationCron: NotificationCron)
        fun disableNotificationCron(notificationCron: NotificationCron)
        fun editNotificationCron(notificationCron: NotificationCron)
        fun deleteNotificationCron(notificationCron: NotificationCron)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val notificationCronItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_cron_item, parent, false)
        val viewHolder = ViewHolder(notificationCronItem)
        viewHolder.notificationCronOptionsButton.setOnClickListener {
            val notificationCron = data[viewHolder.adapterPosition]
            PopupMenu(it.context, it, Gravity.END).apply {
                menu.add(Menu.NONE, 1, Menu.NONE, R.string.test)
                if (!notificationCron.enabled) {
                    menu.add(Menu.NONE, 2, Menu.NONE, R.string.enable)
                } else {
                    menu.add(Menu.NONE, 3, Menu.NONE, R.string.disable)
                }
                menu.add(Menu.NONE, 4, Menu.NONE, R.string.edit)
                menu.add(Menu.NONE, 5, Menu.NONE, R.string.delete)

                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        1 -> buttonListener.testNotificationCron(notificationCron)
                        2 -> buttonListener.enableNotificationCron(notificationCron)
                        3 -> buttonListener.disableNotificationCron(notificationCron)
                        4 -> buttonListener.editNotificationCron(notificationCron)
                        5 -> buttonListener.deleteNotificationCron(notificationCron)
                    }
                    true
                }
            }.show()
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationCron = data[position]
        holder.apply {
            cronText.text = notificationCron.cron
            notificationTitleText.text = notificationCron.notificationTitle
            notificationTextText.text = notificationCron.notificationText
            nextNotificationText.text = getNextNotificationText(notificationCron, itemView.resources)
        }
    }

    override fun getItemCount() = data.size

    fun setData(data: List<NotificationCron>) {
        this.data = data
        notifyDataSetChanged()
    }

    private fun getNextNotificationText(notificationCron: NotificationCron, resources: Resources): String {
        return if (!notificationCron.enabled) {
            resources.getString(R.string.disabled)
        } else {
            notificationCron.nextNotification?.format(DATE_TIME_FORMATTER)
                ?: resources.getString(R.string.no_next_notification)
        }
    }
}