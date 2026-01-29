package com.example.battimuro.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {
    private const val GITHUB_REPO_OWNER = "01DIGITALS"
    private const val GITHUB_REPO_NAME = "battimuro"
    
    // Returns Pair<TagName, Url> or null if error
    suspend fun checkForUpdate(): Pair<String, String>? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/$GITHUB_REPO_OWNER/$GITHUB_REPO_NAME/releases/latest")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                if (connection.responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }
                    reader.close()

                    val json = JSONObject(response.toString())
                    val tagName = json.getString("tag_name") // e.g., "v1.1"
                    val htmlUrl = json.getString("html_url") // Link to release page
                    
                    return@withContext Pair(tagName, htmlUrl)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return@withContext null
        }
    }
}
