package com.github.fi3te.notificationcron.data.remote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper

data class ReadFile(
    val mimeType: String?
)

class ReadFileContract : ActivityResultContract<ReadFile, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: ReadFile): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input.mimeType)
    }

    override fun getSynchronousResult(
        context: Context,
        input: ReadFile
    ): SynchronousResult<Uri?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}

data class CreateFile(
    val defaultFilename: String?,
    val mimeType: String?
)

class CreateFileContract : ActivityResultContract<CreateFile, Uri?>() {
    @CallSuper
    override fun createIntent(context: Context, input: CreateFile): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType(input.mimeType)
            .putExtra(Intent.EXTRA_TITLE, input.defaultFilename)
    }

    override fun getSynchronousResult(
        context: Context,
        input: CreateFile
    ): SynchronousResult<Uri?>? {
        return null
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
    }
}