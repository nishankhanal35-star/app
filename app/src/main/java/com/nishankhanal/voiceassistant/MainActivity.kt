package com.nishankhanal.voiceassistant

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)
        val btnSpeak = findViewById<Button>(R.id.btnSpeak)
        val btnOpenWhatsApp = findViewById<Button>(R.id.btnOpenWhatsApp)
        val btnJoke = findViewById<Button>(R.id.btnJoke)
        val tvJoke = findViewById<TextView>(R.id.tvJoke)

        btnStart.setOnClickListener {
            startService(Intent(this, VoiceForegroundService::class.java))
        }
        btnStop.setOnClickListener {
            stopService(Intent(this, VoiceForegroundService::class.java))
        }
        btnSpeak.setOnClickListener {
            // Quick demo: speak a Nepali sentence using on-device TTS
            VoiceManager.get(this).speak("नमस्ते। म तपाईंलाई कसरी मद्दत गर्न सक्छु?")
        }
        btnOpenWhatsApp.setOnClickListener {
            // Prefill a message to a phone number via Intent (safe fallback)
            ActionDispatcher.openWhatsAppAndPrefill(this, "+9779812345678", "नमस्ते! यो एक परीक्षण सन्देश हो।")
        }

        btnJoke.setOnClickListener {
            btnJoke.isEnabled = false
            btnJoke.text = "Loading..."
            Thread {
                val joke = JokeFetcher.fetchRandomJoke()
                runOnUiThread {
                    btnJoke.isEnabled = true
                    btnJoke.text = getString(R.string.get_joke)
                    tvJoke.text = joke
                    VoiceManager.get(this).speak(joke)
                }
            }.start()
        }
    }
}
