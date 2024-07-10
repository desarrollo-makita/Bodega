package com.makita.ubiapp

import android.os.Bundle
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
                    onLoginSuccess = {
                        navController.navigate("ubicacion")
                    }
                )
            }
            composable("ubicacion") {
                UbicacionScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        // Aquí puedes poner el contenido de tu pantalla principal
        Text(text = "Bienvenido a la pantalla principal")
    }


}
