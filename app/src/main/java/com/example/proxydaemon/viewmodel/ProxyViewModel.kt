package com.example.proxydaemon.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxydaemon.util.IOUtils
import com.example.proxydaemon.util.RootShell
import kotlinx.coroutines.Dispatchers
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
            appendLog("检测v2ray是否运行")
            val v2ray_pid = RootShell.rootExec("pgrep com.v2ray")
            val v2rayRunning = v2ray_pid.trim().isNotEmpty()
            _v2rayStatus.value = v2rayRunning

            if (v2rayRunning){
                appendLog("v2ray正在运行：${v2ray_pid.trim()}")
            }else {
                appendLog("v2ray未启动，请先启动v2ray")
            }


        }
    }

    fun appendLog(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logOutput.value += "[$timestamp] $text\n"
    }

    fun runProxyScript() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                appendLog("检测脚本是否重复运行")
                val result = RootShell.rootExec("pgrep proxyDaemon.sh")
                if (result.isNotEmpty()) {
                    appendLog("脚本已在运行中，无需重复启动")
                } else {
                    val proxy_pid = RootShell.rootExec("nohup ${IOUtils.systemDestinationPath} &")
                    appendLog("脚本运行成功：${proxy_pid}")
                }
            } catch (e: Exception) {
                appendLog("脚本启动失败: ${e.message}")
            }
        }
    }

}