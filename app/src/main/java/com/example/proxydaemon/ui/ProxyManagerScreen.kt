package com.example.proxydaemon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proxydaemon.util.IOUtils
import com.example.proxydaemon.viewmodel.ProxyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProxyManagerScreen(viewModel: ProxyViewModel = viewModel()) {
    val context = LocalContext.current
    val v2rayAppStatus by viewModel.v2rayAppStatus.collectAsState()
    val v2rayProxyStatus by viewModel.v2rayProxyStatus.collectAsState()
    val logOutput by viewModel.logOutput.collectAsState()
    val copyScriptToSystemStatus by viewModel.copyScriptToSystemStatus.collectAsState()

    LaunchedEffect(Unit) {
        // 如果viewmodel的init部分检测正常完成，那么就把脚本复制出来
        if (copyScriptToSystemStatus){
            viewModel.appendLog("拷贝程序内的脚本到：${IOUtils.systemDestinationPath}")
            IOUtils.copyScriptToSystem(context)
            viewModel.appendLog("完成：${IOUtils.systemDestinationPath}")
        }
    }

    val backgroundColor = MaterialTheme.colorScheme.surface
    val cardColor = MaterialTheme.colorScheme.surfaceVariant
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Proxy Daemon",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))  // 可以调整上下间距
                        Text(
                            "一键开启旁路由",
                            fontWeight = FontWeight.Thin,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 状态卡片
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (v2rayAppStatus && v2rayProxyStatus) Color(0xFF4CAF50) else Color(0xFFE53935))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = (if (v2rayAppStatus && v2rayProxyStatus) Icons.Outlined.CheckCircle else Icons.Outlined.Info),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "状态",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (v2rayAppStatus) "V2Ray 已启动" else "V2Ray 未启动",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = (if (v2rayAppStatus) Color(0xFF4CAF50) else Color(0xFFE53935))
                            )
                            Text(
                                text = (if (v2rayProxyStatus) "V2Ray 代理已连接" else "V2Ray 代理未连接"),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = (if (v2rayProxyStatus) Color(0xFF4CAF50) else Color(0xFFE53935))
                            )
                        }
                    }
                }

                // 执行脚本按钮
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp) // 按钮之间间距
                ) {
                    Button(
                        onClick = {viewModel.refreshStatus()},
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("刷新状态", fontSize = 16.sp)
                    }

                    Button(
                        onClick = { viewModel.runProxyScript() },
                        enabled = v2rayAppStatus,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("立即执行脚本", fontSize = 16.sp)
                    }
                }



                // 日志部分
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning, // 替换 Terminal 图标
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "日志输出",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        tonalElevation = 1.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                                .verticalScroll(scrollState) // 添加滚动支持
                        ) {
                            Text(
                                text = logOutput.ifEmpty { "暂无日志内容..." },
                                color = if (logOutput.isEmpty())
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}