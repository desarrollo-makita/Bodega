package com.makita.ubiapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.makita.ubiapp.login.LoginScreen
import com.makita.ubiapp.ui.theme.UbiAppTheme
import com.makita.ubiapp.ubicaciones.UbicacionScreen


class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UbiAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Log.d("*MAKITA*", "01 ")
                    val navController = rememberNavController()
                    SetupNavGraph(navController = navController)
                }
            }
        }
    }

    @Composable
    fun SetupNavGraph(navController: NavHostController) {
        NavHost(
            navController = navController,
            startDestination = "login"
        ) {
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { username, password ->
                        Log.d("*MAKITA*", "SQLITE 02 : ${username}")
                        navController.navigate("ubicacion/$username")
                    }
                )
            }
            composable("ubicacion/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                Log.d("*MAKITA*", "backStackEntry 03 : $username")
                UbicacionScreen(username)
            }
        }
    }



}
