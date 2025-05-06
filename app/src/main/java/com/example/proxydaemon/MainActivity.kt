package com.example.proxydaemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import com.example.proxydaemon.ui.screen.ProxyMainScreen
import com.example.proxydaemon.util.RootShell


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProxyMainScreen()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        RootShell.close()
    }
}



@Preview(showBackground = true)
@Composable
fun ProxyManagerScreenPreview() {
    ProxyMainScreen()
}
