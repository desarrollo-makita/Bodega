package com.makita.ubiapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.makita.ubiapp.ui.component.capturaSerie.CabeceraDocumentoScreen
import com.makita.ubiapp.ui.component.capturaSerie.DetalleDocumentoScreen
import com.makita.ubiapp.ui.component.capturaSerie.ProcesarSinCodigoBarraScreen
import com.makita.ubiapp.ui.component.etiquetas.EtiquetasScreen
import com.makita.ubiapp.ui.component.login.LoginScreen
import com.makita.ubiapp.ui.component.ubicaciones.CapturaSerieScreen
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

                    onLoginSuccess = { username, area ,vigencia,idUsuario ,token , recuperarClave, actividades ->

                        DeviceInfoUtil.registrarInformacionDispositivo(context = this@MainActivity, nombreUsuario = username)

                        if(recuperarClave === 1){
                            Log.d("*MAKITA" , "MainActivity- if")
                            showPasswordDialog = true
                            userIdForDialog = idUsuario.toInt()
                            usernameDialog = username
                        }else{

                            val gson = Gson()

                            val actividadesJson = gson.toJson(actividades)

                            val actividadesJsonEncoded = Uri.encode(gson.toJson(actividades))
                            navController.navigate("menu/$username/$area/$vigencia/$idUsuario/$token/$recuperarClave/$actividadesJsonEncoded")
                        }

                    }
                )
            }
            composable(
                "menu/{username}/{area}/{vigencia}/{idUsuario}/{token}/{recuperarClave}/{actividadesJson}",
                arguments = listOf(
                    navArgument("username") { type = NavType.StringType },
                    navArgument("area") { type = NavType.StringType },
                    navArgument("vigencia") { type = NavType.StringType },
                    navArgument("idUsuario") { type = NavType.StringType },
                    navArgument("token") { type = NavType.StringType },
                    navArgument("recuperarClave") { type = NavType.IntType },
                    navArgument("actividadesJson") { type = NavType.StringType }
                )
            ) { backStackEntry ->

                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""
                val idUsuarioString = backStackEntry.arguments?.getString("idUsuario") ?: "0"
                val idUsuario = idUsuarioString.toIntOrNull() ?: 0

                val vigenciaString = backStackEntry.arguments?.getString("vigencia") ?: "0"
                val vigencia = vigenciaString.toLongOrNull() ?: 0L
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val recuperarClave = backStackEntry.arguments?.getInt("recuperarClave") ?: 0
                val actividadesJson = backStackEntry.arguments?.getString("actividadesJson")

                // Check if actividadesJson is valid before deserializing
                if (!actividadesJson.isNullOrEmpty()) {

                    // Safe deserialization of actividadesJson
                    val gson = Gson()
                    val actividades = try {
                        gson.fromJson(actividadesJson, Array<ActividadItem>::class.java).toList()
                    } catch (e: Exception) {
                        // Log deserialization error
                        Log.e("MainActivity", "Error deserializing actividadesJson: ${e.message}")
                        emptyList<ActividadItem>()
                    }

                    // Proceed to the MenuScreen with deserialized data
                    MenuScreen(
                        nombreUsuario = username,
                        area = area,
                        vigencia = vigencia,
                        idUsuario = idUsuario,
                        token = token,
                        actividades = actividades,
                        navController = navController
                    )
                } else {
                    Log.e("*MainActivity", "actividadesJson is null or empty")
                }
            }

            composable("ubicacion/{username}") { backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                UbicacionScreen(username = username )
            }

            composable("picking/{username}/{area}/{vigencia}/{idusuario}/{token}/{actividadesJson}") {backStackEntry ->
                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""
                val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                val vigencia = backStackEntry.arguments?.getString("vigencia")?.toLongOrNull() ?: 0L
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val actividadesJson = backStackEntry.arguments?.getString("actividadesJson") ?: ""
                val actividades = Gson().fromJson(actividadesJson, Array<ActividadItem>::class.java).toList()

                CapturaSerieScreen(navController, username, area, vigencia, idUsuario,token, actividades)
            }


            composable("cabecera-documento/{item}/{username}/{area}/{vigencia}/{idusuario}/{token}/{actividadesJson}") { backStackEntry ->
                val itemJson = backStackEntry.arguments?.getString("item")
                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""
                val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                val vigencia = backStackEntry.arguments?.getString("vigencia")?.toLongOrNull() ?: 0L
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val actividadesJson = backStackEntry.arguments?.getString("actividadesJson") ?: ""
                val actividades = Gson().fromJson(actividadesJson, Array<ActividadItem>::class.java).toList()
                if (itemJson != null) {
                    val pickingItem = Gson().fromJson(itemJson, PickingItem::class.java)
                    CabeceraDocumentoScreen(navController, pickingItem, username , area , vigencia, idUsuario,token,actividades)
                } else {
                    // Manejar el caso donde itemJson es null, quizás mostrar un mensaje de error
                    Log.e("Navigation", "itemJson es null")
                }

            }

            composable("detalle-documento/{item}/{username}/{area}/{vigencia}/{idusuario}/{token}/{actividadesJson}") { backStackEntry ->
                val itemJson = backStackEntry.arguments?.getString("item")

                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""
                val idUsuario = backStackEntry.arguments?.getString("idUsuario")?.toIntOrNull() ?: 0
                val vigencia = backStackEntry.arguments?.getString("vigencia")?.toLongOrNull() ?: 0L
                val token = backStackEntry.arguments?.getString("token") ?: ""
                val actividadesJson = backStackEntry.arguments?.getString("actividadesJson") ?: ""
                val actividades = Gson().fromJson(actividadesJson, Array<ActividadItem>::class.java).toList()

                if (itemJson != null) {
                    val pickingItem = Gson().fromJson(itemJson, PickingItem::class.java)
                    DetalleDocumentoScreen(navController, pickingItem ,username,area,vigencia,idUsuario,token, actividades)
                } else {
                    // Manejar el caso donde itemJson es null, quizás mostrar un mensaje de error
                    Log.e("Navigation", "itemJson es null")
                }
            }

            composable("procesar-sin-codigo-barra/{item}/{correlativoOrigen}/{correlativo}/{username}/{area}") { backStackEntry ->

                val itemJson = backStackEntry.arguments?.getString("item")
                val correlativoOrigen = backStackEntry.arguments?.getString("correlativoOrigen")?.toIntOrNull() ?: 0
                val correlativo = backStackEntry.arguments?.getString("correlativo")?.toIntOrNull() ?: 0
                val username = backStackEntry.arguments?.getString("username") ?: ""
                val area = backStackEntry.arguments?.getString("area") ?: ""

                if (itemJson != null) {
                    val pickingItem = Gson().fromJson(itemJson, PickingDetalleItem::class.java)
                    ProcesarSinCodigoBarraScreen(navController , pickingItem, correlativoOrigen , correlativo , username, area)
                }

            }

            composable("etiquetado/") { backStackEntry ->
                EtiquetasScreen()
            }

        }
    }

}
