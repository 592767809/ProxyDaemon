package com.example.proxydaemon.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proxydaemon.viewmodel.ProxyViewModel

@Composable
fun ProxyManagerScreen(viewModel: ProxyViewModel = viewModel()) {
    val v2rayStatus by viewModel.v2rayStatus.collectAsState()
    val logOutput by viewModel.logOutput.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(32.dp)) {

        Text("V2Ray 状态：${if (v2rayStatus) "已启动 ✅" else "未启动 ❌"}")
        Spacer(Modifier.height(8.dp))

        Spacer(Modifier.height(24.dp))
        Button(onClick = { viewModel.runProxyScript() }) {
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