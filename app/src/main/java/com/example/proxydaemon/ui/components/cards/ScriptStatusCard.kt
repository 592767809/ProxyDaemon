package com.example.proxydaemon.ui.components.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScriptStatusCard(
    scriptStatus: Boolean,
    cardShape: RoundedCornerShape,
    cardElevation: Dp,
    successColor: Color,
    errorColor: Color
){
    // 脚本执行状态卡片
    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Companion.CenterVertically
        ) {
            // 状态指示圆形
            Box(
                modifier = Modifier.Companion
                    .size(52.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp))
                    .background(
                        brush = Brush.Companion.radialGradient(
                            colors = if (scriptStatus)
                                listOf(successColor.copy(alpha = 0.7f), successColor)
                            else
                                listOf(errorColor.copy(alpha = 0.7f), errorColor)
                        )
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Companion.Center
            ) {
                Icon(
                    imageVector = if (scriptStatus) Icons.Outlined.Build else Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color.Companion.White,
                    modifier = Modifier.Companion.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.Companion.width(16.dp))

            Column {
                Text(
                    text = "脚本状态",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Companion.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.Companion.height(4.dp))

                Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                    Icon(
                        imageVector = if (scriptStatus)
                            Icons.Outlined.CheckCircle
                        else
                            Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.Companion.size(16.dp),
                        tint = if (scriptStatus) successColor else errorColor
                    )
                    Spacer(modifier = Modifier.Companion.width(4.dp))
                    Text(
                        text = if (scriptStatus) "脚本已运行" else "脚本未运行",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Companion.Medium,
                        color = if (scriptStatus) successColor else errorColor
                    )
                }
            }
        }
    }
}