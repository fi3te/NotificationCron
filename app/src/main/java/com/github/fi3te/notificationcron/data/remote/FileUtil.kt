package com.github.fi3te.notificationcron.data.remote

import android.content.Context
import android.net.Uri
import java.io.*

fun copyFile(source: File, destination: Uri, context: Context?): Boolean {
    if (context == null) {
        return false
    }
    try {
        FileInputStream(source).use { inputStream ->
            context.contentResolver.openFileDescriptor(
                destination, "w"
            ).use { parcelFileDescriptor ->
                FileOutputStream(parcelFileDescriptor!!.fileDescriptor)
                    .use { outputStream ->
                        copy(inputStream, outputStream)
                        return true
                    }
            }
        }
    } catch (e: IOException) {
        return false
    }
}

fun copyFile(source: Uri, destination: File, context: Context?): Boolean {
    if (context == null) {
        return false
    }
    try {
        context.contentResolver.openInputStream(source).use { inputStream ->
            FileOutputStream(destination).use { outputStream ->
                copy(inputStream!!, outputStream)
                return true
            }
        }
    } catch (e: IOException) {
        return false
    }
}

@Throws(IOException::class)
private fun copy(inputStream: InputStream, outputStream: FileOutputStream) {
    val channel = outputStream.channel
    channel.truncate(0)

    val buffer = ByteArray(1024)
    var length: Int
    while (inputStream.read(buffer).also { length = it } > 0) {
        outputStream.write(buffer, 0, length)
    }
}