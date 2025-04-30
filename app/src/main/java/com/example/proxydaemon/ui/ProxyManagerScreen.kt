package com.example.proxydaemon.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proxydaemon.util.IOUtils
import com.example.proxydaemon.viewmodel.ProxyViewModel

@Composable
fun ProxyManagerScreen(viewModel: ProxyViewModel = viewModel()) {
    val context = LocalContext.current
    val v2rayStatus by viewModel.v2rayStatus.collectAsState()
    val logOutput by viewModel.logOutput.collectAsState()

    LaunchedEffect(Unit) {
        IOUtils.copyScriptToSystem(context)
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text("V2Ray 状态：${if (v2rayStatus) "已启动 ✅" else "未启动 ❌"}")
        Spacer(Modifier.height(8.dp))

        Spacer(Modifier.height(24.dp))
        Button(onClick = { viewModel.runProxyScript() }, enabled = v2rayStatus) {
            Text("立即执行脚本")
        }

        Spacer(Modifier.height(24.dp))
        Text("日志输出：")
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.Gray)
            .padding(8.dp)) {
            Text(logOutput)
        }
    }
}