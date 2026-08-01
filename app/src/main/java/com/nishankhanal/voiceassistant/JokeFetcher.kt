package com.nishankhanal.voiceassistant

import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object JokeFetcher {
    fun fetchRandomJoke(): String {
        val api = "https://official-joke-api.appspot.com/random_joke"
        var conn: HttpURLConnection? = null
        return try {
            val url = URL(api)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            val code = conn.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                return "Failed to fetch joke (HTTP $code)"
            }
            val data = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(data)
            val setup = json.optString("setup")
            val punchline = json.optString("punchline")
            if (setup.isNullOrEmpty() && punchline.isNullOrEmpty()) {
                "No joke available."
            } else {
                "$setup\n\n$punchline"
            }
        } catch (e: Exception) {
            "Error fetching joke: ${e.localizedMessage}"
        } finally {
            conn?.disconnect()
        }
    }
}
