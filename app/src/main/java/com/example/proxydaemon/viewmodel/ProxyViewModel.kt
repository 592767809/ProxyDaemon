package com.example.proxydaemon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxydaemon.util.ShellUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ProxyViewModel: ViewModel() {
    private val _v2rayStatus = MutableStateFlow(false)
    val v2rayStatus: StateFlow<Boolean> = _v2rayStatus

    private val _logOutput = MutableStateFlow("")
    val logOutput: StateFlow<String> = _logOutput

    init {
        viewModelScope.launch {
            checkStatus()
//            while (true) {
//                checkStatus()
//                delay(5000)
//            }
        }
    }

    private fun appendLog(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logOutput.value += "[$timestamp] $text\n"
    }

    fun runProxyScript() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ShellUtils.runAsRoot("nohup /data/local/proxy/proxy.sh &")
                appendLog("脚本启动成功")
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
            } catch (e: Exception) {
                appendLog("检测失败: ${e.message}")
            }
        }
    }
}