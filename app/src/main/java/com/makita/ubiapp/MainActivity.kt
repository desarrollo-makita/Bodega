package com.makita.ubiapp

import MenuScreen
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
import com.makita.ubiapp.ui.component.login.LoginScreen
import com.makita.ubiapp.ui.component.ubicaciones.UbicacionScreen

import com.makita.ubiapp.ui.theme.UbiAppTheme



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UbiAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Log.d("*MAKITA*", "Iniciamos MainActivity")
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
                    onLoginSuccess = { username, area , vigencia ->
                        Log.d("*MAKITA*", "SQLITE 02 : ${username} ,  ${area} , ${vigencia}")
                        navController.navigate("menu/$username/$area/${vigencia}")
                    }
                )
            }
            composable("menu/{username}/{area}/{vigencia}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""
                // Extraer vigencia como cadena y luego convertirla a Long
                val vigenciaString = backStackEntry.arguments?.getString("vigencia") ?: "0"
                val vigencia = vigenciaString.toLongOrNull() ?: 0L
                Log.d("*MAKITA*", "backStackEntry 03 : $username , $area , $vigencia")
                MenuScreen(nombreUsuario = username, area = area, vigencia = vigencia ,navController = navController)
            }
            composable("ubicacion/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                UbicacionScreen(username = username)
            }

        }
    }



}
