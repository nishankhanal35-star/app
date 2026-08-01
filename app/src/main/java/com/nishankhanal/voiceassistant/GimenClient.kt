package com.nishankhanal.voiceassistant

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

object GimenClient {

    // Default base URL; user can override by providing full base in the key area if needed.
    private const val DEFAULT_BASE = "https://api.gimen.ai/"

    private fun buildClient(apiKey: String?): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (!apiKey.isNullOrBlank()) {
            builder.addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val req: Request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $apiKey")
                        .build()
                    return chain.proceed(req)
                }
            })
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

    suspend fun testKey(context: Context, apiKey: String): Boolean {
        return try {
            val r = retrofit(apiKey).create(HealthApi::class.java)
            val resp = r.health()
            resp.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
