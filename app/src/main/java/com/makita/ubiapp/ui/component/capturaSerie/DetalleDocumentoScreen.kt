package com.makita.ubiapp.ui.component.capturaSerie

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

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
import androidx.compose.foundation.layout.wrapContentSize

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.makita.ubiapp.ActividadItem
import com.makita.ubiapp.DataUpdateCapturaReq
import com.makita.ubiapp.InsertCaptura
import com.makita.ubiapp.InsertCapturaList
import com.makita.ubiapp.PickingDetalleItem
import com.makita.ubiapp.PickingItem
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.component.ubicaciones.eliminarArchivo
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date

val TextFieldValueCapturaSeries: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun DetalleDocumentoScreen(navController: NavController, item: PickingItem , usuario: String, area : String, vigencia : Long ,
                           idUsuario : Int ,
                           token: String,
                           actividades: List<ActividadItem>) {
    val coroutineScope = rememberCoroutineScope()
    var pickingList by remember { mutableStateOf<List<PickingDetalleItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Estado para el loading
    var mensajeDialogo by remember { mutableStateOf("") }
    val areAllItemsComplete = pickingList?.all { it.Cantidad == it.CantidadPedida } == true

    var showDialogErrorVacio by remember { mutableStateOf(false) }

    if (showDialogErrorVacio) {
        AlertDialog(
            onDismissRequest = { showDialogErrorVacio = false },
            title = { Text("Alerta") },
            text = { Text(mensajeDialogo) },
            confirmButton = {
                Button(
                    onClick = {
                         val actividadesJson = Gson().toJson(actividades)
                        val actividadesJsonEncoded = URLEncoder.encode(actividadesJson, StandardCharsets.UTF_8.toString())
                        showDialogErrorVacio = false
                        navController.navigate("picking/$usuario/$area/$vigencia/$idUsuario/$token/$actividadesJsonEncoded")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)  // Color de fondo del botón
                    )
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {}
        )
    }

    fun cargarTodaLaData(){
        isLoading = true
        coroutineScope.launch {
            try {
                delay(1000) // Simulación de espera
                val response = RetrofitClient.apiService.obtenerPickingCorrelativoDetalle(item.correlativo.toString() , area.trim())
                if (response.isSuccessful && response.body() != null) {
                    pickingList = response.body()!!.data
                    errorMessage = null

                    // Iterar sobre los detalles y sumar la cantidad encontrada
                    pickingList?.forEach { detalleItem ->
                        val lineasEncontradas = leerArchivoCaptura(detalleItem.item)
                        val cantidadEncontrada = lineasEncontradas.size

                        // Sumar la cantidad encontrada al valor actual de Cantidad
                        val nuevaCantidad = detalleItem.Cantidad + cantidadEncontrada

                        // Si necesitas actualizar `Cantidad` en el objeto original:
                        detalleItem.Cantidad = nuevaCantidad
                    }

                    // Imprimir el resultado final de la lista modificada
                    println("Lista actualizada: $pickingList")
                } else {
                    val jsonError = JSONObject(response.errorBody()?.string())

                    errorMessage = jsonError.getString("error")
                    println("Error al obtener los datos: $errorMessage")

                    mensajeDialogo = errorMessage as String
                    showDialogErrorVacio = true
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
            }finally {
                isLoading = false
            }
        }


    }

    LaunchedEffect(Unit) {
        cargarTodaLaData()
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
            if(isLoading){
            LoadingIndicator()
        }
            ItemListTable(
                navController,
                pickingList,
                item.CorrelativoOrigen,
                item.correlativo,
                usuario,
                area)
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
            FooterProcesar(
                navController,
                usuario,
                onActualizarClick = {
                cargarTodaLaData()
            },
                isEnabled = areAllItemsComplete,
                area= area,
                vigencia = vigencia ,
                idUsuario = idUsuario ,
                token = token,
                actividades =  actividades

            )
            CapturaScanner(pickingList = pickingList,
                actualizarPickingList = { nuevaLista ->
                    pickingList = nuevaLista // Actualiza el estado global en el componente padre
                },
                usuario = usuario,
                item = item,)
            {
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
fun ItemListTable(
        navController: NavController,
        pickingList: List<PickingDetalleItem>?,
        correlativoOrigen : Int,
        correlativo: Int,
        username: String ,
        area: String) {

    val showDialog = remember { mutableStateOf(false) }
    val itemToShowMessage = remember { mutableStateOf<PickingDetalleItem?>(null) }

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

    // Calcular cuántos ítems están completos
    val completedItemsCount = pickingList?.count { it.Cantidad == it.CantidadPedida } ?: 0
    val totalItemsCount = pickingList?.size ?: 0

    // Contenedor desplazable horizontal y vertical
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Column {
            Text(
                text = "$completedItemsCount de $totalItemsCount ítems completos",
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.Start),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = GreenMakita
            )
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
                    ){
                        fields.forEachIndexed { index, field ->
                            val textColor = if (index == 0) Color.Blue else Color.Black
                            Text(
                                text = field(item),
                                color = textColor,
                                modifier = Modifier
                                    .width(100.dp)
                                    .padding(horizontal = 5.dp)
                                    .let { baseModifier ->
                                        if (index == 0) { // Solo permitir clics en el primer campo
                                            baseModifier.clickable {
                                                if (item.Cantidad == item.CantidadPedida) {
                                                    // Si la cantidad es igual a la cantidad pedida, mostramos el modal
                                                    itemToShowMessage.value = item
                                                    showDialog.value = true
                                                } else {
                                                    // Si la validación pasa, navegar al siguiente componente
                                                    val itemJson = Gson().toJson(item) // Serializamos el objeto
                                                    navController.navigate("procesar-sin-codigo-barra/$itemJson/${correlativoOrigen.toString()}/${correlativo.toString()}/$username/$area")
                                                }
                                            }

                                        } else baseModifier // Sin clickable para otros campos
                                    },
                                fontSize = 12.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }
        }
    }
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Alerta") },
            text = { Text("No se puede procesar este ítem porque la cantidad ya está completa.") },
            confirmButton = {
                Button(
                    onClick = { showDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
                ) {
                    Text("Aceptar")
                }

            }
        )
    }

}

@Composable
fun FooterProcesar(
    navController: NavController ,
    usuario: String,
    onActualizarClick: () -> Unit,
    isEnabled: Boolean ,
    area: String,
    vigencia : Long ,
    idUsuario : Int ,
    token: String,
    actividades: List<ActividadItem>
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp), // Margen alrededor del Row
        horizontalArrangement = Arrangement.SpaceEvenly // Espacio uniforme entre los botones
    ) {
        Button(
            onClick = { procesarDatos(navController , usuario ,area,vigencia, idUsuario,token, actividades) },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
            contentPadding = PaddingValues(8.dp),
            enabled = isEnabled
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically // Centra el texto y el ícono verticalmente
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Procesar", tint = Color.White)
                Text(
                    text = "Procesar",
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp) // Espaciado a la izquierda del texto
                )
            }
        }

        Button(
            onClick = {onActualizarClick()  },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
            contentPadding = PaddingValues(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically // Centra el texto y el ícono verticalmente
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Actualizar", tint = Color.White)
                Text(
                    text = "Actualizar",
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp) // Espaciado a la izquierda del texto
                )
            }
        }
    }
}


