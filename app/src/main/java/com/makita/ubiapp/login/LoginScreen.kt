package com.makita.ubiapp.login

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.LoginRequest
import com.makita.ubiapp.R
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.database.AppDatabase
import com.makita.ubiapp.entity.LoginEntity
import com.makita.ubiapp.ubicaciones.formatTimestamp
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
    // Variables para almacenar el estado de los campos de texto
    var nombreUsuario by remember { mutableStateOf(TextFieldValue()) }
    var clave by remember { mutableStateOf(TextFieldValue()) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var isUsernameFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }

    // Obtener el contexto y la versión de la aplicación
    val context = LocalContext.current
    val appVersion = getAppVersion(context)

    // Inicializar la base de datos y el DAO
    val db = AppDatabase.getDatabase(context)
    val loginDao = db.loginDao()

    // Inicializar el Scope de Coroutine
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.makitafondoblanco),
            contentDescription = "Logo de Makita",
            modifier = Modifier
                .size(150.dp)
                .padding(top = 24.dp)
        )
        // Campo de texto para el nombre de usuario
        OutlinedTextField(
            value = nombreUsuario,
            onValueChange = { nombreUsuario = it },
            label = {
                Text(
                    text = "Usuario",
                    style = TextStyle(
                        fontSize = 18.sp ,
                        fontWeight = if (isUsernameFocused) FontWeight.Bold else FontWeight.Normal,
                    )
                )
            },
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    isUsernameFocused = it.isFocused
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = GreenMakita,
                cursorColor = GreenMakita,
            )
        )
        // Campo de texto para la contraseña
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = {
                Text(
                    text = "Password",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = if (isPasswordFocused) FontWeight.Bold else FontWeight.Normal,
                    )
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    isPasswordFocused = it.isFocused
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = GreenMakita,
                cursorColor = GreenMakita,
            )
        )

        // Mostrar mensaje de error si hay uno
        errorState?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        LaunchedEffect(errorState) {
            if (errorState != null) {
                delay(2000)
                errorState = null
            }
        }

        // Espacio entre los elementos
        Spacer(modifier = Modifier.height(25.dp))

        // Botón de ingreso
        Button(
            onClick = {
                coroutineScope.launch {
                   try {

                       val request = LoginRequest(nombreUsuario.text, clave.text)

                       // Llamar al endpoint de login usando Retrofit
                       val response = RetrofitClient.apiService.login(request)
                       Log.d("*MAKITA*", "RESPONSE $response $nombreUsuario.text ")
                       if (response.isSuccessful) {
                           response.body()?.let { loginResponse ->
                               // Verifica si el campo `data` no es nulo
                               val userData = loginResponse.data
                               Log.d("*MAKITA*", "userData $userData")
                               if (userData != null) {
                                   // Guardar el login en la base de datos local
                                   val loginEntity = LoginEntity(
                                       username = nombreUsuario.text,
                                       password = clave.text,
                                       loginTime = formatTimestamp(System.currentTimeMillis())
                                   )

                                   Log.d("*MAKITA*", "loginEntity $loginEntity")
                                   loginDao.insertLogin(loginEntity)

                                   // Informar al éxito del login
                                   onLoginSuccess(userData.NombreUsuario, userData.Area)
                               } else {
                                   // Manejar el caso donde `data` es null
                                   errorState = "Datos de usuario vacíos"
                                   Log.e("*MAKITA*", "El campo 'data' en la respuesta es nulo")
                               }
                           } ?: run {
                               // Manejar caso donde la respuesta es nula
                               errorState = "Respuesta vacía del servidor"
                               Log.e("*MAKITA*", "Respuesta vacía del servidor")
                           }
                       } else {
                           // Manejar errores en la autenticación
                               errorState = "Nombre de usuario o contraseña incorrectos. Por favor, inténtalo de nuevo."
                       }

                   }catch (e: Exception) {
                       // Manejar errores de conexión
                       errorState = "Error de conexión: ${e.message}"
                       Log.e("*MAKITA*", "Error fetching logins: ${e.message}")

                   }

                }
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .width(150.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00909E)  // GreenMakita
            )
        ) {
            Text("Ingresar")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = "Versión $appVersion",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}


fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        "${pInfo.versionName} (${pInfo.versionCode})"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A"
    }
}





