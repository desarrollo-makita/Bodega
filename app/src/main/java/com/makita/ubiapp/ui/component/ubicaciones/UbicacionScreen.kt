package com.makita.ubiapp.ui.component.ubicaciones

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.makita.ubiapp.ui.component.database.AppDatabase
import com.makita.ubiapp.ui.component.entity.RegistraUbicacionEntity
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import com.makita.ubiapp.ui.component.archivo.guardarDatosEnExcel

val TextFieldValueSaver: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun UbicacionScreen(username: String) {

    val apiService = RetrofitClient.apiService
    var text by rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue()) }
    var nuevaUbicacion by rememberSaveable(stateSaver = TextFieldValueSaver) {
        mutableStateOf(
            TextFieldValue()
        )
    }
    var response by rememberSaveable { mutableStateOf<List<UbicacionResponse>>(emptyList()) }
    var clearRequested by rememberSaveable { mutableStateOf(false) }
    var errorState by rememberSaveable { mutableStateOf<String?>(null) }
    var successMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var successMail by rememberSaveable { mutableStateOf<String?>(null) }
    var registrosMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var datos by rememberSaveable { mutableStateOf<List<RegistraUbicacionEntity>>(emptyList()) }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var isTextFieldEnabled by rememberSaveable { mutableStateOf(true) }
    var showTerminateButton by rememberSaveable { mutableStateOf(false) }
    var showLimpiarButton by rememberSaveable { mutableStateOf(true) }
    var showContinuarProcesoButton by rememberSaveable { mutableStateOf(false) }
    var count by rememberSaveable { mutableIntStateOf(0) }

    val focusRequester = remember { FocusRequester() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)
    val registrarUbicacionDao = db.registrarUbicacion()

    val emailLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("*MAKITA*", "Resul envio Correo : $result")
        coroutineScope.launch {
            if (result.resultCode == Activity.RESULT_CANCELED) {

                registrarUbicacionDao.deleteAllData()
                Log.d("*MAKITA*", "Datos borrados y enviados por correo.")
                successMail = "Datos enviados por correo exitosamente."

            } else {
                Log.e("*MAKITA*", "Error al enviar el correo.")
                errorState = "Error al enviar el correo."
            }
        }

    }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                datos = registrarUbicacionDao.getAllData()

                Log.d("*MAKITA*", "la data de SQLite que se encuentra en la tabla : $datos")
            } catch (e: Exception) {
                Log.e("*MAKITA*", "Error fetching logins: ${e.message}")
            }
        }
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
            .verticalScroll(rememberScrollState()) // Agregar scroll aquí
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
                .focusRequester(focusRequester),
            label = { Text("Escanear Item") },
            enabled = isTextFieldEnabled,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = GreenMakita,
                cursorColor = GreenMakita,
            ),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Clear text",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            clearRequested = true
                            focusRequester.requestFocus()
                        },
                    tint = GreenMakita
                )
            },
        )

        // Mostrar mensaje de registros
        registrosMessage?.let { message ->
            Text(
                text = message,
                color = Color.Red,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp),

                )
        }

        // Mostrar mensaje de éxito
        successMessage?.let { message ->
            LaunchedEffect(Unit) {
                delay(4000) // Mostrar el mensaje por 2 segundos
                successMessage = null // Limpiar el mensaje después de 2 segundos

            }
            Text(
                text = message,
                color = GreenMakita,
                style = TextStyle(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp),

                )
        }

        successMail?.let { message ->
            LaunchedEffect(Unit) {
                delay(4000) // Mostrar el mensaje por 2 segundos
                successMail = null // Limpiar el mensaje después de 2 segundos
                clearRequested = true
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
            response.forEach { ubicacion ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = TextFieldValue(ubicacion.item),
                            onValueChange = { newValue ->
                                ubicacion.item = newValue.text
                            },
                            label = { Text("ITEM", fontWeight = FontWeight.Bold) },
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                fontSize = 20.sp
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

                        OutlinedTextField(
                            value = TextFieldValue(ubicacion.Ubicacion.takeUnless { it.isEmpty() }
                                ?: "Sin Ubicación"),
                            onValueChange = { newValue ->
                                ubicacion.Ubicacion = newValue.text
                            },
                            label = { Text("UBICACIÓN", fontWeight = FontWeight.Bold) },
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                fontSize = 20.sp
                            ),
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
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            fontSize = 20.sp
                        ),
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        OutlinedTextField(
                            value = nuevaUbicacion,
                            onValueChange = { newValue ->
                                nuevaUbicacion = newValue.copy(text = newValue.text.uppercase())
                            },
                            label = { Text("NUEVA UBICACIÓN", fontWeight = FontWeight.Bold) },
                            textStyle = TextStyle(
                                fontWeight = FontWeight.Bold, color = Color.Red, fontSize = 20.sp
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
                                                tipoItem = response.firstOrNull()?.tipoItem
                                                    ?: "" // Obtener tipoItem de la primera respuesta
                                            )
                                            apiService.actualizaUbicacion(request)

                                            val requestRegistro = RegistraUbicacionEntity(
                                                username = username,
                                                timestamp = formatTimestamp(System.currentTimeMillis()),
                                                item = ubicacion.item,
                                                ubicacionAntigua = ubicacion.Ubicacion,
                                                nuevaUbicacion = nuevaUbicacion.text,
                                                tipoItem = response.firstOrNull()?.tipoItem
                                                    ?: "" // Obtener tipoItem de la primera respuesta
                                            )

                                            Log.d(
                                                "*MAKITA*",
                                                "requestTRegistro : $requestRegistro "
                                            )

                                            val responseRegistroUbi =
                                                registrarUbicacionDao.registraUbicacion(
                                                    requestRegistro
                                                )
                                            Log.d("*MAKITA*", "Se registran datos en sqlite : $responseRegistroUbi")


                                            successMessage = "Ubicación actualizada exitosamente"
                                            nuevaUbicacion =
                                                TextFieldValue("") // Limpiar el campo de nueva ubicación
                                            response =
                                                apiService.obtenerUbicacion(text.text) // Actualizar la respuesta después de guardar
                                            showTerminateButton = true
                                            showLimpiarButton = true
                                            showContinuarProcesoButton = false
                                        } catch (e: Exception) {
                                            errorState =
                                                "Error al actualizar ubicación: ${e.message}"
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
        }

        // funcion boton limpiar
        if (clearRequested) {
            Log.d("*MAKITA*", "entro al if $clearRequested")
            text = TextFieldValue("")
            response = emptyList()
            clearRequested = false
            errorState = null
            successMessage = null
            showTerminateButton = false
            showLimpiarButton = true
            showContinuarProcesoButton = false
            registrosMessage = null
        }



        Spacer(modifier = Modifier.height(16.dp))
        if (response.isNotEmpty() || errorState != null || showTerminateButton) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                if (showLimpiarButton) {
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
                }

                if (showTerminateButton) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                showDialog = true
                            }

                        },
                        modifier = Modifier.padding(end = 7.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00909E)  // GreenMakita
                        )
                    ) {
                        Text("Terminar Proceso")

                    }
                }

                if (showContinuarProcesoButton) {
                    Button(
                        onClick = {

                            count = 1
                            isTextFieldEnabled = true
                            clearRequested = true
                            focusRequester.requestFocus()

                            Log.d("*MAKITA*", "PASA POR onclik $isTextFieldEnabled")
                        },
                        modifier = Modifier.padding(end = 7.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00909E)  // GreenMakita
                        )
                    ) {
                        Text("Continuar Proceso")

                    }
                }


            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (datos.isNotEmpty() && count == 0) {
            Log.d("*MAKITA*", "PASA POR datos.isNotEmpty $isTextFieldEnabled")
            val cantidadRegistros = datos.size
            registrosMessage = "Dispositivo con $cantidadRegistros registros"
            isTextFieldEnabled = false
            showTerminateButton = true // Mostrar el botón "Terminar Proceso"
            showLimpiarButton = false
            showContinuarProcesoButton = true

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
                            try {
                                val registros = registrarUbicacionDao.getAllData()
                                if (registros.isNotEmpty()) {
                                    val fileUri = guardarDatosEnExcel(context, registros)
                                    if (fileUri != null) {
                                        val emailIntent = Intent(Intent.ACTION_SEND).apply {
                                            type =
                                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                                            putExtra(
                                                Intent.EXTRA_EMAIL,
                                                arrayOf("jherrera@makita.cl")
                                            ) // Reemplaza con el correo del destinatario
                                            putExtra(Intent.EXTRA_SUBJECT, "Registros de Ubicación")
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "Adjunto encontrarás los registros de ubicación."
                                            )
                                            putExtra(Intent.EXTRA_STREAM, fileUri)
                                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                        }
                                        emailLauncher.launch(emailIntent)


                                        showDialog =
                                            false  // Cerrar el modal después de enviar y borrar

                                    } else {
                                        errorState = "Error al crear el archivo para el correo."
                                        Log.e(
                                            "*MAKITA*",
                                            "Error al crear el archivo para el correo."
                                        )
                                    }
                                } else {
                                    Log.e("*MAKITA*", "No hay registros para enviar.")
                                }
                            } catch (e: Exception) {
                                Log.e("*MAKITA*", "Error al procesar: ${e.message}")
                            }
                        }
                    }
                ) {
                    Text("Sí")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }

} // Fin de UbicacionScreen


fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("GMT-4") // Establece la zona horaria de Santiago de Chile
    return formatter.format(date)
}