@Composable
fun CapturaScanner(
    pickingList: List<PickingDetalleItem>?,
    actualizarPickingList: (List<PickingDetalleItem>) -> Unit,
    usuario: String,
    item: PickingItem,
    actualizarMensajeError: (String) -> Unit
) {
    val textoEntrada = remember { mutableStateOf(TextFieldValue("")) }
    var itemScannerType by remember { mutableStateOf("") }
    var serieInicial by remember { mutableStateOf("") }
    var serieFinal by remember { mutableStateOf("") }
    var letraFabrica by remember { mutableStateOf("") }
    var ean by remember { mutableStateOf("") }
    var digito by remember { mutableStateOf("") }
    var codigoComercial by remember { mutableStateOf("") }
    var codigoChile by remember { mutableStateOf("") }


    val focusRequester = remember { FocusRequester() }
    val pickingListState = remember { mutableStateOf(pickingList ?: listOf()) }

    // Solicitar foco una vez que el componente esté compuesto
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(textoEntrada.value.text) {
        if (textoEntrada.value.text.isNotEmpty()) {
            Log.d("*MAKITA*","Largo del texto _:  ${textoEntrada.value.text.length}")
            if(textoEntrada.value.text.length > 39 && textoEntrada.value.text.length <=55){
              // var mockitemScannerType = "KP0810              000004158000004158Y0088381733540CLA"

              itemScannerType = textoEntrada.value.text.substring(0,20).trim()
                serieInicial = textoEntrada.value.text.substring(20,29).trim()
                serieFinal = textoEntrada.value.text.substring(29,38).trim()
                letraFabrica = textoEntrada.value.text.substring(38,39).trim()
                ean = textoEntrada.value.text.substring(0,20).trim()


            /*    itemScannerType = mockitemScannerType.substring(0,20).trim()
                serieInicial = mockitemScannerType.substring(20,29).trim()
                serieFinal = mockitemScannerType.substring(29,38).trim()
                letraFabrica = mockitemScannerType.substring(38,39).trim()
                ean = mockitemScannerType.substring(0,20).trim()
            */
                val itemDetalle = pickingListState.value.find { it.item == itemScannerType }

                if (itemDetalle == null) {
                    actualizarMensajeError("El ítem ($itemScannerType) no se encuentra en la lista.")

                    textoEntrada.value = TextFieldValue("")
                    itemScannerType= ""
                } else if(itemDetalle != null) {

                    if (itemDetalle.Cantidad >= itemDetalle.CantidadPedida) {
                            actualizarMensajeError("El ítem ($itemScannerType) ya está completo. No se requiere más cantidad.")
                    }else if(serieInicial == serieFinal ){ //unitario
                        val catidadUnitaria = 0
                        actualizarMensajeError("")
                        procesarDataUnitario(item ,usuario,pickingListState ,actualizarPickingList ,itemScannerType ,catidadUnitaria,serieInicial ,serieFinal ,
                            actualizarMensajeError = {
                                mensaje -> actualizarMensajeError(mensaje)
                            }
                        )
                    }else{
                        val cantidadMaster = serieFinal.toInt() - serieInicial.toInt()
                        Log.d("Caja master  :" , "Caja master : $cantidadMaster")
                        procesarDataMaster(
                            item,
                            usuario ,
                            pickingListState ,
                            actualizarPickingList ,
                            itemScannerType ,
                            cantidadMaster,
                            serieInicial ,
                            serieFinal,
                            actualizarMensajeError = {
                                mensaje -> actualizarMensajeError(mensaje)
                            }
                        )
                    }
                }


                textoEntrada.value = TextFieldValue("")
                itemScannerType= ""
            }
            else if(textoEntrada.value.text.length > 55){

               // var mockitemScannerType = "999999-9            00000004600000004600088381597463000197363-40000DC18WC000000000000000000000000000000"

                itemScannerType = textoEntrada.value.text.substring(0,20).trim()
                serieInicial = textoEntrada.value.text.substring(20,29).trim()
                serieFinal = textoEntrada.value.text.substring(29,38).trim()
                digito = textoEntrada.value.text.substring(38, 39).trim()
                ean = textoEntrada.value.text.substring(39, 52).trim()
                codigoComercial = textoEntrada.value.text.substring(53, 63).trim().replace("^0+".toRegex(), "")
                codigoChile = textoEntrada.value.text.substring(63, 73).trim()

            /*    itemScannerType = mockitemScannerType.substring(0,20).trim()
                serieInicial = mockitemScannerType.substring(20,29).trim()
                serieFinal = mockitemScannerType.substring(29,38).trim()
                digito = mockitemScannerType.substring(38, 39).trim()
                ean = mockitemScannerType.substring(39, 52).trim()
                codigoComercial = mockitemScannerType.substring(53, 63).trim().replace("^0+".toRegex(), "")
                codigoChile = mockitemScannerType.substring(63, 73).trim()
*/
                val itemDetalle = pickingListState.value.find { it.item == codigoComercial }

                if (itemDetalle == null) {
                    actualizarMensajeError("El ítem ($codigoComercial) no se encuentra en la lista.")

                    textoEntrada.value = TextFieldValue("")
                    itemScannerType= ""
                }else {

                    if (itemDetalle.Cantidad >= itemDetalle.CantidadPedida) {

                        textoEntrada.value = TextFieldValue("")
                        itemScannerType= ""
                    }else if(serieInicial == serieFinal ){ //unitario
                        val catidadUnitaria = 0
                        actualizarMensajeError("")
                        procesarDataUnitario(item ,usuario,pickingListState ,actualizarPickingList ,codigoComercial ,catidadUnitaria,serieInicial ,serieFinal ,
                            actualizarMensajeError = {
                                    mensaje -> actualizarMensajeError(mensaje)
                            }
                        )
                    }
                }
            }
            else{
                actualizarMensajeError("El código escaneado no corresponde.")
                textoEntrada.value = TextFieldValue("")
                itemScannerType= ""
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

private fun procesarDataUnitario(
    itemCorrelativo: PickingItem,
    usuario: String,
    pickingListState: MutableState<List<PickingDetalleItem>>,
    actualizarPickingList: (List<PickingDetalleItem>) -> Unit,
    itemScannerType: String,
    cantidad: Int,
    serieInicial: String,
    serieFinal: String,
    actualizarMensajeError: (String) -> Unit
) {
    val archivo = File("/data/data/com.makita.ubiapp/files", "picking_data_capturados.txt")

    if (archivo.exists()) {
        val validarSerie = validarSerieEnArchivo(itemCorrelativo.correlativo, serieInicial, archivo )

        if (validarSerie) {
            actualizarMensajeError("La serie ($serieInicial) ya ha sido capturada para este ítem (${itemCorrelativo.correlativo}). No se puede capturar nuevamente.")
            return // Salir de la función si la serie ya está registrada

        }

    }

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
        guardarArchivoPlano(listOf(item), "picking_data_capturados.txt" , usuario, itemCorrelativo,serieInicial,
            serieFinal)
    }
}

private fun procesarDataMaster(
    itemCorrelativo: PickingItem,
    usuario: String,
    pickingListState: MutableState<List<PickingDetalleItem>>,
    actualizarPickingList: (List<PickingDetalleItem>) -> Unit,
    itemScannerType: String,
    cantidad: Int,
    serieInicial: String,
    serieFinal: String,
    actualizarMensajeError: (String) -> Unit
) {
    val archivo = File("/data/data/com.makita.ubiapp/files", "picking_data_capturados.txt")

    if (archivo.exists()) {
        val validarSerie = validarSerieEnArchivo(itemCorrelativo.correlativo, serieInicial, archivo , serieFinal )

       if (validarSerie) {
            actualizarMensajeError("La serie ($serieInicial) ya ha sido capturada para este ítem (${itemCorrelativo.correlativo}). No se puede capturar nuevamente.")
            return // Salir de la función si la serie ya está registrada

        }

    }

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
        guardarArchivoPlano(
            listOf(item),
            "picking_data_capturados.txt" ,
            usuario,
            itemCorrelativo,
            serieInicial,
            serieFinal)
    }
}
fun guardarArchivoPlano(
    data: List<PickingDetalleItem>,
    nombreArchivo: String,
    usuario: String,
    itemCorrelativo: PickingItem,
    serieInicial: String,
    serieFinal: String
) {
    try {
        // Obtener la fecha y hora actual en el formato deseado
        val fechaHoraActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())

        // Crear el archivo o agregar nuevas líneas si ya existe
        val archivo = File("/data/data/com.makita.ubiapp/files", nombreArchivo)

        // Usar un bucle `while` para iterar desde `serieInicial` hasta `serieFinal` en formato de cadena
        var serieActual = serieInicial

        // Iterar con un bucle `while` sobre el rango de cadenas
        while (serieActual <= serieFinal) {
            // Transformar cada objeto a la línea en formato requerido
            val lineas = data.map { item ->
                "Makita;${item.TipoDocumento};${itemCorrelativo.correlativo};${item.linea};${item.Tipoitem};${item.item};${item.Descripcion};${item.Unidad};${item.Ubicacion};" +
                        "1;${item.Cantidad};$serieActual;${itemCorrelativo.CorrelativoOrigen};$usuario;$fechaHoraActual"
            }

            // Agregar las líneas al archivo
            archivo.appendText(lineas.joinToString("\n") + "\n")

            // Incrementar la serie manteniendo ceros a la izquierda
            serieActual = (serieActual.toInt() + 1).toString().padStart(serieInicial.length, '0')
        }

        val correlativo = itemCorrelativo.correlativo
        val linea = data.firstOrNull()?.linea?.toString() ?: ""
        val item = data.firstOrNull()?.item.orEmpty()

        // Crear la solicitud
        val request = DataUpdateCapturaReq(
            correlativo = correlativo,
            linea = linea,
            item = item
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {

                val response = RetrofitClient.apiService.updateCapturaEnProceso(request)
                if (response.isSuccessful) {
                    Log.d("guardarArchivoPlano", "Datos enviados exitosamente a la API.")
                } else {
                    Log.e("guardarArchivoPlano", "Error al enviar datos a la API: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("guardarArchivoPlano", "Error al enviar datos a la API: ${e.message}")
            }
        }
    } catch (e: Exception) {
        Log.e("guardarArchivoPlano", "Error al guardar el archivo: ${e.message}")
    }
}
fun validarSerieEnArchivo(correlativo: Int, nuevaSerie: String , archivo: File, serieFinal: String? = null): Boolean {

    // Leer las líneas del archivo
    val lineas = archivo.readLines()

    // Iterar sobre las líneas para verificar si el correlativo tiene asignada una serie
    for (linea in lineas) {
        val campos = linea.split(";")

        // Verificar que la línea tiene el número correcto de campos
        if (campos.size >= 15) {

            val correlativoArchivo = campos[2]
            val serieArchivo = campos[11]

            if (correlativo == correlativoArchivo.toInt() && serieArchivo == nuevaSerie) {

                return true
            }
        }
    }

    return false
}

fun leerArchivoCaptura(item: String?): List<String> {
    val rutaDirectorio = "/data/data/com.makita.ubiapp/files"
    val nombreArchivo = "picking_data_capturados.txt"
    val archivo = File(rutaDirectorio, nombreArchivo)

    if (archivo.exists()) {
        // Leer el contenido del archivo
        val contenido = archivo.readLines()

        // Filtrar todas las líneas donde el campo 6 (índice 5) coincide con el `item`
        val lineasEncontradas = contenido.filter { linea ->
            val campos = linea.split(";")
            campos.size > 5 && campos[5] == item
        }

        if (lineasEncontradas.isNotEmpty()) {
           Log.d("leo el archivo y obtengo esta data : " , "$lineasEncontradas")
            return lineasEncontradas
        } else {
            println("No se encontraron líneas con el item: $item")
        }
    } else {
        println("Archivo de captura no encontrado.")
    }

    return emptyList()
}

@Composable
fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center) // Centra el texto en la pantalla
    ) {
        Text(text = "Cargando...", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = GreenMakita)
    }
}

