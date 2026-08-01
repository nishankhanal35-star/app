package com.nishankhanal.voiceassistant

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsActivity: AppCompatActivity() {

    private lateinit var apiEdit: EditText
    private lateinit var btnSave: Button
    private lateinit var btnTest: Button
    private lateinit var rbConservative: RadioButton
    private lateinit var rbFull: RadioButton
    private lateinit var rbWake: RadioButton
    private lateinit var rbPtt: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.settings_title)

        apiEdit = findViewById(R.id.etApiKey)
        btnSave = findViewById(R.id.btnSave)
        btnTest = findViewById(R.id.btnTestKey)
        rbConservative = findViewById(R.id.rbConservative)
        rbFull = findViewById(R.id.rbFull)
        rbWake = findViewById(R.id.rbWake)
        rbPtt = findViewById(R.id.rbPtt)

        Storage.getApiKey(this)?.let { apiEdit.setText(it) }
        val full = Storage.isFullAutomation(this)
        if (full) rbFull.isChecked = true else rbConservative.isChecked = true
        val wake = Storage.isWakeword(this)
        if (wake) rbWake.isChecked = true else rbPtt.isChecked = true

        btnSave.setOnClickListener {
            val k = apiEdit.text.toString().trim()
            Storage.saveApiKey(this, k)
            Storage.setAutomationMode(this, rbFull.isChecked)
            Storage.setListeningModeWakeword(this, rbWake.isChecked)
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
        }

        btnTest.setOnClickListener {
            val key = apiEdit.text.toString().trim()
            if (key.isBlank()) {
                Toast.makeText(this, "Paste API key first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Test key using GimenClient.ping
            CoroutineScope(Dispatchers.IO).launch {
                val ok = GimenClient.testKey(this@SettingsActivity, key)
                runOnUiThread {
                    Toast.makeText(this@SettingsActivity, if (ok) getString(R.string.key_ok) else getString(R.string.key_fail), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
