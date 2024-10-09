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
import com.makita.ubiapp.ui.dialogs.ReplacePasswordDialog

import com.makita.ubiapp.ui.theme.UbiAppTheme
import com.makita.ubiapp.ui.util.DeviceInfoUtil



class MainActivity : ComponentActivity() {
    var showPasswordDialog by mutableStateOf(false)
    var userIdForDialog by mutableStateOf(0)
    var usernameDialog by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UbiAppTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Log.d("*MAKITA*", "Iniciamos MainActivity - UbiAppTheme")
                    val navController = rememberNavController()
                    SetupNavGraph(navController = navController)
                    if (showPasswordDialog) {
                        ReplacePasswordDialog(
                            onDismiss = { showPasswordDialog = false },
                            idUsuarioInicial = userIdForDialog,
                            nombreUsuarioInicial = usernameDialog
                        )
                    }
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

                    onLoginSuccess = { username, area ,vigencia,idUsuario ,token , recuperarClave ->
                        Log.d("*MAKITA*", "SetupNavGraph :  $username , $area ,  $vigencia ,$idUsuario ,  $token , $recuperarClave" )

                        DeviceInfoUtil.registrarInformacionDispositivo(context = this@MainActivity, nombreUsuario = username)

                        if(recuperarClave === 1){

                            showPasswordDialog = true
                            userIdForDialog = idUsuario.toInt()
                            usernameDialog = username
                        }else{

                            navController.navigate("menu/$username/$area/${vigencia}/${idUsuario}/${token}/${recuperarClave}")
                        }

                    }
                )
            }
            composable("menu/{username}/{area}/{vigencia}/{idUsuario}/{token}/{recuperarClave}") { backStackEntry ->

                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""
                val idUsuarioString = backStackEntry.arguments?.getString("idUsuario") ?: "0"
                val idUsuario = idUsuarioString.toIntOrNull() ?: 0

                val vigenciaString = backStackEntry.arguments?.getString("vigencia") ?: "0"
                val vigencia = vigenciaString.toLongOrNull() ?: 0L
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val recuperarClave =  backStackEntry.arguments?.getInt("recuperarClave") ?: 0

                Log.d("*MAKITA*", "MainActivity 01 : $username , $area  ,$vigencia,$idUsuario,$token, $recuperarClave " )
                MenuScreen(
                    nombreUsuario = username, // jherreras
                    area = area, // herramientas
                    vigencia = vigencia, // 52
                    idUsuario = idUsuario,
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
