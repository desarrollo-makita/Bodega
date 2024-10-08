package com.makita.ubiapp.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.RecuperarRequest
import com.makita.ubiapp.RetrofitClient.apiService
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PasswordRecoveryDialog(onDismiss: () -> Unit) {
    var username by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var successMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Recuperar Contraseña",
                style = TextStyle(
                fontSize = 20.sp,
                color = GreenMakita,
                fontWeight = FontWeight.Bold
            ))
            },
        text = {
            Column {
                Text("Introduce tu nombre de usuario:",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = GreenMakita,

                    ))
                Spacer(modifier = Modifier.height(25.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },

                    label = { Text("Nombre de usuario",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = GreenMakita,
                            fontWeight = FontWeight.Bold
                        )) },
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth(),

                )
                errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }
                // Mostrar mensaje de éxito si está disponible
                if (successMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = successMessage,
                        color = GreenMakita,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (username.text.isEmpty()) {
                        errorMessage = "El nombre de usuario no puede estar vacío"
                    } else {
                        coroutineScope.launch {
                            val request = RecuperarRequest(usuario = username.text)
                            val response = apiService.recuperarPassword(request)

                            if (response.isSuccessful) {
                                Log.d("*MAKITA", "RESPONSE: ${response.body()}")

                                // Verificar si el cuerpo de la respuesta no es null
                                val responseBody = response.body()
                                if (responseBody != null && responseBody.mensaje.existe == true) {
                                    successMessage = "Se ha enviado una clave temporal a su correo"

                                    // Mostrar el mensaje por 2 segundos
                                    delay(3000)
                                    successMessage = ""  // Limpia el mensaje después de 2 segundos
                                    onDismiss()
                                } else {
                                    errorMessage = "Ingrese un nombre de usuario válido"

                                    // Mostrar el mensaje por 2 segundos
                                    delay(3000)
                                    errorMessage = ""  // Limpia el mensaje después de 2 segundos
                                }
                            }else{
                                errorMessage = "Ingrese un nombre de usuario valido"

                                // Mostrar el mensaje por 2 segundos
                                delay(3000)
                                errorMessage = ""  // Limpia el mensaje después de 2 segundos


                            }
                        }

                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E))
            ) {
                Text("Enviar")
            }
        },


        dismissButton = {
            Button(onClick = { onDismiss() } ,    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E))) {
                Text("Cancelar")
            }
        }
    )
}

