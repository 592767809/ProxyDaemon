package com.example.proxydaemon.ui.components.buttons

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proxydaemon.ui.components.dialog.UpdateDialog
import com.example.proxydaemon.util.UpdateUtils

@Composable
fun UpdateButton(){
    var isLoading by remember { mutableStateOf(false) }
    var updateInfo by remember { mutableStateOf<UpdateUtils.UpdateInfo?>(null) }
    var showNoUpdateDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Button(
        onClick = {
            isLoading = true
            UpdateUtils.checkUpdate(context) { info ->
                isLoading = false
                if (info == null) {
                    showNoUpdateDialog = true
                } else {
                    updateInfo = info
                }
            }
        },
        enabled = !isLoading,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.secondary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier.Companion.fillMaxWidth()
    ) {
        Box {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.Companion.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("检查更新", fontWeight = FontWeight.Companion.Medium)
            }
        }

        updateInfo?.let { info ->
            UpdateDialog(
                latestVersion = info.latestVersion,
                releaseNote = info.releaseNote,
                downloadUrl = info.downloadUrl ?: "",
                onDismiss = { updateInfo = null }
            )
        }

        if (showNoUpdateDialog) {
            AlertDialog(
                onDismissRequest = { showNoUpdateDialog = false },
                title = { Text("已是最新版本") },
                confirmButton = {
                    TextButton(onClick = { showNoUpdateDialog = false }) {
                        Text("确认")
                    }
                }
            )
        }

    }

}