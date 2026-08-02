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
import android.media.MediaPlayer
import java.io.File

class SettingsActivity: AppCompatActivity() {

    private lateinit var apiEdit: EditText
    private lateinit var etAsr: EditText
    private lateinit var etTts: EditText
    private lateinit var btnSave: Button
    private lateinit var btnTest: Button
    private lateinit var btnTestStt: Button
    private lateinit var btnTestTts: Button
    private lateinit var rbConservative: RadioButton
    private lateinit var rbFull: RadioButton
    private lateinit var rbWake: RadioButton
    private lateinit var rbPtt: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        title = getString(R.string.settings_title)

        apiEdit = findViewById(R.id.etApiKey)
        etAsr = findViewById(R.id.etAsrModel)
        etTts = findViewById(R.id.etTtsModel)
        btnSave = findViewById(R.id.btnSave)
        btnTest = findViewById(R.id.btnTestKey)
        btnTestStt = findViewById(R.id.btnTestStt)
        btnTestTts = findViewById(R.id.btnTestTts)
        rbConservative = findViewById(R.id.rbConservative)
        rbFull = findViewById(R.id.rbFull)
        rbWake = findViewById(R.id.rbWake)
        rbPtt = findViewById(R.id.rbPtt)

        Storage.getApiKey(this)?.let { apiEdit.setText(it) }
        Storage.getAsrModel(this)?.let { etAsr.setText(it) }
        Storage.getTtsModel(this)?.let { etTts.setText(it) }
        val full = Storage.isFullAutomation(this)
        if (full) rbFull.isChecked = true else rbConservative.isChecked = true
        val wake = Storage.isWakeword(this)
        if (wake) rbWake.isChecked = true else rbPtt.isChecked = true

        btnSave.setOnClickListener {
            val k = apiEdit.text.toString().trim()
            Storage.saveApiKey(this, k)
            Storage.saveAsrModel(this, etAsr.text.toString().trim())
            Storage.saveTtsModel(this, etTts.text.toString().trim())
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

        btnTestStt.setOnClickListener {
            // record short audio and send to API
            val key = apiEdit.text.toString().trim()
            val model = etAsr.text.toString().trim().ifEmpty { "gimen/asr-nepali" }
            if (key.isBlank()) {
                Toast.makeText(this, "Paste API key first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            btnTestStt.isEnabled = false
            Toast.makeText(this, "Recording 4 seconds...", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                val wav = AudioRecorder.recordToFile(4)
                if (wav != null) {
                    val text = GimenClient.transcribeFile(this@SettingsActivity, key, model, wav)
                    runOnUiThread {
                        Toast.makeText(this@SettingsActivity, "Result: ${text ?: "(no transcript)"}", Toast.LENGTH_LONG).show()
                        btnTestStt.isEnabled = true
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(this@SettingsActivity, "Recording failed", Toast.LENGTH_SHORT).show()
                        btnTestStt.isEnabled = true
                    }
                }
            }
        }

        btnTestTts.setOnClickListener {
            val key = apiEdit.text.toString().trim()
            val model = etTts.text.toString().trim().ifEmpty { "gimen/tts-nepali" }
            if (key.isBlank()) {
                Toast.makeText(this, "Paste API key first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            btnTestTts.isEnabled = false
            CoroutineScope(Dispatchers.IO).launch {
                val audio = GimenClient.synthesizeSpeech(this@SettingsActivity, key, model, "नमस्ते, यो तपाईको टेस्ट हो।")
                if (audio != null) {
                    try {
                        val f = File.createTempFile("gimen_tts", ".mp3", null)
                        f.writeBytes(audio)
                        val mp = MediaPlayer()
                        mp.setDataSource(f.absolutePath)
                        mp.prepare()
                        mp.start()
                        mp.setOnCompletionListener {
                            it.release()
                            f.delete()
                            runOnUiThread { btnTestTts.isEnabled = true }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        runOnUiThread { Toast.makeText(this@SettingsActivity, "TTS playback failed", Toast.LENGTH_SHORT).show(); btnTestTts.isEnabled = true }
                    }
                } else {
                    runOnUiThread { Toast.makeText(this@SettingsActivity, "TTS failed", Toast.LENGTH_SHORT).show(); btnTestTts.isEnabled = true }
                }
            }
        }
    }
}