fun procesarDatos(navController: NavController,
                  usuario: String,
                  area: String,
                  vigencia : Long ,
                  idUsuario : Int ,
                  token: String,
                  actividades: List<ActividadItem>
                  ) {
    val capturas = leerArchivoCapturaCompleto()
    val username = usuario
    val rutaDirectorio = "/data/data/com.makita.ubiapp/files"
    val nombreArchivo = "picking_data_capturados.txt"

    val archivo = File(rutaDirectorio, nombreArchivo)

    if (capturas.isNotEmpty()) {
        val capturaList = InsertCapturaList(data = capturas)
        // Ejecutar la tarea de red en un hilo de fondo
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Llamada a la API para insertar los datos
                val response = RetrofitClient.apiService.insertarCapturasSeries(capturaList)

                // Si la respuesta es exitosa, navegar a la siguiente pantalla
                if (response.isSuccessful) {
                    Log.d("Proceso", "Datos enviados correctamente.")

                    //elimino el archivo en el proceso exitoso
                    archivo.delete()

                    // Cambiar al hilo principal para hacer la navegación
                    withContext(Dispatchers.Main) {
                        val actividadesJson = Gson().toJson(actividades)
                        val actividadesJsonEncoded = URLEncoder.encode(actividadesJson, StandardCharsets.UTF_8.toString())
                        navController.navigate("picking/$usuario/$area/$vigencia/$idUsuario/$token/$actividadesJsonEncoded")
                    }
                } else {
                    Log.e("Error Proceso", "Error al enviar datos: ${response.code()} - ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("Excepción Proceso", "Excepción al enviar datos: ${e.message}")
            }
        }
    } else {
        Log.e("Archivo", "No se encontraron datos en el archivo.")
    }
}

