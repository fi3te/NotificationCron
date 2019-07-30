package com.github.notificationcron.ui.licenses

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.notificationcron.R
import kotlinx.coroutines.*

class LicensesActivity : AppCompatActivity() {

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val uiScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ioScope.launch {
            setLicenseText("copy_of_apache_license.txt", findViewById(R.id.apacheText))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private suspend fun setLicenseText(fileName: String, textView: TextView) = withContext(Dispatchers.IO) {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()

        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        val text = String(buffer)

        uiScope.launch {
            textView.text = text
        }
    }
}
