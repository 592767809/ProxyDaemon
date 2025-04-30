package com.example.proxydaemon.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxydaemon.util.ShellUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProxyViewModel: ViewModel() {
    private val _v2rayStatus = MutableStateFlow(false)
    val v2rayStatus: StateFlow<Boolean> = _v2rayStatus

    private val _logOutput = MutableStateFlow("")
    val logOutput: StateFlow<String> = _logOutput

    init {
        viewModelScope.launch {
            ShellUtils.runAsRoot(":")
            appendLog("获取root权限成功")
            checkStatus()
        }
    }

    private fun appendLog(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logOutput.value += "[$timestamp] $text\n"
    }

    fun prepareAndRunScript(context: Context) {
        val fileName = "proxy.sh"
        val destinationPath = "/data/local/tmp/$fileName"

        // Step 1: 将 assets 中的脚本复制出来
        context.assets.open(fileName).use { input ->
            FileOutputStream(destinationPath).use { output ->
                input.copyTo(output)
            }
        }

        appendLog("复制脚本到系统")

        // Step 2: 设置可执行权限
        ShellUtils.runAsRoot("chmod 755 $destinationPath")

        appendLog("脚本添加可执行权限")

        var output = ShellUtils.runAsRoot("nohup $destinationPath &")
        appendLog("脚本启动成功：${output}")
    }



    fun runProxyScript(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (ShellUtils.isScriptRunning()) {
                    appendLog("脚本已在运行中，无需重复启动")
                } else {
                    prepareAndRunScript(context)
                }
            } catch (e: Exception) {
                appendLog("启动失败: ${e.message}")
            }
        }
    }

    private fun checkStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val v2rayRunning = ShellUtils.isProcessRunning("com.v2ray.ang")

                _v2rayStatus.value = v2rayRunning
                appendLog("v2ray正在运行")
            } catch (e: Exception) {
                appendLog("检测失败: ${e.message}")
            }
        }
    }
}