package com.makita.ubiapp.ui.dialogs

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.CambioClaveRequest
import com.makita.ubiapp.Data
import com.makita.ubiapp.ReplaceClaveRequest
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.RetrofitClient.apiService
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ReplacePasswordDialog(
    onDismiss: () -> Unit,
    idUsuarioInicial: Int,
    nombreUsuarioInicial: String
) {
    // Inicializa los estados con los valores iniciales proporcionados
    var idUsuario by remember { mutableStateOf(idUsuarioInicial) }
    var nombreUsuario by remember { mutableStateOf(nombreUsuarioInicial) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isCurrentPasswordValid by remember { mutableStateOf(false) }
    var isFocused by remember { mutableStateOf(false) } // Controla si el campo está enfocado
    var isFocusedConfirmarClave by remember { mutableStateOf(false) } // Controla si el campo está enfocado

    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var dialogMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()

    // Validaciones de los requisitos de contraseña
    val hasMinLength = newPassword.length >= 8
    val hasUpperCase = newPassword.any { it.isUpperCase() }
    val hasSpecialChar = newPassword.any { !it.isLetterOrDigit() }
    val isPasswordValid = hasMinLength && hasUpperCase && hasSpecialChar
    val doPasswordsMatch = newPassword == confirmPassword
    val isButtonEnabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank() && isPasswordValid && doPasswordsMatch

    // Función para validar la clave actual cuando se pierde el foco del campo
    fun validarClaveActual(idUsuario: Int, currentPassword: String, nombreUsuario: String) {

        val cambioClaveRequest = CambioClaveRequest(nombreUsuario, currentPassword, idUsuario, ""  )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("*MAKITA*", "Request: $cambioClaveRequest")

                val response = RetrofitClient.apiService.validarClaveActual(cambioClaveRequest)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()
                    Log.e("*MAKITA*", "Response: ${responseBody.toString()}")

                    // Cambiamos el estado en el hilo principal (UI)
                    withContext(Dispatchers.Main) {
                        // Actualizamos el estado isCurrentPasswordValid basado en la respuesta
                        isCurrentPasswordValid = responseBody?.status == 200
                    }
                }else{
                    // Manejo de error en la respuesta
                    withContext(Dispatchers.Main) {
                        isCurrentPasswordValid = false
                        Toast.makeText(context, "Error en la validación: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isCurrentPasswordValid = false
                    Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Actualizar la validez de la clave mientras el usuario escribe
    LaunchedEffect(currentPassword) {
        Log.e("*MAKITA*", "Response: $currentPassword")
        if (currentPassword.isNotEmpty()) {
            validarClaveActual(idUsuario, currentPassword, nombreUsuario)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Clave Temporal", fontSize = 20.sp , fontWeight = FontWeight.Bold , color = GreenMakita) },
        text = {
            Column {
                OutlinedTextField(

                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = {
                        Text(
                            text = "Clave Temporal",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = GreenMakita
                            )
                        ) },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isCurrentPasswordValid) Icons.Filled.LockOpen else Icons.Filled.Lock,
                            contentDescription = "Clave Actual",
                            tint = if (isCurrentPasswordValid) GreenMakita else Color.Gray
                        )
                    },
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused && currentPassword.isNotEmpty()) {
                                // Llamamos a validarClaveActual cuando el usuario pierde el foco
                                validarClaveActual(idUsuario, currentPassword, nombreUsuario)
                            }
                        },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenMakita,
                        unfocusedBorderColor = GreenMakita,
                        focusedLabelColor = GreenMakita,
                        cursorColor = GreenMakita,
                    )

                )
                if (isCurrentPasswordValid) {
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("Nueva Clave" ,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = GreenMakita
                            )) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isPasswordValid) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                contentDescription = "Nueva Clave",
                                tint = if (isPasswordValid) GreenMakita else Color.Gray
                            )
                        },
                        visualTransformation = visualTransformation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused // Cambia el estado cuando el campo se enfoca
                            },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenMakita,
                            unfocusedBorderColor = GreenMakita,
                            focusedLabelColor = GreenMakita,
                            cursorColor = GreenMakita,
                            ),
                    )

                }
                if (isPasswordValid) {
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar Clave",style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = GreenMakita
                        )) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isFocusedConfirmarClave && doPasswordsMatch) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                contentDescription = "Confirmar Clave",
                                tint = if (isFocusedConfirmarClave && doPasswordsMatch) GreenMakita else Color.Gray
                            )
                        },
                        visualTransformation = visualTransformation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .onFocusChanged { focusState ->
                                isFocusedConfirmarClave = focusState.isFocused // Cambia el estado cuando el campo se enfoca

                            },

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenMakita,
                            unfocusedBorderColor = GreenMakita,
                            focusedLabelColor = GreenMakita,
                            cursorColor = GreenMakita,
                        ),

                        )

                }


                // Mostrar las reglas de contraseña solo cuando el campo esté enfocado
                if (isFocused) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            "• Debe tener al menos 8 caracteres",
                            color = if (hasMinLength) GreenMakita else Color.Red,
                            fontSize = 14.sp
                        )
                        Text(
                            "• Debe contener una letra mayúscula",
                            color = if (hasUpperCase) GreenMakita else Color.Red,
                            fontSize = 14.sp
                        )
                        Text(
                            "• Debe contener un carácter especial",
                            color = if (hasSpecialChar) GreenMakita else Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
                    if (isFocusedConfirmarClave) {
                        Text(
                            if (doPasswordsMatch) "Las contraseñas coinciden" else "Las contraseñas no coinciden",
                            color = if (doPasswordsMatch) GreenMakita else Color.Red,
                            fontSize = 14.sp

                        )
                    }

                }

        },
        confirmButton = {
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        replacePassword(idUsuario, confirmPassword, nombreUsuario,  "",
                            { message ->
                                dialogMessage = message
                                showSuccessDialog = true
                            },
                            { message ->
                                dialogMessage = message
                                showErrorDialog = true
                            })
                    }
                },
                enabled = isButtonEnabled
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = Color.White
    )

    // Muestra un diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Éxito" ,  fontSize = 20.sp , fontWeight = FontWeight.Bold , color = GreenMakita) },
            text = { Text(dialogMessage ,  fontSize = 15.sp ) },
            confirmButton = {
                Button(onClick = {
                    showSuccessDialog = false
                    onDismiss()
                }) {
                    Text("Aceptar")

                }
            }
        )
    }

    // Muestra un diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                onDismiss()
            },
            title = { Text("Error",  fontSize = 20.sp , fontWeight = FontWeight.Bold , color = Color.Red) },
            text = { Text(dialogMessage,  fontSize = 15.sp , fontWeight = FontWeight.Bold ) },
            confirmButton = {
                Button(onClick = {
                    showErrorDialog = false
                    onDismiss()
                }) {

                    Text("Aceptar")
                }
            }
        )
    }
}

suspend fun replacePassword(idUsuario: Int, confirmPassword: String, nombreUsuario: String , token: String , onSuccess: (String) -> Unit,
                           onError: (String) -> Unit) {
    try {
        Log.e("*MAKITA*", "replacePassword: ${idUsuario} , ${confirmPassword} , ${nombreUsuario} , ${token}" )
        val response = apiService.replaceClave(
            ReplaceClaveRequest(
                data = Data(
                    idUsuario = idUsuario,
                    password = confirmPassword,

                )
            )
        )

        if (response.isSuccessful) {
            // Llama a onSuccess con el mensaje deseado

            onSuccess("La contraseña ha sido cambiada exitosamente.")
        } else {
            // Manejo de error en la respuesta

            onError("Error: ${response.message()}")
        }

    } catch (e: Exception) {
        Log.e("*MAKITA*", "ResponseError:${e.message}")
    }
}


