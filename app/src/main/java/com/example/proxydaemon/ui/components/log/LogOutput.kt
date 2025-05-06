package com.example.proxydaemon.ui.components.log

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LogOutput(logOutput: String){
    // 日志输出区域
    Column(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(top = 8.dp)  // 日志输出字样距离上面两个按钮的边距
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth(),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.Companion.width(8.dp))
            Text(
                "日志输出",
                fontWeight = FontWeight.Companion.Bold,
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
            modifier = Modifier.Companion
                .fillMaxWidth()
                .weight(1f),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp
        ) {
            Box(
                modifier = Modifier.Companion
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
                    fontFamily = FontFamily.Companion.Monospace
                )
            }
        }
    }
}