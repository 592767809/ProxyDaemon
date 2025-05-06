package com.example.proxydaemon.ui.components.dialog

import android.content.Intent
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri

@Composable
fun UpdateDialog(
    latestVersion: String,
    releaseNote: String,
    downloadUrl: String,
    onDismiss: () -> Unit
){
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("发现新版本：$latestVersion") },
        text = { Text(releaseNote) },
        confirmButton = {
            TextButton(onClick = {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, downloadUrl.toUri())
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "无法打开链接", Toast.LENGTH_SHORT).show()
                }

                onDismiss()
            }) {
                Text("去更新")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}