package com.nishankhanal.voiceassistant

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

object Storage {
    private const val PREFS_NAME = "voice_assistant_prefs"
    private const val KEY_API = "gimen_api_key"
    private const val KEY_AUTOMATION = "automation_mode"
    private const val KEY_LISTENING = "listening_mode"
    private const val KEY_ASR_MODEL = "asr_model"
    private const val KEY_TTS_MODEL = "tts_model"

    private fun prefs(context: Context) = EncryptedSharedPreferences.create(
        PREFS_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveApiKey(context: Context, key: String) {
        prefs(context).edit().putString(KEY_API, key).apply()
    }

    fun getApiKey(context: Context): String? = prefs(context).getString(KEY_API, null)

    fun saveAsrModel(context: Context, model: String) {
        prefs(context).edit().putString(KEY_ASR_MODEL, model).apply()
    }

    fun getAsrModel(context: Context): String? = prefs(context).getString(KEY_ASR_MODEL, null)

    fun saveTtsModel(context: Context, model: String) {
        prefs(context).edit().putString(KEY_TTS_MODEL, model).apply()
    }

    fun getTtsModel(context: Context): String? = prefs(context).getString(KEY_TTS_MODEL, null)

    fun setAutomationMode(context: Context, full: Boolean) {
        prefs(context).edit().putBoolean(KEY_AUTOMATION, full).apply()
    }

    fun isFullAutomation(context: Context): Boolean = prefs(context).getBoolean(KEY_AUTOMATION, false)

    fun setListeningModeWakeword(context: Context, wakeword: Boolean) {
        prefs(context).edit().putBoolean(KEY_LISTENING, wakeword).apply()
    }

    fun isWakeword(context: Context): Boolean = prefs(context).getBoolean(KEY_LISTENING, true)
}