fun leerArchivoCapturaCompleto(): List<InsertCaptura> {
    val rutaDirectorio = "/data/data/com.makita.ubiapp/files"
    val nombreArchivo = "picking_data_capturados.txt"
    val archivo = File(rutaDirectorio, nombreArchivo)
    val capturas = mutableListOf<InsertCaptura>()

    if (archivo.exists()) {
        Log.d("Archivo", "Archivo de captura encontrado: ${archivo.path}")

        // Procesar cada línea del archivo
        archivo.forEachLine { linea ->
            val campos = linea.split(";")
            if (campos.size == 15) {
                Log.d("parametros","parametros $campos")
                val captura = InsertCaptura(
                    empresa = campos[0],
                    tipoDocumento = campos[1],
                    correlativo = campos[2],
                    linea = campos[3],
                    tipoItem = campos[4],
                    item = campos[5],
                    descripcion = campos[6],
                    unidad = campos[7],
                    ubicacion = campos[8],
                    cantidadPedida = campos[9],
                    cantidad = campos[10],
                    serieActual = campos[11],
                    usuario = campos[13],
                    fechaHoraActual = campos[14]
                )
                capturas.add(captura)
            } else {
                Log.e("Error Línea", "Línea con formato incorrecto: $linea")
            }
        }
        Log.d("Capturas", "Capturas obtenidas: $capturas")
        return capturas
    } else {
        Log.e("Archivo", "Archivo de captura no encontrado.")
    }

    return emptyList()
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
    val usuario = "juanito mena"
    val area= "Herraminetas"
    //DetalleDocumentoScreen(navController = navController, item = exampleItem , usuario, area )
}
