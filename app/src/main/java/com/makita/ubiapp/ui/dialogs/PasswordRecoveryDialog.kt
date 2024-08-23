package com.makita.ubiapp.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import android.util.Log
import androidx.compose.ui.graphics.Color

@Composable
fun PasswordRecoveryDialog(onDismiss: () -> Unit) {
    var email by remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Recuperar Contraseña")
        },
        text = {
            Column {
                Text("Introduce tu correo electrónico para recuperar tu contraseña:")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth(),
                )
                errorMessage?.let {
                    Text(text = it, color = Color.Red)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (email.text.isEmpty()) {
                        errorMessage = "El correo electrónico no puede estar vacío"
                    } else {
                        Log.d("*MAKITA*", "Correo de recuperación enviado a: ${email.text}")
                        onDismiss()
                    }
                }
            ) {
                Text("Enviar")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}
