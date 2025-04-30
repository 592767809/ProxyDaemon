package com.example.proxydaemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.proxydaemon.ui.ProxyManagerScreen
import com.example.proxydaemon.ui.RootCheckScreen
import androidx.navigation.compose.composable



@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "root_check") {
        composable("root_check") {
            RootCheckScreen(
                onRootSuccess = {
                    navController.navigate("proxy_manager") {
                        popUpTo("root_check") { inclusive = true }
                    }
                }
            )
        }

        composable("proxy_manager") {
            ProxyManagerScreen()
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
//            RootCheckScreen()
//            ProxyManagerScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}

//@Preview(showBackground = true)
//@Composable
//fun RootCheckScreenPreview() {
//    RootCheckScreen()
//}


@Preview(showBackground = true)
@Composable
fun ProxyManagerScreenPreview() {
    ProxyManagerScreen()
}
