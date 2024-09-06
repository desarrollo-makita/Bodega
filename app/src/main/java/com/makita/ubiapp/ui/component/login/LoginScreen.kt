package com.makita.ubiapp.ui.component.login

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

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
import androidx.compose.ui.platform.LocalConfiguration
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
import com.makita.ubiapp.ui.component.database.AppDatabase
import com.makita.ubiapp.ui.component.entity.LoginEntity
import com.makita.ubiapp.ui.component.ubicaciones.formatTimestamp
import com.makita.ubiapp.ui.dialogs.PasswordRecoveryDialog
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String, String, Long , Int) -> Unit) {
    var nombreUsuario by remember { mutableStateOf(TextFieldValue()) }
    var clave by remember { mutableStateOf(TextFieldValue()) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var isUsernameFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val appVersion = getAppVersion(context)
    val db = AppDatabase.getDatabase(context)
    val loginDao = db.loginDao()
    val coroutineScope = rememberCoroutineScope()

    // Envuelve el contenido en un Scrollable Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .verticalScroll(rememberScrollState()), // Permite el desplazamiento vertical
        verticalArrangement = Arrangement.Center,
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
                        fontSize = 18.sp,
                        fontWeight = if (isUsernameFocused) FontWeight.Bold else FontWeight.Bold, color = GreenMakita
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    isUsernameFocused = it.isFocused
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            textStyle = TextStyle(color = GreenMakita, fontSize = 15.sp, fontWeight = FontWeight.Bold),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = GreenMakita,
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
                        fontWeight = if (isUsernameFocused) FontWeight.Bold else FontWeight.Bold, color = GreenMakita
                    )
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    isPasswordFocused = it.isFocused
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            textStyle = TextStyle(color = GreenMakita, fontSize = 15.sp, fontWeight = FontWeight.Bold),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = GreenMakita,
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
                        val response = RetrofitClient.apiService.login(request)
                        Log.d("*MAKITA*", "RESPONSE $response $nombreUsuario.text ")
                        if (response.isSuccessful) {
                            response.body()?.let { loginResponse ->
                                val userData = loginResponse.data
                                Log.d("*MAKITA*", "userData $userData")
                                if (userData != null) {
                                    val loginEntity = LoginEntity(
                                        username = nombreUsuario.text,
                                        password = clave.text,
                                        loginTime = formatTimestamp(System.currentTimeMillis())
                                    )

                                    Log.d("*MAKITA*", "loginEntity $loginEntity")
                                    loginDao.insertLogin(loginEntity)

                                    onLoginSuccess(userData.NombreUsuario, userData.Area , 22 , userData.UsuarioID)
                                } else {
                                    errorState = "Datos de usuario vacíos"
                                    Log.e("*MAKITA*", "El campo 'data' en la respuesta es nulo")
                                }
                            } ?: run {
                                errorState = "Respuesta vacía del servidor"
                                Log.e("*MAKITA*", "Respuesta vacía del servidor")
                            }
                        } else {
                            errorState = "Nombre de usuario o contraseña incorrectos. Por favor, inténtalo de nuevo."
                        }

                    } catch (e: Exception) {
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

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = GreenMakita,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable { showRecoveryDialog = true },
            style = TextStyle(
                fontWeight = FontWeight.Bold // Aplica negrita
            )

        )

        Spacer(modifier = Modifier.height(100.dp))

        Text(
            text = "Versión $appVersion",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }

    if (showRecoveryDialog) {
        PasswordRecoveryDialog(onDismiss = { showRecoveryDialog = false })
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





