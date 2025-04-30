package com.example.proxydaemon.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proxydaemon.util.ShellUtils
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun RootCheckScreen(onRootSuccess: () -> Unit){
    val context = LocalContext.current
    var isRooted by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(Unit) {
        isRooted = ShellUtils.isDeviceRooted()
        if (isRooted == true) {
            onRootSuccess()  // 检测到 root 后回调
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 48.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Root 检测",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when (isRooted) {
                true -> "✅ 手机已 root"
                false -> "❌ 手机未 root"
                else -> "正在检测Root状态..."
            },
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            isRooted = ShellUtils.isDeviceRooted()
        }) {
            Text("重新检测")
        }
    }
}