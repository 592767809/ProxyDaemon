package com.example.proxydaemon.util

object RootShell {
    // 普通用户命令
    fun userExec(cmd: String): String{
        return try {
            val process = Runtime.getRuntime().exec(arrayOf(cmd))
            val result = process.inputStream.bufferedReader().readText()
            process.waitFor()
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR: ${e.message}"
        }
    }

    // su执行命令
    fun rootExec(cmd: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
            val result = process.inputStream.bufferedReader().readText()
            process.waitFor()
            return result
        } catch (e: Exception) {
            e.printStackTrace()
            "ERROR: ${e.message}"
        }
    }

}