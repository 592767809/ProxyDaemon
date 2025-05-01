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
    private val _v2rayAppStatus = MutableStateFlow(false)
    val v2rayAppStatus: StateFlow<Boolean> = _v2rayAppStatus

    private val _v2rayProxyStatus = MutableStateFlow(false)
    val v2rayProxyStatus: StateFlow<Boolean> = _v2rayProxyStatus

    private val _logOutput = MutableStateFlow("")
    val logOutput: StateFlow<String> = _logOutput

    private val _copyScriptToSystemStatus = MutableStateFlow(true) // 初始为 true
    val copyScriptToSystemStatus: StateFlow<Boolean> = _copyScriptToSystemStatus

    init {
        viewModelScope.launch {
            checkStatus()
        }
    }

    fun appendLog(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logOutput.value += "[$timestamp] $text\n"
    }

    fun runProxyScript() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                appendLog("检测脚本是否已运行")
                val result = RootShell.rootExec("pgrep proxyDaemon.sh")
                if (result.isNotEmpty()) {
                    appendLog("脚本已运行，无需重复启动")
                } else {
                    val proxyPid = RootShell.rootExec("nohup ${IOUtils.systemDestinationPath} &")
                    appendLog("脚本运行成功 $proxyPid")
                }
            } catch (e: Exception) {
                appendLog("脚本启动失败: ${e.message}")
            }
        }
    }

    fun checkStatus(){
        viewModelScope.launch(Dispatchers.IO) {
            appendLog("检测v2ray是否运行")
            val v2rayPid = RootShell.rootExec("pgrep com.v2ray")
            val isV2rayAppRunning = v2rayPid.trim().isNotEmpty()
            _v2rayAppStatus.value = isV2rayAppRunning
            _copyScriptToSystemStatus.value = isV2rayAppRunning

            if (isV2rayAppRunning){
                appendLog("检测v2ray是否开启代理")

                var v2rayResult = RootShell.rootExec("ps | grep libtun2socks.so")
                val isV2rayProxyOn = v2rayResult.contains("v2ray")

                _v2rayProxyStatus.value = isV2rayProxyOn

                _copyScriptToSystemStatus.value = isV2rayProxyOn

                if (isV2rayProxyOn){
                    appendLog("v2ray正在运行：${v2rayPid.trim()}")
                }else{
                    appendLog("v2ray代理未开启，请选择一个节点开启代理")
                }
            }else {
                appendLog("v2ray未启动，请先启动v2ray")
            }
        }
    }

    fun refreshStatus(){
        viewModelScope.launch(Dispatchers.IO) {
            checkStatus()
        }
    }

}