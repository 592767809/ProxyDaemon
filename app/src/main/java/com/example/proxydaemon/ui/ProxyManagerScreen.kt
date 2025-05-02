package com.example.proxydaemon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
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
    val scriptStatus by viewModel.scriptStatus.collectAsState()
    val logOutput by viewModel.logOutput.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("通用","Android", "iOS", "Windows", "Mac", "Linux")

    val networkStatus = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // 检查网络连接
        viewModel.appendLog("检测网络状态")
        if (!NetworkUtils.isWifiConnected(context)){
            viewModel.appendLog("网络未连接 ❌")
            networkStatus.value = false
            return@LaunchedEffect
        }
        viewModel.appendLog("网络正常 ✔\uFE0F")

        // 检测各种状态
        viewModel.appendLog("检测必要信息")
        if (!viewModel.checkStatus()){
            viewModel.appendLog("检测失败 ❌")
            return@LaunchedEffect
        }
        viewModel.appendLog("必要信息正常 ✔\uFE0F")

        // 把脚本复制出来
        viewModel.appendLog("拷贝内置脚本到系统")
        if (!IOUtils.copyScriptToSystem(context)){
            viewModel.appendLog("拷贝失败 ❌")
            return@LaunchedEffect
        }
        viewModel.appendLog("拷贝完成 ✔\uFE0F")

        // 获取网络状态
        viewModel.appendLog("获取连接信息")
        if (!NetworkUtils.initNetworkInfo()){
            viewModel.appendLog("获取失败 ❌")
            return@LaunchedEffect
        }
        viewModel.appendLog("连接信息正常 ✔\uFE0F")

    }

    val backgroundColor = MaterialTheme.colorScheme.surface

    Scaffold(
        topBar = {
            // 创建一个渐变背景的顶部栏
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Proxy Daemon",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                            Text(
                                "一键开启旁路由",
                                fontWeight = FontWeight.Light,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = "信息",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent, // 透明背景，让渐变效果显示
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
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
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // 通用样式变量
                val cardShape = RoundedCornerShape(16.dp)
                val cardElevation = 2.dp
                val successColor = Color(0xFF4CAF50)
                val errorColor = Color(0xFFE53935)


                // root 状态卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 状态指示圆形
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = if (rootStatus)
                                            listOf(successColor.copy(alpha = 0.7f), successColor)
                                        else
                                            listOf(errorColor.copy(alpha = 0.7f), errorColor)
                                    )
                                )
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (rootStatus) Icons.Outlined.Lock else Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Root 状态",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (rootStatus)
                                        Icons.Outlined.CheckCircle
                                    else
                                        Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (rootStatus) successColor else errorColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (rootStatus) "设备已 Root" else "设备未 Root",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (rootStatus) successColor else errorColor
                                )
                            }
                        }
                    }
                }

                // V2Ray 状态卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 状态指示圆形
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = if (v2rayAppStatus && v2rayProxyStatus)
                                            listOf(successColor.copy(alpha = 0.7f), successColor)
                                        else
                                            listOf(errorColor.copy(alpha = 0.7f), errorColor)
                                    )
                                )
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (v2rayAppStatus && v2rayProxyStatus)
                                    Icons.AutoMirrored.Outlined.Send
                                else
                                    Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "V2Ray 状态",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (v2rayAppStatus)
                                        Icons.Outlined.CheckCircle
                                    else
                                        Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (v2rayAppStatus) successColor else errorColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (v2rayAppStatus) "应用已启动" else "请先启动应用",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (v2rayAppStatus) successColor else errorColor
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (v2rayProxyStatus)
                                        Icons.Outlined.CheckCircle
                                    else
                                        Icons.Default.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (v2rayProxyStatus) successColor else errorColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (v2rayProxyStatus) "代理已连接" else "请选择一个节点连接",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (v2rayProxyStatus) successColor else errorColor
                                )
                            }
                        }
                    }
                }

                // 脚本执行状态卡片
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = cardShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 状态指示圆形
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(26.dp))
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = if (scriptStatus)
                                            listOf(successColor.copy(alpha = 0.7f), successColor)
                                        else
                                            listOf(errorColor.copy(alpha = 0.7f), errorColor)
                                    )
                                )
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (scriptStatus) Icons.Outlined.Build else Icons.Outlined.Info,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "脚本状态",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (scriptStatus)
                                        Icons.Outlined.CheckCircle
                                    else
                                        Icons.Outlined.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (scriptStatus) successColor else errorColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (scriptStatus) "脚本已运行" else "脚本未运行",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (scriptStatus) successColor else errorColor
                                )
                            }
                        }
                    }
                }

                // 按钮区域
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 刷新状态按钮
                    Button(
                        onClick = { viewModel.refreshStatus() },
                        enabled = networkStatus.value,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("刷新状态", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }

                    // 执行脚本按钮
                    Button(
                        onClick = { viewModel.runProxyScript() },
                        enabled = rootStatus && v2rayAppStatus && v2rayProxyStatus,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("立即执行脚本", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }

                // 日志输出区域
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)  // 日志输出字样距离上面两个按钮的边距
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
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

                    // 日志输出框
                    val logScrollState = rememberScrollState()

                    // 自动滚动到底部
                    LaunchedEffect(logOutput) {
                        if (logOutput.isNotEmpty()) {
                            logScrollState.animateScrollTo(logScrollState.maxValue)
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
                        tonalElevation = 2.dp,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)  // 日志框内容距离四周的边距
                                .verticalScroll(logScrollState)
                        ) {
                            Text(
                                text = logOutput.ifEmpty { "暂无日志内容..." },
                                color = if (logOutput.isEmpty())
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // “使用帮助”弹窗
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(
                        onClick = { showDialog = false },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("关闭", fontWeight = FontWeight.Medium)
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "使用帮助",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                text = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 100.dp, max = 320.dp)  // 增加最大高度
                    ) {
                        // 选项卡和内容
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // 左侧竖直选项卡
                            Column(
                                modifier = Modifier
                                    .width(100.dp)
                                    .fillMaxHeight()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                    .clip(RoundedCornerShape(8.dp))
                                    .padding(vertical = 4.dp)
                            ) {
                                tabTitles.forEachIndexed { index, title ->
                                    val selected = selectedTabIndex == index
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp, horizontal = 4.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (selected)
                                                    MaterialTheme.colorScheme.primaryContainer
                                                else
                                                    Color.Transparent
                                            )
                                            .clickable { selectedTabIndex = index }
                                            .padding(vertical = 8.dp, horizontal = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = title,
                                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (selected)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            // 右侧内容区
                            val contentScrollState = rememberScrollState()
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 8.dp)
                                    .fillMaxHeight()
                                    .verticalScroll(contentScrollState)
                            ) {
                                when (tabTitles[selectedTabIndex]) {
                                    "通用" -> {
                                        // 通用信息卡片式布局
                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            InfoCard("旁路由地址", NetworkUtils.networkInfo["ip"] ?: "未知")
                                            InfoCard("子网掩码", NetworkUtils.networkInfo["netmask"] ?: "未知")
                                            InfoCard("前缀长度", NetworkUtils.networkInfo["netmaskPrefix"] ?: "未知")
                                            InfoCard("DNS", NetworkUtils.networkInfo["dns"] ?: "未知")
                                        }
                                    }
                                    "Android", "iOS", "Windows", "Mac", "Linux" -> {
                                        // 其他平台保持文本形式但增加样式
                                        val instructions = when (tabTitles[selectedTabIndex]) {
                                            "Android" -> listOf(
                                                "进入「设置」",
                                                "打开「WLAN」",
                                                "选择「已连接的WLAN」",
                                                "进入网络详情",
                                                "记下目前IP地址",
                                                "把「IP设置」更改为「静态」",
                                                "按照如下内容输入：",
                                                "IP地址：刚才记下的IP地址",
                                                "路由器：${NetworkUtils.networkInfo["ip"] ?: "未知"}",
                                                "前缀长度：${NetworkUtils.networkInfo["netmaskPrefix"] ?: "未知"}",
                                                "DNS：${NetworkUtils.networkInfo["dns"] ?: "未知"}"
                                            )

                                            "iOS" -> listOf(
                                                "进入「设置」",
                                                "打开「Wi-Fi」",
                                                "点击已连接的Wi-Fi",
                                                "找到「IPV4地址」",
                                                "记下目前IP地址",
                                                "修改「配置IP」为「手动」",
                                                "按照如下内容输入：",
                                                "IP地址：刚才记下的IP地址",
                                                "子网掩码：${NetworkUtils.networkInfo["netmask"] ?: "未知"}",
                                                "路由器：${NetworkUtils.networkInfo["ip"] ?: "未知"}"
                                            )

                                            "Windows" -> listOf(
                                                "点击右下角「网络图标」",
                                                "打开「网络和Internet设置」",
                                                "点击「更改适配器选项」",
                                                "右键点击已连接的Wi-Fi，选择「属性」",
                                                "双击「Internet协议版本 4 (TCP/IPv4)」",
                                                "记下当前IP地址信息",
                                                "选择「使用下面的IP地址」",
                                                "按照如下内容输入：",
                                                "IP地址：刚才记下的IP地址",
                                                "子网掩码：${NetworkUtils.networkInfo["netmask"] ?: "未知"}",
                                                "默认网关：${NetworkUtils.networkInfo["ip"] ?: "未知"}",
                                                "DNS服务器：${NetworkUtils.networkInfo["dns"] ?: "未知"}"
                                            )
                                            "Mac" -> listOf(
                                                "点击屏幕右上角的「Wi-Fi图标」",
                                                "选择「打开网络偏好设置」",
                                                "选中当前连接的Wi-Fi，点击右下角「高级」",
                                                "切换到「TCP/IP」标签",
                                                "记下当前IP地址",
                                                "将「配置IPv4」改为「手动」",
                                                "按照如下内容输入：",
                                                "IP地址：刚才记下的IP地址",
                                                "子网掩码：${NetworkUtils.networkInfo["netmask"] ?: "未知"}",
                                                "路由器地址：${NetworkUtils.networkInfo["ip"] ?: "未知"}",
                                                "DNS服务器：${NetworkUtils.networkInfo["dns"] ?: "未知"}"
                                            )

                                            "Linux" -> listOf(
                                                "打开「系统设置」",
                                                "进入「网络」或「Wi-Fi」设置",
                                                "选择已连接的网络，点击齿轮图标进入设置",
                                                "切换到「IPv4」标签页",
                                                "记下当前的IP地址",
                                                "将「方法」改为「手动」",
                                                "按照如下内容输入：",
                                                "地址：刚才记下的IP地址",
                                                "子网掩码：${NetworkUtils.networkInfo["netmask"] ?: "未知"}",
                                                "网关：${NetworkUtils.networkInfo["ip"] ?: "未知"}",
                                                "DNS：${NetworkUtils.networkInfo["dns"] ?: "未知"}"
                                            )

                                            else -> emptyList()
                                        }

                                        Column(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            instructions.forEach { instruction ->
                                                if (instruction.contains("：")) {
                                                    // 标题和值分开显示
                                                    val parts = instruction.split("：", limit = 2)
                                                    Text(
                                                        text = parts[0] + "：",
                                                        fontWeight = FontWeight.Bold,
                                                        color = MaterialTheme.colorScheme.primary
                                                    )
                                                    Text(
                                                        text = parts[1],
                                                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                                                    )
                                                } else {
                                                    // 普通指令步骤
                                                    Text(text = instruction)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            )
        }
    }
}

// 通用信息卡片组件
@Composable
fun InfoCard(title: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}