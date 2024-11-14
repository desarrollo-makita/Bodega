package com.makita.ubiapp.ui.component.capturaSerie

import android.util.Log
import androidx.compose.foundation.background

import androidx.compose.foundation.horizontalScroll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.makita.ubiapp.PickingDetalleItem
import com.makita.ubiapp.PickingItem
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

val TextFieldValueCapturaSeries: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun DetalleDocumentoScreen(navController: NavController, item: PickingItem) {
    val coroutineScope = rememberCoroutineScope()
    var pickingList by remember { mutableStateOf<List<PickingDetalleItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }


    // Llamada a la API al iniciar
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.apiService.obtenerPickingCorrelativoDetalle(item.correlativo.toString())
                if (response.isSuccessful && response.body() != null) {
                    pickingList = response.body()!!.data
                    errorMessage = null
                } else {
                    errorMessage = "Error al obtener los datos: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
            }
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage.isNullOrEmpty()) {
            errorMessage = null // Limpia el mensaje de error
        }

    }

    // Fondo degradado y diseño principal
    Column(
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
            .padding(8.dp), // Espaciado global
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cabecera
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White, shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                    )
                )
                .padding(16.dp)
        ) {
            HeaderDetalle(item , errorMessage)
        }

        // Tabla con datos desplazables
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Ajusta el espacio restante
                .background(Color.White)
                .padding(0.dp)
        ) {

            ItemListTable(pickingList)
        }

        // Pie de página
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White, shape = RoundedCornerShape(
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp,
                    )
                )
                .padding(16.dp)
        ) {
            SepararDetalle()
            FooterProcesar(navController)
            CapturaScanner(pickingList = pickingList,
                actualizarPickingList = { nuevaLista ->
                    pickingList = nuevaLista // Actualiza el estado global en el componente padre
                },

                ) {
                nuevoMensaje -> errorMessage = nuevoMensaje // Actualiza el mensaje global
            }
        }
    }
}

