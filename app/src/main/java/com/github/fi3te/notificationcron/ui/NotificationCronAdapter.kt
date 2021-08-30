package com.github.fi3te.notificationcron.ui

import android.content.res.Resources
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.RecyclerView
import com.github.fi3te.notificationcron.R
import com.github.fi3te.notificationcron.data.DATE_TIME_FORMATTER
import com.github.fi3te.notificationcron.data.model.db.NotificationCron
import java.util.*

class NotificationCronAdapter(private var data: List<NotificationCron>, private val viewListener: ViewListener) :
    RecyclerView.Adapter<NotificationCronAdapter.ViewHolder>(), NotificationCronDragCallback.DragListener {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cronText: TextView = view.findViewById(R.id.cronText)
        val notificationTitleText: TextView = view.findViewById(R.id.notificationTitleText)
        val notificationTextText: TextView = view.findViewById(R.id.notificationTextText)
        val onClickUriText: TextView = view.findViewById(R.id.onClickUriText)
        val nextNotificationText: TextView = view.findViewById(R.id.nextNotificationText)
        val notificationCronOptionsButton: ImageButton = view.findViewById(R.id.notificationCronOptionsButton)
    }

    interface ViewListener {
        fun testNotificationCron(notificationCron: NotificationCron)
        fun enableNotificationCron(notificationCron: NotificationCron)
        fun disableNotificationCron(notificationCron: NotificationCron)
        fun editNotificationCron(notificationCron: NotificationCron)
        fun deleteNotificationCron(notificationCron: NotificationCron)
        fun moveNotificationCrons(notificationCrons: List<NotificationCron>)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val notificationCronItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.notification_cron_item, parent, false)
        val viewHolder = ViewHolder(notificationCronItem)
        viewHolder.notificationCronOptionsButton.setOnClickListener {
            val notificationCron = data[viewHolder.adapterPosition]
            val contextThemeWrapper = ContextThemeWrapper(it.context, R.style.AppTheme_PopupOverlay)
            PopupMenu(contextThemeWrapper, it, Gravity.END).apply {
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
                        1 -> viewListener.testNotificationCron(notificationCron)
                        2 -> viewListener.enableNotificationCron(notificationCron)
                        3 -> viewListener.disableNotificationCron(notificationCron)
                        4 -> viewListener.editNotificationCron(notificationCron)
                        5 -> viewListener.deleteNotificationCron(notificationCron)
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
            onClickUriText.text = notificationCron.onClickUri
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

    private fun swapCronPositions(from: Int, to: Int) {
        val temp = this.data[to].position
        data[to].position = data[from].position
        data[from].position = temp
        Collections.swap(data, from, to)
    }

    override fun onCronMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                swapCronPositions(i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                swapCronPositions(i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onCronClear() {
        viewListener.moveNotificationCrons(data)
    }
}