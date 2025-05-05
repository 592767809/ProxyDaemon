package com.example.proxydaemon.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.net.toUri

object UpdateUtils {

    private const val API_URL = "https://api.github.com/repos/Sh-Fang/ProxyDaemon/releases/latest"

    fun checkUpdate(context: Context) {
        Thread {
            try {
                val url = URL(API_URL)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")

                if (conn.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = conn.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val result = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        result.append(line)
                    }

                    val json = JSONObject(result.toString())

                    val tagName = json.getString("tag_name") // e.g. "v1.2.0"
                    val releaseNote = json.getString("body")

                    val assets: JSONArray = json.getJSONArray("assets")
                    var downloadUrl: String? = null
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        if (asset.getString("name").endsWith(".apk")) {
                            downloadUrl = asset.getString("browser_download_url")
                            break
                        }
                    }

                    val latestVersion = tagName.removePrefix("v")
                    val currentVersion = context.packageManager
                        .getPackageInfo(context.packageName, 0).versionName

                    if (isNewerVersion(latestVersion, currentVersion.toString())) {
                        val finalDownloadUrl = downloadUrl
                        Handler(Looper.getMainLooper()).post {
                            AlertDialog.Builder(context)
                                .setTitle("发现新版本：$latestVersion")
                                .setMessage(releaseNote)
                                .setPositiveButton("去更新") { _, _ ->
                                    finalDownloadUrl?.let {
                                        val intent = Intent(Intent.ACTION_VIEW, it.toUri())
                                        context.startActivity(intent)
                                    }
                                }
                                .setNegativeButton("取消", null)
                                .show()
                        }
                    }else{
                        Handler(Looper.getMainLooper()).post {
                            AlertDialog.Builder(context)
                                .setTitle("已是最新版本")
                                .setNegativeButton("确认", null)
                                .show()
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    // 简单的版本比较器（基于 "1.2.0" 这样的格式）
    private fun isNewerVersion(latest: String, current: String): Boolean {
        val l = latest.split(".")
        val c = current.split(".")
        for (i in 0 until l.size.coerceAtLeast(c.size)) {
            val lv = if (i < l.size) l[i].toInt() else 0
            val cv = if (i < c.size) c[i].toInt() else 0
            if (lv > cv) return true
            if (lv < cv) return false
        }
        return false
    }
}
