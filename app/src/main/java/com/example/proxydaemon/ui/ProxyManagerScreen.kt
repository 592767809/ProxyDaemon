package com.example.proxydaemon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.proxydaemon.util.NetworkUtils
import com.example.proxydaemon.viewmodel.ProxyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProxyManagerScreen(viewModel: ProxyViewModel = viewModel()) {
    val context = LocalContext.current
    val rootStatus by viewModel.rootStatus.collectAsState()
    val v2rayAppStatus by viewModel.v2rayAppStatus.collectAsState()
    val v2rayProxyStatus by viewModel.v2rayProxyStatus.collectAsState()
    val logOutput by viewModel.logOutput.collectAsState()
    val copyScriptToSystemStatus by viewModel.copyScriptToSystemStatus.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("通用","Android", "iOS", "Windows", "Mac", "Linux")

    LaunchedEffect(Unit) {
        // 如果viewmodel的init部分检测正常完成
        if (rootStatus && copyScriptToSystemStatus){
            // 把脚本复制出来
            viewModel.appendLog("拷贝内置脚本到系统")
            IOUtils.copyScriptToSystem(context)
            viewModel.appendLog("拷贝完成 ✔\uFE0F")

            // 获取网络状态
            NetworkUtils.getNetworkInfo()
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
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "信息",
                            tint = MaterialTheme.colorScheme.onPrimary
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
                // root 状态卡片
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
                                .background(if (rootStatus) Color(0xFF4CAF50) else Color(0xFFE53935))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = (if (rootStatus) Icons.Outlined.CheckCircle else Icons.Outlined.Info),
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Root 状态",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (rootStatus) "- 设备已 Root" else "- 设备未 Root",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = (if (rootStatus) Color(0xFF4CAF50) else Color(0xFFE53935))
                            )
                        }
                    }
                }

                // v2ray 状态卡片
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
                                text = "V2Ray 状态",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Text(
                                text = if (v2rayAppStatus) "- 应用已启动" else "- 请先启动应用",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = (if (v2rayAppStatus) Color(0xFF4CAF50) else Color(0xFFE53935))
                            )
                            Text(
                                text = (if (v2rayProxyStatus) "- 代理已连接" else "- 请选择一个节点连接"),
                                fontSize = 14.sp,
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
                        enabled = rootStatus && v2rayAppStatus && v2rayProxyStatus,
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

        // 模态弹窗部分
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("关闭")
                    }
                },
                title = {
                    Text("使用帮助", fontWeight = FontWeight.Bold)
                },
                text = {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min=100.dp, max=200.dp)
                    ) {
                        // 左侧竖直 Tabs
                        Column(
                            modifier = Modifier
                                .width(100.dp)
                                .fillMaxHeight()
                                .padding(vertical = 1.dp)
                        ) {
                            tabTitles.forEachIndexed { index, title ->
                                val selected = selectedTabIndex == index
                                Text(
                                    text = title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedTabIndex = index }
                                        .padding(5.dp),
                                    color = if (selected)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // 右侧内容区
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 10.dp)
                                .verticalScroll(scrollState) // 添加滚动支持
                        ) {
                            Spacer(modifier = Modifier.height(8.dp))

                            val generalInfoText = buildString {
                                append("路由器地址：\n${NetworkUtils.networkInfo["ip"]}\n\n")
                                append("子网掩码：\n${NetworkUtils.networkInfo["netmask"]}\n\n")
                                append("前缀长度：\n${NetworkUtils.networkInfo["netmaskPrefix"]}\n\n")
                                append("DNS：\n${NetworkUtils.networkInfo["dns"]}")
                            }

                            when (tabTitles[selectedTabIndex]) {
                                "通用" -> Text(generalInfoText)
                                "Android" -> Text(
                                            "进入“设置”\n\n" +
                                            "打开“WLAN”\n\n" +
                                            "选择“已连接的WLAN”\n\n" +
                                            "进入网络详情\n\n" +
                                            "记下目前IP地址\n\n" +
                                            "把“IP设置”更改为“静态”\n\n" +
                                            "按照如下内容输入：\n\n" +
                                            "IP地址：\n刚才记下的IP地址\n\n" +
                                            "路由器：\n${NetworkUtils.networkInfo["ip"]}\n\n" +
                                            "前缀长度：\n${NetworkUtils.networkInfo["netmaskPrefix"]}\n\n" +
                                            "DNS：\n${NetworkUtils.networkInfo["dns"]}"
                                )
                                "iOS" -> Text(
                                            "进入“设置”\n\n" +
                                            "打开“Wi-Fi”\n\n" +
                                            "点击已连接的Wi-Fi\n\n" +
                                            "找到“IPV4地址”\n\n" +
                                            "记下目前IP地址\n\n" +
                                            "修改“配置IP”为“手动”\n\n" +
                                            "按照如下内容输入：\n\n" +
                                            "IP地址：\n刚才记下的IP地址\n\n" +
                                            "子网掩码：\n${NetworkUtils.networkInfo["netmask"]}\n\n" +
                                            "路由器：\n${NetworkUtils.networkInfo["ip"]}\n\n"
                                )
                                "Windows" -> Text(
                                            "点击右下角“网络图标”\n\n" +
                                            "打开“网络和Internet设置”\n\n" +
                                            "点击“更改适配器选项”\n\n" +
                                            "右键点击已连接的Wi-Fi，选择“属性”\n\n" +
                                            "双击“Internet协议版本 4 (TCP/IPv4)”\n\n" +
                                            "记下当前IP地址信息\n\n" +
                                            "选择“使用下面的IP地址”\n\n" +
                                            "按照如下内容输入：\n\n" +
                                            "IP地址：\n刚才记下的IP地址\n\n" +
                                            "子网掩码：\n${NetworkUtils.networkInfo["netmask"]}\n\n" +
                                            "默认网关：\n${NetworkUtils.networkInfo["ip"]}\n\n" +
                                            "DNS服务器：\n${NetworkUtils.networkInfo["dns"]}"
                                )

                                "Mac" -> Text(
                                            "点击屏幕右上角的“Wi-Fi图标”\n\n" +
                                            "选择“打开网络偏好设置”\n\n" +
                                            "选中当前连接的Wi-Fi，点击右下角“高级”\n\n" +
                                            "切换到“TCP/IP”标签\n\n" +
                                            "记下当前IP地址\n\n" +
                                            "将“配置IPv4”改为“手动”\n\n" +
                                            "按照如下内容输入：\n\n" +
                                            "IP地址：\n刚才记下的IP地址\n\n" +
                                            "子网掩码：\n${NetworkUtils.networkInfo["netmask"]}\n\n" +
                                            "路由器地址：\n${NetworkUtils.networkInfo["ip"]}\n\n" +
                                            "DNS服务器：\n${NetworkUtils.networkInfo["dns"]}"
                                )

                                "Linux" -> Text(
                                            "打开“系统设置”\n\n" +
                                            "进入“网络”或“Wi-Fi”设置\n\n" +
                                            "选择已连接的网络，点击齿轮图标进入设置\n\n" +
                                            "切换到“IPv4”标签页\n\n" +
                                            "记下当前的IP地址\n\n" +
                                            "将“方法”改为“手动”\n\n" +
                                            "按照如下内容输入：\n\n" +
                                            "地址：\n刚才记下的IP地址\n\n" +
                                            "子网掩码：\n${NetworkUtils.networkInfo["netmask"]}\n\n" +
                                            "网关：\n${NetworkUtils.networkInfo["ip"]}\n\n" +
                                            "DNS：\n${NetworkUtils.networkInfo["dns"]}"
                                )
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface
            )
        }


    }
}