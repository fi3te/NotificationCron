package com.github.fi3te.notificationcron.ui

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.github.fi3te.notificationcron.R

fun showImprintDialog(windowContext: Context) {
    MaterialDialog(windowContext).show {
        title(R.string.imprint)
        customView(R.layout.dialog_imprint)
    }
}