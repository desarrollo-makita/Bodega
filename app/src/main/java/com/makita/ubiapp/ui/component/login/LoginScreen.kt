// Archivo: LoginScreen.kt
package com.makita.ubiapp.ui.component.login

import android.content.Context
import android.content.pm.PackageManager
import kotlinx.coroutines.delay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.R

import com.makita.ubiapp.ui.dialogs.PasswordRecoveryDialog
import com.makita.ubiapp.ui.theme.GreenMakita
import androidx.lifecycle.viewmodel.compose.viewModel
import com.makita.ubiapp.ActividadItem


@Composable
fun LoginScreen(onLoginSuccess: (String, String, Long, Int, String , Int , List<ActividadItem>) -> Unit) {

    val loginViewModel: LoginViewModel = viewModel()
    val context = LocalContext.current
    val appVersion = getAppVersion(context)
    var showRecoveryDialog by remember { mutableStateOf(false) }
    var isPasswordVisible by remember { mutableStateOf(false) }

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
            value = loginViewModel.nombreUsuario.value,
            onValueChange = { loginViewModel.nombreUsuario.value = it },
            label = {
                Text(
                    text = "Usuario",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = if (loginViewModel.isUsernameFocused.value) FontWeight.Bold else FontWeight.Bold,
                        color = GreenMakita
                    )
                )
            },

            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    loginViewModel.isUsernameFocused.value = it.isFocused
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
            value = loginViewModel.clave.value,
            onValueChange = { loginViewModel.clave.value = it },
            label = {
                Text(
                    text = "Password",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = if (loginViewModel.isPasswordFocused.value) FontWeight.Bold else FontWeight.Bold,
                        color = GreenMakita
                    )
                )
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),

            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    loginViewModel.isPasswordFocused.value = it.isFocused
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            textStyle = TextStyle(color = GreenMakita, fontSize = 15.sp, fontWeight = FontWeight.Bold),
            singleLine = true,
            trailingIcon = {
                // Aquí se añade el ícono para mostrar/ocultar la contraseña
                val icon = if (isPasswordVisible) {
                    Icons.Default.Visibility // Asegúrate de tener este ícono en tu drawable
                } else {
                    Icons.Default.VisibilityOff// Asegúrate de tener este ícono en tu drawable
                }

                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = if (isPasswordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = GreenMakita,
                focusedLabelColor = GreenMakita,
                cursorColor = GreenMakita,
            )
        )

        // Mostrar mensaje de error si hay uno
        loginViewModel.errorState.value?.let { error ->
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

        LaunchedEffect(loginViewModel.errorState.value) {
            if (loginViewModel.errorState.value != null) {
                delay(2000)
                loginViewModel.errorState.value = null
            }
        }

        // Espacio entre los elementos
        Spacer(modifier = Modifier.height(25.dp))

        // Botón de ingreso
        Button(
            onClick = {

                loginViewModel.login { username, area, vigencia, idUsuario,token , recuperarClave , actividades->
                    onLoginSuccess(username, area, vigencia,idUsuario , token , recuperarClave , actividades)
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
