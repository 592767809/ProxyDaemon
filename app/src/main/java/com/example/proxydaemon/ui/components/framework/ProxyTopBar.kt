package com.example.proxydaemon.ui.components.framework

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProxyTopBar(onInfoClick: () -> Unit) {
    // 一个渐变背景的顶部栏
    Box(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .background(
                brush = Brush.Companion.horizontalGradient(
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
                    horizontalAlignment = Alignment.Companion.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "Proxy Daemon",
                        fontWeight = FontWeight.Companion.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.Companion.White
                    )
                    Text(
                        "一键开启旁路由",
                        fontWeight = FontWeight.Companion.Light,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Companion.White.copy(alpha = 0.85f)
                    )
                }
            },
            actions = {
                IconButton(onClick = { onInfoClick() }) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = "信息",
                        tint = Color.Companion.White
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Companion.Transparent, // 透明背景，让渐变效果显示
                titleContentColor = Color.Companion.White,
                actionIconContentColor = Color.Companion.White
            )
        )
    }
}