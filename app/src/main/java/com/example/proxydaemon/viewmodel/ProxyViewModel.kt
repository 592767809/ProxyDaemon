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
    private val _rootStatus = MutableStateFlow(false)
    val rootStatus: StateFlow<Boolean> = _rootStatus

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
                appendLog("检测脚本状态")
                val result = RootShell.rootExec("pgrep proxyDaemon.sh")
                if (result.isNotEmpty()) {
                    appendLog("脚本已运行，无需重复启动")
                } else {
                    RootShell.rootExec("nohup ${IOUtils.systemDestinationPath} &")
                    appendLog("脚本运行成功 ✔\uFE0F")
                }
            } catch (e: Exception) {
                appendLog("脚本启动失败")
            }
        }
    }

    fun checkStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                appendLog("检测设备 Root 状态...")
                RootShell.rootExec("ls /data")
                _rootStatus.value = true
                appendLog("设备已 Root ✔\uFE0F")

                appendLog("检测 V2Ray 状态")
                val v2rayResult = RootShell.rootExec("pgrep com.v2ray")
                val isV2rayAppRunning = v2rayResult.trim().isNotEmpty()
                _v2rayAppStatus.value = isV2rayAppRunning
                _copyScriptToSystemStatus.value = isV2rayAppRunning

                if (isV2rayAppRunning) {
                    appendLog("V2Ray 正在运行 ✔\uFE0F")
                    appendLog("检测 V2Ray 代理状态")
                    try {
                        val proxyResult = RootShell.rootExec("ps | grep libtun2socks.so")
                        val isV2rayProxyOn = proxyResult.contains("v2ray")

                        _v2rayProxyStatus.value = isV2rayProxyOn
                        _copyScriptToSystemStatus.value = isV2rayProxyOn

                        if (isV2rayProxyOn) {
                            appendLog("V2Ray 代理已开启 ✔\uFE0F")
                        } else {
                            appendLog("未检测到 V2Ray 代理，请选择一个节点开启代理 ❌")
                        }
                    } catch (e: Exception) {
                        appendLog("检测代理状态失败 ❌")
                    }
                } else {
                    appendLog("未检测到 V2ay 应用，请先启动 V2Ray ❌")
                }
            } catch (e: Exception) {
                appendLog("检测失败：设备没有 Root ❌")
            }
        }
    }

    fun refreshStatus(){
        viewModelScope.launch(Dispatchers.IO) {
            checkStatus()
        }
    }

}