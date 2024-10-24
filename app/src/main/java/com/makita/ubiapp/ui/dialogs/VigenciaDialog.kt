package com.makita.ubiapp.ui.dialogs

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


@Composable
fun VigenciaDialog(vigencia:Long ,
                   idUsuario:Int,
                   nombreUsuario : String,
                   token : String,
                   onDismiss: () -> Unit) {

    // Estado para controlar la visibilidad de ChangePasswordDialog
    var showChangePasswordDialog by remember { mutableStateOf(false) }

    if (!showChangePasswordDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Aviso de Vigencia") },
            text = { Text("La vigencia expira en ${vigencia} dias." ,  fontSize = 15.sp , fontWeight = FontWeight.Bold) },
            confirmButton = {
                Button(onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)  // GreenMakita
                    )) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showChangePasswordDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)  // GreenMakita
                    )
                    ) { // Muestra el diálogo para actualizar la clave
                        Text("Cambiar Clave")
                        Log.e("*MAKITA*", "Vigencia $vigencia , $idUsuario, $nombreUsuario ,$token")
                    }
            }
        )

    }


    // Mostrar ChangePasswordDialog si showChangePasswordDialog es true
    if (showChangePasswordDialog) {

        ChangePasswordDialog(
            onDismiss = {
                showChangePasswordDialog = false
                onDismiss() },
            idUsuarioInicial = idUsuario,  // Pasa el valor dinámico de vigencia que ya tienes
            nombreUsuarioInicial = nombreUsuario,  // Pasa el valor dinámico de idUsuario que ya tienes
            token = token
        )
    }

}
