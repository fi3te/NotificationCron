package com.github.fi3te.notificationcron.ui

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.github.fi3te.notificationcron.R

fun showHelpDialog(windowContext: Context) {
    MaterialDialog(windowContext).show {
        title(R.string.help)
        customView(R.layout.dialog_help)
    }
}