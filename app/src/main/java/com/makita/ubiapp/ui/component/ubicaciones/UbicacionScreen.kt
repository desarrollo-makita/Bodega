package com.makita.ubiapp.ui.component.ubicaciones

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.ActualizaUbicacionRequest
import com.makita.ubiapp.RegistraBitacoraRequest
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.RetrofitClient.apiService
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


// conserva los datos cuando cambia de orientacion el dispositivo
val TextFieldValueSaver: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun UbicacionScreen(username: String) {


    val apiService = RetrofitClient.apiService
    var text by rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue()) }
    var secondTextFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue())}
    var nuevaUbicacion by rememberSaveable(stateSaver = TextFieldValueSaver) {mutableStateOf(TextFieldValue())}
    var response by rememberSaveable { mutableStateOf<List<UbicacionResponse>>(emptyList()) }
    var clearRequested by rememberSaveable { mutableStateOf(false) }
    var errorState by rememberSaveable { mutableStateOf<String?>(null) }
    var successMessage by rememberSaveable { mutableStateOf<String?>(null) }
    var successMail by rememberSaveable { mutableStateOf<String?>(null) }
    var registrosMessage by rememberSaveable { mutableStateOf<String?>(null) }

    var isTextFieldEnabled by rememberSaveable { mutableStateOf(true) }
    var showTerminateButton by rememberSaveable { mutableStateOf(false) }
    var showLimpiarButton by rememberSaveable { mutableStateOf(true) }
    var showContinuarProcesoButton by rememberSaveable { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val context = LocalContext.current
    val db = AppDatabase.getDatabase(context)

    val keyboardController = LocalSoftwareKeyboardController.current

    // base de datos en memoria
    val registrarUbicacionDao = db.registrarUbicacion()

    LaunchedEffect(text) {
        if (text.text.isNotEmpty()) {
            Log.d("*MAKITA*", "[UbicacionScreen] Se ingresa texto : ${text.text}")

            try {
                val ubicaciones = apiService.obtenerUbicacion(text.text)
                response = ubicaciones

                // Limpiar mensajes de error y éxito previos si hubo una respuesta válida
                errorState = null
                successMessage = null

                Log.d("*MAKITA*", "[UbicacionScreen] Respuesta servicio obtenerUbicacion  : ${text.text}")

                if (ubicaciones.isEmpty()) {
                    // Mostrar mensaje de error si no se encontraron datos
                    errorState = " No se encontraron datos para el item proporcionado"
                }else{
                    val ubicacionPrimera = ubicaciones.first() // Usar el primer elemento de la lista como ejemplo
                    bitacoraRegistro(
                        username = username,
                        ubicacion = ubicacionPrimera,
                        nuevaUbicacion = nuevaUbicacion.text,
                        response = ubicaciones
                    )


                    Log.d("*MAKITA*", "[UbicacionScreen] Registro en bitácora completado")
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
    LaunchedEffect(Unit) {

        focusRequester.requestFocus()
        keyboardController?.hide()
    }
    suspend fun buscarUbiManual(textoManual : String){
        try {
            val ubicaciones = apiService.obtenerUbicacion(textoManual)
            response = ubicaciones

            // Limpiar mensajes de error y éxito previos si hubo una respuesta válida
            errorState = null
            successMessage = null

            Log.d("*MAKITA*", "[UbicacionScreen] Respuesta servicio obtenerUbicacion  : ${text.text}")

            if (ubicaciones.isEmpty()) {
                // Mostrar mensaje de error si no se encontraron datos
                errorState = " No se encontraron datos para el item proporcionado"
            }else{
                val ubicacionPrimera = ubicaciones.first() // Usar el primer elemento de la lista como ejemplo
                bitacoraRegistro(
                    username = username,
                    ubicacion = ubicacionPrimera,
                    nuevaUbicacion = nuevaUbicacion.text,
                    response = ubicaciones
                )


                Log.d("*MAKITA*", "[UbicacionScreen] Registro en bitácora completado")
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00909E),
                        Color(0xFF80CBC4)
                    )
                )
            )
            .padding(16.dp)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Agregar scroll aquí
                .padding(2.dp)
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(20.dp),

            ) {
            Spacer(modifier = Modifier.height(10.dp))

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
                shape = RoundedCornerShape(12.dp),
                label = {
                    Text(
                        "Escanear Item",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenMakita
                        )
                    )
                },
                enabled = isTextFieldEnabled,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text  // mostrar teclado
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                    unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                    focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                    unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                    cursorColor = GreenMakita,            // Color del cursor en GreenMakita

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

            OutlinedTextField(
                value = secondTextFieldValue, // Variable para el estado del nuevo TextField
                onValueChange = { newValue ->
                    val upperCaseValue = newValue.text.uppercase().take(20)
                    secondTextFieldValue = newValue.copy(text = upperCaseValue)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),// No incluimos `focusRequester` para evitar foco automático
                label = {
                    Text(
                        "Ingreso Manual", // Cambia el texto del label según lo necesario
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenMakita
                        )
                    )
                },
                enabled = true, // El campo está habilitado para escritura por teclado
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text // Mostrar teclado de texto
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                    unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                    focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                    unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                    cursorColor = GreenMakita,            // Color del cursor en GreenMakita
                ),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow, // Icono de "Play"
                        contentDescription = "Acción de enviar",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                CoroutineScope(Dispatchers.Main).launch {
                                    clearRequested = true
                                    buscarUbiManual(secondTextFieldValue.text)
                                }
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
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                                    unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                                    focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                                    unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                                    cursorColor = GreenMakita,            // Color del cursor en GreenMakita
                                ),
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
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                                    unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                                    focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                                    unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                                    cursorColor = GreenMakita,            // Color del cursor en GreenMakita
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
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                                unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                                focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                                unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                                cursorColor = GreenMakita,            // Color del cursor en GreenMakita
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
                    // se comenta para probar en bodega , solo ubicaciones sin cambio, descomentar despues.
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
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red,
                                    fontSize = 20.sp
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                                    unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                                    focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                                    unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                                    cursorColor = GreenMakita,            // Color del cursor en GreenMakita
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

                                                val requestRegistroBitacora =
                                                    RegistraBitacoraRequest(
                                                        usuario = username,
                                                        fechaCambio = formatTimestamp(System.currentTimeMillis()),
                                                        item = ubicacion.item,
                                                        ubicacionAntigua = ubicacion.Ubicacion,
                                                        nuevaUbicacion = nuevaUbicacion.text,
                                                        tipoItem = response.firstOrNull()?.tipoItem ?: "",
                                                        operacion = "Cambio ubicacion",
                                                    )

                                                val bitacoraRegistroUbi =
                                                    apiService.insertaBitacoraUbicacion(
                                                        requestRegistroBitacora
                                                    )

                                                Log.d(
                                                    "*MAKITA*",
                                                    "BITACORAREGISTROUBI $bitacoraRegistroUbi"
                                                )
                                                val responseRegistroUbi =
                                                    registrarUbicacionDao.registraUbicacion(
                                                        requestRegistro
                                                    )

                                                successMessage =
                                                    "Ubicación actualizada exitosamente"
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

                text = TextFieldValue("")
                secondTextFieldValue = TextFieldValue("")
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

                }
            }
        }
    }


} // Fin de UbicacionScreen


fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("America/Santiago") // Zona horaria de Chile con ajuste DST
    return formatter.format(date)
}


suspend fun bitacoraRegistro(username : String , ubicacion: UbicacionResponse, nuevaUbicacion : String, response: List<UbicacionResponse>){
    val nuevaUbicacionFinal = if (nuevaUbicacion.isBlank()) "" else nuevaUbicacion
    val requestRegistroBitacora =
        RegistraBitacoraRequest(
            usuario = username,
            fechaCambio = formatTimestamp(System.currentTimeMillis()),
            item = ubicacion.item,
            ubicacionAntigua = ubicacion.Ubicacion,
            nuevaUbicacion = nuevaUbicacionFinal,
            tipoItem = response.firstOrNull()?.tipoItem  ?: "",// Obtener tipoItem de la primera respuesta
            operacion = "Consulta",
        )

    val bitacoraRegistroUbi =
        apiService.insertaBitacoraUbicacion(
            requestRegistroBitacora
        )
}


@Preview(showBackground = true)
@Composable
fun UbicacionScreenPreview() {
    var username by remember { mutableStateOf("juanito Mena") }
    // Render de la pantalla
    UbicacionScreen(
        username = username,

        )
}


