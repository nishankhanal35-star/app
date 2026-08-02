package com.nishankhanal.voiceassistant

import android.content.Context
import okhttp3.*
import okio.BufferedSink
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.File
import android.util.Base64

object GimenClient {
    private const val DEFAULT_BASE = "https://api.gimen.ai/"

    private fun buildClient(apiKey: String?): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (!apiKey.isNullOrBlank()) {
            builder.addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(req)
            }
        }
        return builder.build()
    }

    private fun retrofit(apiKey: String?): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DEFAULT_BASE)
            .client(buildClient(apiKey))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    interface HealthApi {
        @GET("v1/health")
        suspend fun health(): Map<String, Any>
    }

    interface AsrApi {
        @Multipart
        @POST("v1/asr")
        suspend fun transcribe(@Part file: MultipartBody.Part, @Query("model") model: String): Map<String, Any>
    }

    interface TtsApi {
        @POST("v1/tts")
        suspend fun synthesize(@Body body: Map<String, String>, @Query("model") model: String): Map<String, Any>
    }

    suspend fun testKey(context: Context, apiKey: String): Boolean {
        return try {
            val r = retrofit(apiKey).create(HealthApi::class.java)
            val resp = r.health()
            resp.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun transcribeFile(context: Context, apiKey: String?, model: String, file: File): String? {
        return try {
            val reqFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("file", file.name, reqFile)
            val api = retrofit(apiKey).create(AsrApi::class.java)
            val resp = api.transcribe(part, model)
            // Expecting { "text": "..." } or similar
            when {
                resp.containsKey("text") -> resp["text"]?.toString()
                resp.containsKey("transcript") -> resp["transcript"]?.toString()
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun synthesizeSpeech(context: Context, apiKey: String?, model: String, text: String): ByteArray? {
        return try {
            val api = retrofit(apiKey).create(TtsApi::class.java)
            val body = mapOf("text" to text)
            val resp = api.synthesize(body, model)
            // Expecting { "audio": "BASE64DATA" } or raw bytes in a field
            if (resp.containsKey("audio")) {
                val b64 = resp["audio"]?.toString() ?: return null
                Base64.decode(b64, Base64.DEFAULT)
            } else if (resp.containsKey("audio_base64")) {
                val b64 = resp["audio_base64"]?.toString() ?: return null
                Base64.decode(b64, Base64.DEFAULT)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
