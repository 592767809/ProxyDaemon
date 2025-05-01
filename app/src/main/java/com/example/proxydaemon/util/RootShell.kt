package com.example.proxydaemon.util

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

object RootShell {

    private var suProcess: Process? = null
    private var suWriter: BufferedWriter? = null
    private var suReader: BufferedReader? = null

    fun initShell() {
        suProcess = Runtime.getRuntime().exec("su")
        suWriter = BufferedWriter(OutputStreamWriter(suProcess!!.outputStream))
        suReader = BufferedReader(InputStreamReader(suProcess!!.inputStream))
    }

    fun rootExec(cmd: String): String {
        if (suProcess == null) initShell()

        // 标记结果结束（避免读取卡死）
        val endMark = "EOF-${System.currentTimeMillis()}"
        suWriter?.apply {
            write("$cmd\n")
            write("echo $endMark\n")
            flush()
        }

        // 读取输出直到遇到 endMark
        val output = StringBuilder()
        var line: String?
        while (true) {
            line = suReader?.readLine() ?: break
            if (line.contains(endMark)) break
            output.appendLine(line)
        }

        return output.toString()
    }

    fun close() {
        suWriter?.write("exit\n")
        suWriter?.flush()
        suWriter?.close()
        suReader?.close()
        suProcess?.destroy()
        suProcess = null
    }

}