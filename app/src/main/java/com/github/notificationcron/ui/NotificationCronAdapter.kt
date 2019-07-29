package com.github.notificationcron.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.notificationcron.R
import com.github.notificationcron.data.DATE_TIME_FORMATTER
import com.github.notificationcron.data.model.NotificationCron

class NotificationCronAdapter(private var data: List<NotificationCron>, private val buttonListener: ButtonListener) :
    RecyclerView.Adapter<NotificationCronAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cronText: TextView = view.findViewById(R.id.cronText)
        val notificationTitleText: TextView = view.findViewById(R.id.notificationTitleText)
        val notificationTextText: TextView = view.findViewById(R.id.notificationTextText)
        val nextNotificationText: TextView = view.findViewById(R.id.nextNotificationText)
        val deleteNotificationCronButton: ImageButton = view.findViewById(R.id.deleteNotificationCronButton)
    }

    interface ButtonListener {
        fun deleteNotificationCron(notificationCron: NotificationCron)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val notificationCronItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_cron_item, parent, false)
        val viewHolder = ViewHolder(notificationCronItem)
        viewHolder.deleteNotificationCronButton.setOnClickListener {
            val notificationCron = data[viewHolder.adapterPosition]
            buttonListener.deleteNotificationCron(notificationCron)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationCron = data[position]
        holder.cronText.text = notificationCron.cron
        holder.notificationTitleText.text = notificationCron.notificationTitle
        holder.notificationTextText.text = notificationCron.notificationText
        holder.nextNotificationText.text = notificationCron.nextNotification?.format(DATE_TIME_FORMATTER)
            ?: holder.itemView.resources.getString(R.string.no_next_notification)
    }

    override fun getItemCount() = data.size

    fun setData(data: List<NotificationCron>) {
        this.data = data
        notifyDataSetChanged()
    }
}