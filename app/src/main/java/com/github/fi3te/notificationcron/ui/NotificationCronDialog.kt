package com.github.fi3te.notificationcron.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.github.fi3te.notificationcron.R
import com.github.fi3te.notificationcron.data.isCronValid
import com.github.fi3te.notificationcron.data.model.NotificationCron

fun showCreateDialog(windowContext: Context, create: (notificationCron: NotificationCron) -> Unit) {
    MaterialDialog(windowContext).show {
        title(R.string.create_scheduled_notifications)

        addCronInputView(this)

        positiveButton(R.string.create) {
            val (cron, notificationTitle, notificationText) = getInput(this)
            val notificationCron = NotificationCron(
                cron = cron,
                notificationTitle = notificationTitle,
                notificationText = notificationText
            )
            create(notificationCron)
        }
        negativeButton(R.string.cancel)
        setActionButtonEnabled(WhichButton.POSITIVE, false)
    }
}

fun showUpdateDialog(
    windowContext: Context,
    value: NotificationCron,
    update: (notificationCron: NotificationCron) -> Unit
) {
    MaterialDialog(windowContext).show {
        title(R.string.edit_scheduled_notifications)

        addCronInputView(this)
        setInput(this, value)

        positiveButton(R.string.update) {
            val (cron, notificationTitle, notificationText) = getInput(this)
            val updatedNotificationCron = value.copy(
                cron = cron,
                notificationTitle = notificationTitle,
                notificationText = notificationText
            )
            update(updatedNotificationCron)
        }
        negativeButton(R.string.cancel)
    }
}

fun showDeleteDialog(
    windowContext: Context,
    delete: () -> Unit
) {
    MaterialDialog(windowContext).show {
        title(R.string.delete_scheduled_notifications)
        positiveButton(R.string.delete) {
            delete()
        }
        negativeButton(R.string.cancel)
    }
}

private fun addCronInputView(dialog: MaterialDialog) {
    dialog.customView(R.layout.dialog_input_notification_cron)
    val cronInput = dialog.getCustomView().findViewById<EditText>(R.id.cronInput)
    addCronValidation(dialog, cronInput)
}

private fun getInput(dialog: MaterialDialog): Triple<String, String, String> {
    val customView = dialog.getCustomView()
    val cronInput = customView.findViewById<EditText>(R.id.cronInput)
    val notificationTitleInput = customView.findViewById<EditText>(R.id.notificationTitleInput)
    val notificationTextInput = customView.findViewById<EditText>(R.id.notificationTextInput)
    return Triple(
        cronInput.text.toString(),
        notificationTitleInput.text.toString(),
        notificationTextInput.text.toString()
    )
}

private fun setInput(dialog: MaterialDialog, notificationCron: NotificationCron) {
    val customView = dialog.getCustomView()
    customView.findViewById<EditText>(R.id.cronInput).setText(notificationCron.cron)
    customView.findViewById<EditText>(R.id.notificationTitleInput).setText(notificationCron.notificationTitle)
    customView.findViewById<EditText>(R.id.notificationTextInput).setText(notificationCron.notificationText)
}

private fun addCronValidation(dialog: MaterialDialog, cronInput: EditText) {
    cronInput.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(p0: Editable?) {
            val cronString = cronInput.text.toString()
            dialog.setActionButtonEnabled(WhichButton.POSITIVE, isCronValid(cronString))
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    })
}