@Composable
fun HeaderDetalle(item: PickingItem , errorMessage: String?) {
    // Estado para el valor del texto
    val correlativo = remember { mutableStateOf(TextFieldValue(item.correlativo.toString())) }
    val folio = remember { mutableStateOf(TextFieldValue(item.CorrelativoOrigen.toString())) }
    val cliente = remember { mutableStateOf(TextFieldValue(item.nombrecliente)) }
    val cantidadItemInt = remember { mutableStateOf(item.Total_Items) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp), // Espacio entre el Row y el OutlinedTextField
                horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
            ) {
                OutlinedTextField(
                    value = correlativo.value,
                    onValueChange = { newValue -> correlativo.value = newValue },
                    label = { Text("Correlativo", fontSize = 15.sp, color = Color(0xFF00909E)) },
                    modifier = Modifier
                        .weight(1f) // El campo de texto ocupa el espacio disponible
                        .padding(end = 8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00909E),
                        unfocusedBorderColor = Color(0xFF00909E),
                        focusedLabelColor = Color(0xFF00909E),
                        unfocusedLabelColor = Color(0xFF00909E),
                        cursorColor = Color(0xFF00909E),
                        disabledBorderColor = Color(0xFF00909E)
                    ),
                    textStyle = TextStyle(
                        color = GreenMakita, // Cambia a tu color personalizado aquí
                        fontSize = 16.sp // Ajusta el tamaño de la fuente según sea necesario
                    ),
                    enabled = false
                )

                OutlinedTextField(
                    value = folio.value,
                    onValueChange = { newValue -> folio.value = newValue },
                    label = { Text("Folio", fontSize = 15.sp, color = Color(0xFF00909E)) },
                    modifier = Modifier
                        .weight(1f) // El campo de texto ocupa el espacio disponible
                        .padding(end = 8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF00909E),
                        unfocusedBorderColor = Color(0xFF00909E),
                        focusedLabelColor = Color(0xFF00909E),
                        unfocusedLabelColor = Color(0xFF00909E),
                        cursorColor = Color(0xFF00909E),
                        disabledBorderColor = Color(0xFF00909E)
                    ),
                    textStyle = TextStyle(
                        color = GreenMakita, // Cambia a tu color personalizado aquí
                        fontSize = 16.sp // Ajusta el tamaño de la fuente según sea necesario
                    ),
                    enabled = false
                )
            }

            // Nuevo OutlinedTextField para el cliente que ocupa el ancho completo
            OutlinedTextField(
                value = cliente.value,
                onValueChange = { newValue -> cliente.value = newValue },
                label = { Text("Cliente", fontSize = 15.sp, color = Color(0xFF00909E)) },
                modifier = Modifier
                    .fillMaxWidth() // Ocupa todo el ancho disponible
                    .height(60.dp), // Mantiene la altura consistente
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00909E),
                    unfocusedBorderColor = Color(0xFF00909E),
                    focusedLabelColor = Color(0xFF00909E),
                    unfocusedLabelColor = Color(0xFF00909E),
                    cursorColor = Color(0xFF00909E),
                    disabledBorderColor = Color(0xFF00909E)
                ),
                textStyle = TextStyle(
                    color = GreenMakita, // Cambia a tu color personalizado aquí
                    fontSize = 16.sp // Ajusta el tamaño de la fuente según sea necesario
                ),
                enabled = false
            )

            Text(
                "${cantidadItemInt.value} items encontrado" ,
                color = GreenMakita,
                modifier = Modifier.padding(top = 8.dp)
            )

            errorMessage?.let {
                Text(
                    it,
                    color = Color.Red,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Composable
fun SepararDetalle(){
    Divider(
        color = Color(0xFFFF7F50),
        thickness = 2.dp,
        modifier = Modifier
            .padding(vertical = 1.dp)
            .fillMaxWidth(),
    )
}

@Composable
fun ItemListTable(pickingList: List<PickingDetalleItem>?) {
    val headers = listOf("Item", "Descripcion", "Cantidad", "Cantidad Pedido", "Tipo Documento", "Tipo Item", "Unidad", "Ubicacion")

    val fields = listOf<(PickingDetalleItem) -> String>(
        { item -> item.item ?: "Sin item" },
        { item -> item.Descripcion ?: "Sin descripción" },
        { item -> item.Cantidad.toString() },
        { item -> item.CantidadPedida.toString() },
        { item -> item.TipoDocumento ?: "Sin TipoDocumento" },
        { item -> item.Tipoitem ?: "Sin tipoItem" },
        { item -> item.Unidad ?: "Sin Unidad" },
        { item -> item.Ubicacion ?: "Sin ubicación" }
    )

    // Contenedor desplazable horizontal y vertical
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, shape = RoundedCornerShape(5.dp))
                    .padding(vertical = 5.dp)
            ) {
                headers.forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(horizontal = 5.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = GreenMakita
                        
                    )
                }
            }

            SepararDetalle()
            // Cuerpo de la tabla
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                pickingList?.forEach { item ->
                    val backgroundColor = if (item.Cantidad == item.CantidadPedida) GreenMakita.copy(alpha = 0.1f) else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor)
                            .padding(vertical = 5.dp)
                    ) {
                        fields.forEach { field ->
                            Text(
                                text = field(item),
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(horizontal = 5.dp),
                                fontSize = 12.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FooterProcesar(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp), // Margen alrededor del Row
        horizontalArrangement = Arrangement.SpaceEvenly // Espacio uniforme entre los botones
    ) {
        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f) // Cada botón ocupa la misma proporción del espacio disponible
                .padding(horizontal = 4.dp), // Espaciado entre los botones
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
            contentPadding = PaddingValues(8.dp) // Espaciado interno para mantener buen tamaño del ícono
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Procesar", tint = Color.White)
        }

        Button(
            onClick = { },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
            contentPadding = PaddingValues(8.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
            contentPadding = PaddingValues(8.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Salir", tint = Color.White)
        }
    }

}

@Composable
fun CapturaScanner(
    pickingList: List<PickingDetalleItem>?,
    actualizarPickingList: (List<PickingDetalleItem>) -> Unit,
    actualizarMensajeError: (String) -> Unit
) {
    val textoEntrada = remember { mutableStateOf(TextFieldValue("")) }
    var itemScannerType by remember { mutableStateOf("") }
    var serieInicial by remember { mutableStateOf("") }
    var serieFinal by remember { mutableStateOf("") }
    var letraFabrica by remember { mutableStateOf("") }
    var ean by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val pickingListState = remember { mutableStateOf(pickingList ?: listOf()) }

    // Solicitar foco una vez que el componente esté compuesto
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(textoEntrada.value.text) {
        if (textoEntrada.value.text.isNotEmpty()) {
            if(textoEntrada.value.text.length > 39){
                var mockitemScannerType = "DLM462PT4           000004158000004158Y0088381733540CLA"
               /* itemScannerType = textoEntrada.value.text.substring(0,20).trim()
                serieInicial = textoEntrada.value.text.substring(20,29).trim()
                serieFinal = textoEntrada.value.text.substring(29,38).trim()
                letraFabrica = textoEntrada.value.text.substring(38,39).trim()
                ean = textoEntrada.value.text.substring(0,20).trim()*/

                itemScannerType = mockitemScannerType.substring(0,20).trim()
                serieInicial = mockitemScannerType.substring(20,29).trim()
                serieFinal = mockitemScannerType.substring(29,38).trim()
                letraFabrica = mockitemScannerType.substring(38,39).trim()
                ean = mockitemScannerType.substring(0,20).trim()

                Log.d("Serie de inicio : " ,"Serie de inicio $serieInicial")
                Log.d("Serie Final :", "Serie Final $serieFinal")

                val itemDetalle = pickingListState.value.find { it.item == itemScannerType }

                if (itemDetalle == null) {
                    actualizarMensajeError("El ítem ($itemScannerType) no se encuentra en la lista.")
                } else if(itemDetalle != null) {

                    if (itemDetalle.Cantidad >= itemDetalle.CantidadPedida) {
                        actualizarMensajeError("El ítem ($itemScannerType) ya está completo. No se requiere más cantidad.")
                    }else if(serieInicial == serieFinal ){ //unitario
                        val catidadUnitaria = 0
                        actualizarMensajeError("")
                        procesarData(pickingListState , actualizarPickingList , itemScannerType , catidadUnitaria , serieInicial ,serieFinal )

                    }else{
                        val cantidadMaster = serieFinal.toInt() - serieInicial.toInt()
                        Log.d("Caja master  :" , "Caja master : $cantidadMaster")
                        procesarData(pickingListState , actualizarPickingList , itemScannerType , cantidadMaster, serieInicial ,serieFinal)
                    }
                }

                delay(1000)
                textoEntrada.value = TextFieldValue("")
                itemScannerType= ""
            }else{
                actualizarMensajeError("El código escaneado no corresponde.")
            }
        }
    }

    LaunchedEffect(pickingList) {
        pickingListState.value = pickingList ?: listOf()
    }

    // Campo de texto con foco inicial
    BasicTextField(
        value = textoEntrada.value,
        onValueChange = { textoEntrada.value = it },
        modifier = Modifier
            .focusRequester(focusRequester) // Aplica el FocusRequester aquí
            .fillMaxWidth()
            .alpha(0f),
        singleLine = true
    )
}

private fun procesarData(
    pickingListState: MutableState<List<PickingDetalleItem>>,
    actualizarPickingList: (List<PickingDetalleItem>) -> Unit,
    itemScannerType: String,
    cantidad: Int,
    serieInicial: String,
    serieFinal: String
) {
    // Identificar y actualizar el item correspondiente
    val updatedList = pickingListState.value.map { item ->
        if (item.item == itemScannerType) {
            val nuevaCantidad = item.Cantidad + cantidad + 1
            item.copy(
                Cantidad = nuevaCantidad,
                serie = serieInicial
            )
        } else {
            item
        }
    }

    // Obtener el item actualizado
    val itemActualizado = updatedList.find { it.item == itemScannerType }

    // Loguear y actualizar el estado de la lista
    Log.d("item actualizado", "Item actualizado: $itemActualizado")
    pickingListState.value = updatedList
    actualizarPickingList(updatedList)

    // Guardar solo el item actualizado
    itemActualizado?.let { item ->
        guardarArchivoPlano(listOf(item), "picking_data_capturados.txt")
    }
}


fun guardarArchivoPlano(data: List<PickingDetalleItem>, nombreArchivo: String) {
    try {
        // Obtener la fecha y hora actual en el formato deseado
        val fechaHoraActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

        // Transformar cada objeto a la línea en formato requerido
        val lineas = data.map { item ->
            "makita;${item.linea};${item.item};${item.Descripcion};${item.Cantidad};" +
                    "${item.CantidadPedida};${item.TipoDocumento};${item.Tipoitem};" +
                    "${item.Unidad};${item.Ubicacion};${item.serie};MAKITA;$fechaHoraActual"
        }

        // Crear el archivo o agregar nuevas líneas si ya existe
        val archivo = File("/data/data/com.makita.ubiapp/files", nombreArchivo)
        archivo.appendText(lineas.joinToString("\n") + "\n") // Agregar las líneas al archivo con un salto de línea

        Log.d("guardarArchivoPlano", "Líneas agregadas exitosamente: ${archivo.absolutePath}")
    } catch (e: Exception) {
        Log.e("guardarArchivoPlano", "Error al guardar el archivo: ${e.message}")
    }
}




@Preview(showBackground = true)
@Composable
fun CapturaSerieScreenView() {

// Crea un objeto de ejemplo de PickingItem para pasar al composable
val exampleItem = PickingItem(
    CorrelativoOrigen = 1,
    DocumentoOrigen = "Doc123",
    entidad = "16802012-0",
    Fecha = "2024-11-04",
    nombrecliente = "Cliente Ejemplo",
    empresa = "makita",
    Total_Items = 0 ,
    glosa = "glosa este es un texto largo para probar el coportamiento de mis esoacios espacios espacios espacios espacoips ",
    Ciudad = "Santiago",
    comuna = "Lampa",
    Bodorigen = "02",
    correlativo = 123,
    Direccion = "avenida el parque",
    Boddestino = "15"

)
val navController = rememberNavController()
DetalleDocumentoScreen(navController = navController, item = exampleItem )
}
