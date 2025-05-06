package com.example.proxydaemon.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.example.proxydaemon.ui.components.buttons.UpdateButton
import com.example.proxydaemon.ui.components.cards.InfoCard
import com.example.proxydaemon.util.NetworkUtils

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("通用", "Android", "iOS", "Windows", "Mac", "Linux", "关于")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("关闭", fontWeight = FontWeight.Medium)
            }
        },
        title = { DialogTitle() },
        text = {
            DialogContent(
                selectedTabIndex = selectedTabIndex,
                tabTitles = tabTitles,
                onTabSelected = { selectedTabIndex = it }
            )
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

@Composable
private fun DialogTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
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
}

@Composable
private fun DialogContent(
    selectedTabIndex: Int,
    tabTitles: List<String>,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 100.dp, max = 320.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // 左侧选项卡
            TabsPanel(
                selectedTabIndex = selectedTabIndex,
                tabTitles = tabTitles,
                onTabSelected = onTabSelected
            )

            // 右侧内容区
            ContentPanel(
                selectedTabTitle = tabTitles[selectedTabIndex]
            )
        }
    }
}

@Composable
private fun TabsPanel(
    selectedTabIndex: Int,
    tabTitles: List<String>,
    onTabSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .width(100.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .clip(RoundedCornerShape(8.dp))
            .padding(vertical = 4.dp)
    ) {
        tabTitles.forEachIndexed { index, title ->
            TabItem(
                title = title,
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
private fun TabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
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
            .clickable(onClick = onClick)
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

@Composable
private fun ContentPanel(selectedTabTitle: String) {
    val contentScrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp)
            .fillMaxHeight()
            .verticalScroll(contentScrollState)
    ) {
        when (selectedTabTitle) {
            "通用" -> GeneralContent()
            "关于" -> AboutContent()
            else -> PlatformInstructionContent(platform = selectedTabTitle)
        }
    }
}

@Composable
private fun GeneralContent() {
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

@Composable
private fun AboutContent() {
    UpdateButton()
}

@Composable
private fun PlatformInstructionContent(platform: String) {
    val instructions = getPlatformInstructions(platform)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        instructions.forEach { instruction ->
            if (instruction.contains("：")) {
                // 标题和值分开显示
                val parts = instruction.split("：", limit = 2)
                InstructionHeader(parts[0])
                InstructionValue(parts[1])
            } else {
                // 普通指令步骤
                Text(text = instruction)
            }
        }
    }
}

@Composable
private fun InstructionHeader(text: String) {
    Text(
        text = "$text：",
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun InstructionValue(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
    )
}

private fun getPlatformInstructions(platform: String): List<String> {
    return when (platform) {
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
            "路由器：${NetworkUtils.networkInfo["ip"] ?: "未知"}",
            "退出当前页面",
            "进入DNS配置页面",
            "修改为「手动」",
            "DNS：${NetworkUtils.networkInfo["dns"] ?: "未知"}"
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
}