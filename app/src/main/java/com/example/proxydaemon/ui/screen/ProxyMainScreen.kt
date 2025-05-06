package com.example.proxydaemon.ui.screen

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proxydaemon.viewmodel.ProxyViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.proxydaemon.ui.components.dialog.HelpDialog
import com.example.proxydaemon.ui.components.framework.ProxyContent
import com.example.proxydaemon.ui.components.framework.ProxyTopBar

// 主屏幕
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProxyMainScreen(viewModel: ProxyViewModel = viewModel()) {
    // 状态收集
    val context = LocalContext.current
    val rootStatus by viewModel.rootStatus.collectAsState()
    val v2rayAppStatus by viewModel.v2rayAppStatus.collectAsState()
    val v2rayProxyStatus by viewModel.v2rayProxyStatus.collectAsState()
    val scriptStatus by viewModel.scriptStatus.collectAsState()
    val logOutput by viewModel.logOutput.collectAsState()

    // 本地状态
    var showDialog by remember { mutableStateOf(false) }
    val networkStatus = remember { mutableStateOf(true) }

    // 初始化效果
    LaunchedEffect(Unit) {
        viewModel.performStartupChecks(context, networkStatus)
    }

    Scaffold(
        topBar = {
            // 创建一个渐变背景的顶部栏
            ProxyTopBar(onInfoClick = { showDialog = true })
        }
    ) { padding ->
        // 主页的内容
        ProxyContent(
            padding = padding,
            rootStatus = rootStatus,
            v2rayAppStatus = v2rayAppStatus,
            v2rayProxyStatus = v2rayProxyStatus,
            scriptStatus = scriptStatus,
            logOutput = logOutput,
            networkStatus = networkStatus.value,
            onRefreshClick = { viewModel.refreshStatus() },
            onRunScriptClick = { viewModel.runProxyScript() }
        )

        // 帮助弹窗
        if (showDialog) {
            HelpDialog(onDismiss = { showDialog = false })
        }

    }
}