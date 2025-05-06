package com.example.proxydaemon.ui.components.framework

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.proxydaemon.ui.components.buttons.ActionButtons
import com.example.proxydaemon.ui.components.cards.RootStatusCard
import com.example.proxydaemon.ui.components.cards.ScriptStatusCard
import com.example.proxydaemon.ui.components.cards.V2rayStatusCard
import com.example.proxydaemon.ui.components.log.LogOutput

@Composable
fun ProxyContent(
    padding: PaddingValues,
    rootStatus: Boolean,
    v2rayAppStatus: Boolean,
    v2rayProxyStatus: Boolean,
    scriptStatus: Boolean,
    logOutput: String,
    networkStatus: Boolean,
    onRefreshClick: () -> Unit,
    onRunScriptClick: () -> Unit
) {
    // 通用样式变量
    val cardShape = RoundedCornerShape(16.dp)
    val cardElevation = 2.dp
    val successColor = Color(0xFF4CAF50)
    val errorColor = Color(0xFFE53935)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            // root 状态卡片
            RootStatusCard(
                rootStatus = rootStatus,
                cardShape = cardShape,
                cardElevation = cardElevation,
                successColor = successColor,
                errorColor = errorColor
            )

            // V2Ray 状态卡片
            V2rayStatusCard(
                v2rayAppStatus = v2rayAppStatus,
                v2rayProxyStatus = v2rayProxyStatus,
                cardShape = cardShape,
                cardElevation = cardElevation,
                successColor = successColor,
                errorColor = errorColor
            )

            // 脚本执行状态卡片
            ScriptStatusCard(
                scriptStatus = scriptStatus,
                cardShape = cardShape,
                cardElevation = cardElevation,
                successColor = successColor,
                errorColor = errorColor
            )

            // 操作按钮
            ActionButtons(
                networkStatus = networkStatus,
                canRunScript = rootStatus && v2rayAppStatus && v2rayProxyStatus,
                onRefreshClick = onRefreshClick,
                onRunScriptClick = onRunScriptClick
            )

            // 日志区域
            LogOutput(logOutput = logOutput)
        }
    }
}