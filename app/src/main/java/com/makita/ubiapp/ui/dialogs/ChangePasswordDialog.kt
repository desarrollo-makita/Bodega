package com.makita.ubiapp.ui.dialogs

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.ui.theme.GreenMakita

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (currentPassword: String, newPassword: String, confirmPassword: String) -> Unit
) {
    // Estados para los campos de entrada
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Visualización de la contraseña
    val visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()

    // Validaciones de los requisitos de contraseña
    val hasMinLength = newPassword.length >= 8
    val hasUpperCase = newPassword.any { it.isUpperCase() }
    val hasSpecialChar = newPassword.any { !it.isLetterOrDigit() }
    val isPasswordValid = hasMinLength && hasUpperCase && hasSpecialChar
    val doPasswordsMatch = newPassword == confirmPassword
    val isButtonEnabled = currentPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank() && isPasswordValid && doPasswordsMatch

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Clave", fontSize = 20.sp) },
        text = {
            Column {
                // Clave actual
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Clave Actual") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Clave Actual") },
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                // Nueva clave
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("Nueva Clave") },
                    leadingIcon = { Icon(Icons.Filled.LockOpen, contentDescription = "Nueva Clave") },
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                // Confirmar clave
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Clave") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Confirmar Clave") },
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                // Mensajes de validación
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
                onClick = { onConfirm(currentPassword, newPassword, confirmPassword) },
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
        containerColor = Color.White // Cambia el color del fondo del diálogo si es necesario
    )
}
