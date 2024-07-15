package com.makita.ubiapp.ubicaciones

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.makita.ubiapp.ActualizaUbicacionRequest
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.UbicacionResponse
import com.makita.ubiapp.database.AppDatabase
import com.makita.ubiapp.entity.RegistraUbicacionEntity


import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


import android.content.Context
import java.io.File
import java.io.FileWriter



@Composable
fun UbicacionScreen(username: String) {

    val apiService = RetrofitClient.apiService
    var text by remember { mutableStateOf(TextFieldValue()) }
    var response by remember { mutableStateOf<List<UbicacionResponse>>(emptyList()) }
    var clearRequested by remember { mutableStateOf(false) }
    var nuevaUbicacion by remember { mutableStateOf(TextFieldValue()) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    var datos by remember { mutableStateOf<List<RegistraUbicacionEntity>>(emptyList()) }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val registrarUbicacionDao = db.registrarUbicacion()
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {

                datos = registrarUbicacionDao.getAllData()
                Log.d("*MAKITA*", "la dataa de SQLite que se encuentra en la tabla : ${datos}")
            } catch (e: Exception) {
                Log.e("*MAKITA*", "Error fetching logins: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }


    LaunchedEffect(text) {
        if (text.text.isNotEmpty()) {
            println("*MAKITA* Texto ingresado: ${text.text}")
            try {
                val ubicaciones = apiService.obtenerUbicacion(text.text)
                response = ubicaciones

                // Limpiar mensajes de error y éxito previos si hubo una respuesta válida
                errorState = null
                successMessage = null

                print("*MAKITA* response $response")

                if (ubicaciones.isEmpty()) {
                    // Mostrar mensaje de error si no se encontraron datos
                    errorState = " No se encontraron datos para el item proporcionado"
                }
            } catch (e: Exception) {
                errorState = if (e.message?.contains("404") == true) {
                    "No se encontraron datos para el item proporcionado"
                } else {
                    "Error al obtener ubicación: ${e.message}"
                }
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            text,
            { newTextFieldValue ->
                val trimmedText = newTextFieldValue.text.take(20).trim()
                text = TextFieldValue(text = trimmedText)


            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState())
                .focusRequester(focusRequester),
            label = { Text("Escanear Item") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = GreenMakita,
                cursorColor = GreenMakita,
            )
        )

        // Mostrar mensaje de éxito
        successMessage?.let { message ->
            LaunchedEffect(Unit) {
                delay(3000) // Mostrar el mensaje por 2 segundos
                successMessage = null // Limpiar el mensaje después de 2 segundos
            }
            Text(
                text = message,
                color = GreenMakita,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp),

            )
        }

        // Mostrar mensaje de error si existe
        errorState?.let { errorMessage ->
            LaunchedEffect(Unit) {
                delay(2000) // Mostrar el mensaje por 2 segundos
                errorState = null // Limpiar el mensaje después de 2 segundos
                text = TextFieldValue("") // Limpiar el campo de texto
            }
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (response.isNotEmpty()) {
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        if (clearRequested) {
            text = TextFieldValue("")
            response = emptyList()
            clearRequested = false
            errorState = null
            successMessage = null
        }

        response.forEach { ubicacion ->
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = TextFieldValue(ubicacion.item),
                        onValueChange = { newValue ->
                            ubicacion.item = newValue.text
                        },
                        label = { Text("ITEM", fontWeight = FontWeight.Bold) },
                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 20.sp),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenMakita,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = GreenMakita,
                            cursorColor = GreenMakita,
                        )
                    )

                    OutlinedTextField(
                        value = TextFieldValue(ubicacion.Ubicacion.takeUnless { it.isEmpty() } ?: "Sin Ubicación"),
                        onValueChange = { newValue ->
                            ubicacion.Ubicacion = newValue.text
                        },
                        label = { Text("UBICACIÓN", fontWeight = FontWeight.Bold) },
                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = Color.Red , fontSize = 20.sp),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenMakita,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = GreenMakita,
                            cursorColor = GreenMakita,
                        )
                    )
                }

                OutlinedTextField(
                    value = TextFieldValue(ubicacion.tipoItem),
                    onValueChange = { newValue ->
                        ubicacion.tipoItem = newValue.text
                    },
                    label = { Text("TIPO ITEM", fontWeight = FontWeight.Bold) },
                    textStyle = TextStyle(fontWeight = FontWeight.Bold, color = Color.Red , fontSize = 20.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenMakita,
                        unfocusedBorderColor = Color.Gray,
                        focusedLabelColor = GreenMakita,
                        cursorColor = GreenMakita,
                    )
                )

                Text(
                    text = buildAnnotatedString {
                        pushStyle(
                            style = SpanStyle(fontWeight = FontWeight.Bold)
                        )
                        append("Descripción:")
                        pop()
                        append(" ${ubicacion.descripcion}")
                    },
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically)
                {
                    OutlinedTextField(
                        value = nuevaUbicacion,
                        onValueChange = { newValue ->
                            nuevaUbicacion = newValue.copy(text = newValue.text.uppercase())
                        },
                        label = { Text("NUEVA UBICACIÓN", fontWeight = FontWeight.Bold) },
                        textStyle = TextStyle(fontWeight = FontWeight.Bold, color = Color.Red , fontSize = 20.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenMakita,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = GreenMakita,
                            cursorColor = GreenMakita,
                        )
                    )

                    Button(
                        onClick = {
                            if (nuevaUbicacion.text.isNotEmpty()) {
                                CoroutineScope(Dispatchers.Main).launch {
                                    try {
                                        val request = ActualizaUbicacionRequest(
                                            nuevaUbicacion = nuevaUbicacion.text,
                                            empresa = "Makita",
                                            item = text.text,
                                            tipoItem = response.firstOrNull()?.tipoItem ?: "" // Obtener tipoItem de la primera respuesta
                                        )
                                        apiService.actualizaUbicacion(request)

                                        val requestRegistro = RegistraUbicacionEntity (
                                            username = username,
                                            timestamp =  formatTimestamp(System.currentTimeMillis()),
                                            item = ubicacion.item,
                                            ubicacionAntigua = ubicacion.Ubicacion,
                                            nuevaUbicacion =  nuevaUbicacion.text,
                                            tipoItem = response.firstOrNull()?.tipoItem ?: "" // Obtener tipoItem de la primera respuesta
                                        )

                                        Log.d("*MAKITA*" , "requestTRegistro : $requestRegistro " )

                                        var responseRegistroUbi = registrarUbicacionDao.registraUbicacion(requestRegistro)
                                        Log.d("*MAKITA*" , "Respuesta de ingreso de informacion $responseRegistroUbi")


                                        successMessage = "Ubicación actualizada exitosamente"
                                        nuevaUbicacion = TextFieldValue("") // Limpiar el campo de nueva ubicación
                                        response = apiService.obtenerUbicacion(text.text) // Actualizar la respuesta después de guardar
                                    } catch (e: Exception) {
                                        errorState = "Error al actualizar ubicación: ${e.message}"
                                    } finally {
                                        focusRequester.requestFocus() // Solicitar foco en el campo de escanear item
                                    }
                                }
                            }

                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenMakita,
                            contentColor = Color.White
                        ),
                        enabled = nuevaUbicacion.text.isNotEmpty()

                    ) {
                        Text(text = "Grabar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (response.isNotEmpty() || errorState != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = {
                        clearRequested = true
                        focusRequester.requestFocus()
                    },
                    modifier = Modifier.padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)  // GreenMakita
                    )
                ) {
                    Text("Limpiar")

                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            showDialog = true

                        }
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)  // GreenMakita
                    )
                ) {
                    Text("Terminar Proceso")

                }


            }
        }
        
        

        Spacer(modifier = Modifier.height(16.dp))
        if (datos.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Divider(
                color = Color.Gray,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text("Registros de Inicio de Sesión", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            datos.forEach { data ->
                Text("Usuario: ${data.username}/ Item: ${data.item} / Fecha: ${data.timestamp} / Tipo Item: ${data.tipoItem} / Ubicacion Antigua: ${data.ubicacionAntigua}" +
                        "/ Nueva Ubicacion: ${data.nuevaUbicacion}")
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmación de Borrado") },
            text = { Text("¿Estás seguro de borrar la información y cerrar el proceso?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val result = registrarUbicacionDao.deleteAllData()
                            Log.d("MAKITA", "RESULTADO DEL DELETE : $result")
                            showDialog = false  // Cerrar el modal después de borrar
                        }
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("No")
                }
            }
        )
    }


}


fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("GMT-4") // Establece la zona horaria de Santiago de Chile
    return formatter.format(date)
}




