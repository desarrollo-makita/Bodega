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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.CambioClaveRequest
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        vigencia: Long,
        idUsuario: Int,
        nombreUsuario: String
    ) -> Unit,
    idUsuarioInicial: Int,         // Agregamos idUsuario inicial como parámetro
    nombreUsuarioInicial: String,  // Agregamos nombreUsuario inicial como parámetro
    vigenciaInicial: Long          // Agregamos vigencia inicial como parámetro
) {
    // Inicializa los estados con los valores iniciales proporcionados
    var idUsuario by remember { mutableStateOf(idUsuarioInicial) }
    var nombreUsuario by remember { mutableStateOf(nombreUsuarioInicial) }
    var vigencia by remember { mutableStateOf(vigenciaInicial) }

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isCurrentPasswordValid by remember { mutableStateOf<Boolean?>(null) }

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
        val cambioClaveRequest = CambioClaveRequest(nombreUsuario, currentPassword, idUsuario)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.e("*MAKITA*", "Request: $cambioClaveRequest")
                val response = RetrofitClient.apiService.validarClaveActual(cambioClaveRequest)
                val responseBody = response.body()
                Log.e("*MAKITA*", "Response: ${responseBody.toString()}")
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isCurrentPasswordValid = false
                    Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar Clave", fontSize = 20.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Clave Actual") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = "Clave Actual") },
                    visualTransformation = visualTransformation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused && currentPassword.isNotEmpty()) {
                                // Llamamos a validarClaveActual cuando el usuario pierde el foco
                                validarClaveActual(idUsuario, currentPassword, nombreUsuario)
                            }
                        }
                )
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
                onClick = { onConfirm(vigencia, idUsuario, nombreUsuario) },
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
}
