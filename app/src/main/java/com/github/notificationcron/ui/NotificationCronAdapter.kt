package com.github.notificationcron.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.notificationcron.R
import com.github.notificationcron.data.model.NotificationCron

class NotificationCronAdapter(private var data: List<NotificationCron>) :
    RecyclerView.Adapter<NotificationCronAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cronText: TextView = view.findViewById(R.id.cronText)
        val notificationTitleText: TextView = view.findViewById(R.id.notificationTitleText)
        val notificationTextText: TextView = view.findViewById(R.id.notificationTextText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val notificationCronItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_cron_item, parent, false)
        return ViewHolder(notificationCronItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notificationCron = data[position]
        holder.cronText.text = notificationCron.cron
        holder.notificationTitleText.text = notificationCron.notificationTitle
        holder.notificationTextText.text = notificationCron.notificationText
    }

    override fun getItemCount() = data.size

    fun setData(data: List<NotificationCron>) {
        this.data = data
        notifyDataSetChanged()
    }
}