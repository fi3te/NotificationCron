package com.github.fi3te.notificationcron.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.github.fi3te.notificationcron.R
import com.github.fi3te.notificationcron.data.remote.*
import com.github.fi3te.notificationcron.ui.BackupViewModel
import com.github.fi3te.notificationcron.ui.reloadTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SettingsActivity : AppCompatActivity() {

    private lateinit var backupViewModel: BackupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        backupViewModel =
            ViewModelProvider(this).get(BackupViewModel::class.java)
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

    override fun onBackPressed() {
        if (!backupViewModel.backupRunning) {
            super.onBackPressed()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

        private lateinit var backupViewModel: BackupViewModel
        private lateinit var createBackupLauncher: ActivityResultLauncher<CreateFile>
        private lateinit var restoreBackupLauncher: ActivityResultLauncher<ReadFile>

        companion object {
            private const val CREATE_BACKUP = "create_backup"
            private const val RESTORE_BACKUP = "restore_backup"
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val createBackup = preferenceManager.findPreference<Preference>(CREATE_BACKUP)
            createBackup?.onPreferenceClickListener = this

            val restoreBackup = preferenceManager.findPreference<Preference>(RESTORE_BACKUP)
            restoreBackup?.onPreferenceClickListener = this

            backupViewModel =
                ViewModelProvider(requireActivity()).get(BackupViewModel::class.java)
            createBackupLauncher = registerForActivityResult(CreateFileContract()) {
                it?.let {
                    backupViewModel.createBackup(it).observe(viewLifecycleOwner, { successful ->
                        val textRes =
                            if (successful) R.string.created_backup_successfully else R.string.restored_backup_unsuccessfully
                        Toast.makeText(context, textRes, Toast.LENGTH_SHORT).show()
                    })
                }
            }
            restoreBackupLauncher = registerForActivityResult(ReadFileContract()) {
                it?.let {
                    backupViewModel.restoreBackup(it).observe(viewLifecycleOwner, { successful ->
                        val textRes =
                            if (successful) R.string.restored_backup_successfully else R.string.restored_backup_unsuccessfully
                        activity?.let { reloadTheme(it) }
                        Toast.makeText(context, textRes, Toast.LENGTH_SHORT).show()
                    })
                }
            }
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
            super.onPause()
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            activity?.let {
                if (key.equals("theme") && !backupViewModel.backupRunning) {
                    reloadTheme(requireActivity())
                }
            }
        }

        override fun onPreferenceClick(preference: Preference?): Boolean {
            if (activity != null && !backupViewModel.backupRunning) {
                when (preference?.key) {
                    CREATE_BACKUP -> {
                        val dateString =
                            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                        val defaultFileName = "Backup_$dateString.json"
                        createBackupLauncher.launch(jsonCreateFile(defaultFileName))
                    }
                    RESTORE_BACKUP -> {
                        restoreBackupLauncher.launch(jsonReadFile())
                    }
                }
            }
            return true
        }
    }
}
