package com.example.proxydaemon.util

import android.content.Context
import java.io.FileOutputStream

object IOUtils {

    var destinationPath = ""
    var systemDestinationPath = "/data/local/tmp/"

    fun copyScriptToSystem(context: Context) {
        val fileName = "proxyDaemon.sh"
        val externalStoragePath = context.getExternalFilesDir(null)?.absolutePath
        destinationPath = "$externalStoragePath/$fileName"
        systemDestinationPath = "$systemDestinationPath/$fileName"

        // 复制脚本文件
        try {
            context.assets.open(fileName).use { input ->
                FileOutputStream(destinationPath).use { output ->
                    input.copyTo(output)
                }
            }

            // 设置脚本文件的权限为可执行
            RootShell.rootExec("cp $destinationPath $systemDestinationPath")
            RootShell.rootExec("chmod 755 $systemDestinationPath")

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}