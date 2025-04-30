package com.example.proxydaemon.util

import java.io.BufferedReader
import java.io.InputStreamReader

object ShellUtils {
    fun runAsRoot(cmd: String): String {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
        val result = process.inputStream.bufferedReader().readText()
        process.waitFor()
        return result
    }

    fun isProcessRunning(processName: String): Boolean {
        val output = runAsRoot("pidof $processName")
        return output.trim().isNotEmpty()
    }

    fun checkOutputContains(cmd: String, keyword: String): Boolean {
        val output = runAsRoot(cmd)
        return output.contains(keyword)
    }
}