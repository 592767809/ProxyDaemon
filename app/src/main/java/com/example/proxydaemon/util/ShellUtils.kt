package com.example.proxydaemon.util

import android.content.Context
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream


object ShellUtils {



    fun isScriptRunning(): Boolean {
        // 执行 pgrep 命令，查找 "proxy.sh" 进程
        val result = runAsRoot("pgrep -af \"proxy.sh\"")

        // 如果结果不为空，则表示脚本正在运行
        return result.isNotEmpty()
    }

    fun isDeviceRooted(): Boolean{
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            val cmd = "echo testRoot > /data/local/tmp/test.txt"
            os.writeBytes("$cmd\n")
            os.writeBytes("exit\n")
            os.flush()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
    }

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