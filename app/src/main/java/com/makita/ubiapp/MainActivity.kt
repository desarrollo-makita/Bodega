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
import com.makita.ubiapp.ui.component.login.LoginScreen
import com.makita.ubiapp.ui.component.ubicaciones.UbicacionScreen
import com.makita.ubiapp.ui.dialogs.MenuScreen

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
                    onLoginSuccess = { username, area , idUsuario, vigencia , token ->

                        navController.navigate("menu/$username/$area/$idUsuario/${vigencia}/${token}")
                    }
                )
            }
            composable("menu/{username}/{area}/{vigencia}/{idUsuario}/{token}") { backStackEntry ->

                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""
                val idUsuarioString = backStackEntry.arguments?.getString("idUsuario") ?: "0"
                val idUsuario = idUsuarioString.toIntOrNull() ?: 0

                val vigenciaString = backStackEntry.arguments?.getString("vigencia") ?: "0"
                val vigencia = vigenciaString.toLongOrNull() ?: 0L
                val token = backStackEntry.arguments?.getString("token") ?: ""
                Log.d("*MAKITA*", "backStackEntry 03 : $username , $area  ,$vigencia, $idUsuario")
                MenuScreen(
                    nombreUsuario = username,
                    area = area,
                    idUsuario = idUsuario,
                    vigencia = vigencia,
                    token = token,
                    navController = navController
                )
            }
            composable("ubicacion/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                UbicacionScreen(username = username)
            }

        }
    }



}
