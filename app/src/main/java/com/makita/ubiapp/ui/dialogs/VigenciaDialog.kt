package com.makita.ubiapp.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun VigenciaDialog(vigencia:Long ,
                   onDismiss: () -> Unit,
                   onUpdatePassword: () -> Unit) {

    // Estado para controlar la visibilidad de ChangePasswordDialog
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    // Mostrar ChangePasswordDialog si showChangePasswordDialog es true
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onConfirm = { currentPassword, newPassword, confirmPassword ->
                // Lógica para manejar la actualización de la clave
                // Por ejemplo, llamar a un servicio para cambiar la clave
                // Luego ocultar el diálogo
                showChangePasswordDialog = false
            }
        )
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aviso de Vigencia") },
        text = { Text("La vigencia expira en ${vigencia} dias.") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        },
        dismissButton = {
            Button(onClick = { showChangePasswordDialog = true }) { // Muestra el diálogo para actualizar la clave
                Text("Actualizar Clave")
            }
        }
    )
}
