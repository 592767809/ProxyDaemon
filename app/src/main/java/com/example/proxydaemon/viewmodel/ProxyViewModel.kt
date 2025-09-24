package com.example.proxydaemon.viewmodel

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proxydaemon.util.IOUtils
import com.example.proxydaemon.util.NetworkUtils
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

    private val _scriptStatus = MutableStateFlow(false)
    val scriptStatus: StateFlow<Boolean> = _scriptStatus

    private val _logOutput = MutableStateFlow("")
    val logOutput: StateFlow<String> = _logOutput

    fun appendLog(text: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logOutput.value += "[$timestamp] $text\n"
    }

    fun runProxyScript() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = RootShell.rootExec("pgrep proxyDaemon.sh")
                if (result.isNotBlank()) {
                    appendLog("检测到脚本正在运行，尝试终止并重新运行")
                    val pids = result.lines().mapNotNull { it.trim().toIntOrNull() }
                    pids.forEach { pid ->
                        RootShell.rootExec("kill -9 $pid")
                    }
                }

                RootShell.rootExec("nohup ${IOUtils.systemDestinationPath} &")
                if(RootShell.rootExec("pgrep proxyDaemon.sh").isNotBlank()){
                    appendLog("脚本运行成功 ✔\uFE0F")
                    _scriptStatus.value = true
                }else{
                    appendLog("脚本运行失败 ❌")
                }

            } catch (e: Exception) {
                appendLog("脚本启动失败：${e.message}")
            }
        }
    }

    fun checkStatus(): Boolean {
        try {
            appendLog("检测设备 Root 状态...")
            RootShell.rootExec("ls /data")
            _rootStatus.value = true
            appendLog("设备已 Root ✔\uFE0F")
        } catch (e: Exception) {
            appendLog("检测失败：设备没有 Root ❌")
            return false
        }

        try {
            appendLog("检测代理App状态...")
            val v2rayResult = RootShell.rootExec("ps | grep -E 'v2ray|xray|clash|tun2socks' | grep -v grep")
            val isProxyAppRunning = v2rayResult.trim().isNotEmpty()
            _v2rayAppStatus.value = isProxyAppRunning

            if (!isProxyAppRunning) {
                appendLog("代理App未启动 ❌")
                return false
            }

            appendLog("代理App正在运行 ✔\uFE0F")
            appendLog("检测代理状态...")

            val proxyResult = RootShell.rootExec("ip addr")
            val isProxyOn = Regex("tun\\d+|clash\\d*").containsMatchIn(proxyResult)

            _v2rayProxyStatus.value = isProxyOn

            if (!isProxyOn) {
                appendLog("未检测到代理，请选择一个节点开启代理 ❌")
                return false
            }

            appendLog("代理已连接 ✔\uFE0F")

            appendLog("检测脚本状态...")
            val result = RootShell.rootExec("pgrep proxyDaemon.sh")
            if (result.isNotBlank()) {
                _scriptStatus.value = true
                appendLog("脚本正在运行 ✔\uFE0F")
            }else{
                _scriptStatus.value = false
                appendLog("脚本尚未运行")
            }

        }catch (e: Exception){
            appendLog("检测失败：${e.message}")
        }

        return true
    }

    fun refreshStatus(){
        viewModelScope.launch(Dispatchers.IO) {
            checkStatus()
        }
    }

    fun performStartupChecks(context: Context, networkStatus: MutableState<Boolean>) {
        appendLog("检测网络状态")
        if (!NetworkUtils.isWifiConnected(context)) {
            appendLog("网络未连接 ❌")
            networkStatus.value = false
            return
        }
        appendLog("网络正常 ✔️")

        appendLog("检测必要信息")
        if (!checkStatus()) {
            appendLog("检测失败 ❌")
            return
        }
        appendLog("必要信息正常 ✔️")

        appendLog("拷贝内置脚本到系统")
        if (!IOUtils.copyScriptToSystem(context)) {
            appendLog("拷贝失败 ❌")
            return
        }
        appendLog("拷贝完成 ✔️")

        appendLog("获取连接信息")
        if (!NetworkUtils.initNetworkInfo()) {
            appendLog("获取失败 ❌")
            return
        }
        appendLog("连接信息正常 ✔️")
    }

}