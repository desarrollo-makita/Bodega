package com.makita.ubiapp.ui.component.consultaStock


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.ItemStockResponse
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.component.capturaSerie.Separar
import com.makita.ubiapp.ui.component.ubicaciones.TextFieldValueSaver
import com.makita.ubiapp.ui.component.ubicaciones.bitacoraRegistro
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ConsultaStockScreen() {

    val focusRequester = remember { FocusRequester() }
    var text by rememberSaveable(stateSaver = TextFieldValueSaver) { mutableStateOf(TextFieldValue()) }
    var secondTextFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue())}
    val apiService = RetrofitClient.apiService

    var response by rememberSaveable { mutableStateOf<List<ItemStockResponse>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var clearRequested by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(text) {

        if (text.text.isNotEmpty()) {
            Log.d("*MAKITA*", "[ConsultaStockScreen] Se ingresa item para consultar stock : ${text.text}")
            try {
                val stock = apiService.consultarStock(text.text)
                Log.d("*MAKITA*", "Respuesta :  : $stock")
                if (stock.isEmpty()) {
                    // Si la respuesta está vacía, asignamos un mensaje de error
                    errorMessage = "No se encontraron datos para el item proporcionado."
                    Log.d("*MAKITA*", "Respuesta :  : $errorMessage")
                    response = emptyList() // Aseguramos que la respuesta esté vacía
                } else {
                    response = stock
                    errorMessage = null // Limpiamos el mensaje de error si hay datos
                }
            } catch (e: Exception) {
                errorMessage = "Error al consultar el stock: ${e.message}"
                e.printStackTrace()
            }
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    suspend fun buscarStockManual(textoManual : String){
        Log.d("*MAKITA*", "[ConsultaStockScreen] Se ingresa item para consultar stock : ${text.text}")
            try {
                val stock = apiService.consultarStock(textoManual)
                Log.d("*MAKITA*", "RespuestaManual :  : $stock")
                if (stock.isEmpty()) {
                    // Si la respuesta está vacía, asignamos un mensaje de error
                    errorMessage = "No se encontraron datos para el item proporcionado."
                    Log.d("*MAKITA*", "Respuesta :  : $errorMessage")
                    response = emptyList() // Aseguramos que la respuesta esté vacía
                } else {
                    response = stock
                    errorMessage = null // Limpiamos el mensaje de error si hay datos
                }
            } catch (e: Exception) {
                errorMessage = "Error al consultar el stock: ${e.message}"
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
             )
        {

            TituloStock()
            Separar()

            OutlinedTextField(
                text,
                { newTextFieldValue ->
                    val trimmedText = newTextFieldValue.text
                        .take(20)
                        .trim()
                        .uppercase()

                    text = TextFieldValue(
                        text = trimmedText,
                        selection = TextRange(trimmedText.length) // Coloca el cursor al final del texto
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
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
                    .padding(horizontal = 16.dp),
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
                                    buscarStockManual(secondTextFieldValue.text)
                                }
                            },
                        tint = GreenMakita
                    )
                },
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

            }

            // funcion boton limpiar
            if (clearRequested) {
                secondTextFieldValue = TextFieldValue("")
                text = TextFieldValue("")
                response = emptyList()
                clearRequested = false
                errorMessage = null
            }

            Resultado(response)

        }

    }
}


@Composable
fun TituloStock() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Text(
            text = "CONSULTAR STOCK",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center),
            color = Color(0xFF00909E)
        )
    }
}

@Composable
fun Resultado(responseStock: List<ItemStockResponse>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        responseStock.forEach { stock ->
            Column(modifier = Modifier.fillMaxWidth()) {
                // Campo STOCK
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = stock.StockFinal.toString(), // Asegúrate de que el valor sea un String
                        onValueChange = { newValue ->
                            // Intenta convertir el texto a Int y actualizar el valor de StockFinal
                            stock.StockFinal = newValue.toIntOrNull() ?: 0 // Si no es un número válido, asigna 0
                        },
                        label = { Text("STOCK", fontWeight = FontWeight.Bold) },
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(20.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                            unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                            focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                            unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                            cursorColor = GreenMakita,            // Color del cursor en GreenMakita
                        ),
                    )

                    // Campo BODEGA
                    OutlinedTextField(
                        value = TextFieldValue(stock.Bodega),
                        onValueChange = { newValue ->
                            stock.Bodega = newValue.text
                        },
                        label = { Text("BODEGA", fontWeight = FontWeight.Bold) },
                        textStyle = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Red,
                            fontSize = 20.sp
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(20.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenMakita,     // Borde al enfocar en GreenMakita
                            unfocusedBorderColor = GreenMakita,   // Borde sin enfoque en GreenMakita
                            focusedLabelColor = GreenMakita,      // Color del label cuando el campo está enfocado
                            unfocusedLabelColor = GreenMakita,    // Color del label cuando no está enfocado
                            cursorColor = GreenMakita,            // Color del cursor en GreenMakita
                        ),
                    )
                }

                // Campo DESCRIPCION (debajo de los anteriores)
                OutlinedTextField(
                    value = TextFieldValue(stock.Descripcion), // Mostrar la descripción
                    onValueChange = { newValue ->
                        stock.Descripcion = newValue.text // Actualiza la descripción
                    },
                    label = { Text("DESCRIPCION", fontWeight = FontWeight.Bold) },
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth() // Asegúrate de que ocupe el ancho completo
                        .padding(horizontal = 20.dp, vertical = 8.dp), // Ajuste de padding para el campo
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenMakita,
                        unfocusedBorderColor = GreenMakita,
                        focusedLabelColor = GreenMakita,
                        unfocusedLabelColor = GreenMakita,
                        cursorColor = GreenMakita,
                    ),
                )

                // Campo DESCRIPCION (debajo de los anteriores)
                OutlinedTextField(
                    value = TextFieldValue(stock.TipoItem), // Mostrar la descripción
                    onValueChange = { newValue ->
                        stock.TipoItem = newValue.text // Actualiza la descripción
                    },
                    label = { Text("Tipo Item", fontWeight = FontWeight.Bold) },
                    textStyle = TextStyle(
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth() // Asegúrate de que ocupe el ancho completo
                        .padding(horizontal = 20.dp, vertical = 8.dp), // Ajuste de padding para el campo
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenMakita,
                        unfocusedBorderColor = GreenMakita,
                        focusedLabelColor = GreenMakita,
                        unfocusedLabelColor = GreenMakita,
                        cursorColor = GreenMakita,
                    ),
                )
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
fun ConsultarStockPreview() {

    ConsultaStockScreen()
}