package com.makita.ubiapp.ui.component.ubicaciones


import android.media.RouteListingPreference
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.gson.Gson
import com.makita.ubiapp.ActividadItem
import com.makita.ubiapp.CorrelativoRequest

import com.makita.ubiapp.PickingItem
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.component.database.AppDatabase
import com.makita.ubiapp.ui.component.entity.PickingItemEntity

import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun CapturaSerieScreen(navController: NavController,
                       username:String ,
                       area : String,
                       vigencia : Long ,
                       idUsuario : Int ,
                       token: String,
                       actividades: List<ActividadItem>) {

    var folioText by remember { mutableStateOf("") }
    var pickingList by remember { mutableStateOf<List<PickingItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Estado para el loading
    val coroutineScope = rememberCoroutineScope() // Remember a coroutine scope

    var usuarioActivo by remember { mutableStateOf<String?>(username) }
    var area1 by remember { mutableStateOf<String?>(area) }

    var showDialog by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var itemPendiente by remember { mutableStateOf<PickingItem?>(null) }
    var showDialogErrorVacio by remember { mutableStateOf(false) }

    fun verificarDatosPendientes(pickingList: List<PickingItem>): PickingItem? {
        val rutaArchivo = "/data/data/com.makita.ubiapp/files/picking_data_capturados.txt"
        val lineasArchivo = leerArchivo(rutaArchivo)

        pickingList.forEach { item ->
            val correlativo = item.CorrelativoOrigen.toString()
            // Verificar si el correlativo ya existe en el archivo
            val existeEnArchivo = lineasArchivo.any { it.split(";")[12] == correlativo }
            Log.d("*PROCESO CAPTURA**", "RESULTADA _ : $existeEnArchivo")

            if (existeEnArchivo) {
                mensajeDialogo = "El Correlativo $correlativo tiene procesos pendientes ¿Deseas Procesar?"
                showDialog = true
                itemPendiente = item
                return item // Retorna el primer item encontrado
            }
        }

        // Si no se encuentra ningún item con procesos pendientes, retorna null
        return null
    }

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
                        navController.navigate("menu/$username/$area/$vigencia/$idUsuario/$token/0/$actividadesJsonEncoded")
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
    fun cargarTodaLaData() {
        isLoading = true
        coroutineScope.launch {
            try {
                delay(1000) // Simulación de espera
                val response = RetrofitClient.apiService.obtenerPickinglist(area1.toString())
                if (response.isSuccessful && response.body() != null) {
                    pickingList = response.body()!!.data
                    errorMessage = null

                    Log.d("*MAKITA*", "pickingList** ${pickingList!!.size}")
                    verificarDatosPendientes(pickingList!!)

                } else {
                    val jsonError = JSONObject(response.errorBody()?.string())

                    errorMessage = jsonError.getString("error")
                    println("Error al obtener los datos: $errorMessage")

                    mensajeDialogo = errorMessage as String
                    showDialogErrorVacio = true

                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    // Llama a cargarTodaLaData al entrar a CapturaSerieScreen
    LaunchedEffect(Unit) {
        cargarTodaLaData()
    }

    if (showDialog && itemPendiente != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Alerta") },
            text = { Text(mensajeDialogo) },
            confirmButton = {
                Button(
                    onClick = {
                        val actividadesJson = Gson().toJson(actividades)
                        val actividadesJsonEncoded = URLEncoder.encode(actividadesJson, StandardCharsets.UTF_8.toString())
                        showDialog = false
                        navController.navigate("cabecera-documento/${Gson().toJson(itemPendiente)}/$username/$area/$vigencia/$idUsuario/$token/$actividadesJsonEncoded")
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)  // Color de fondo del botón
                    )
                ) {
                    Text("Procesar")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        eliminarArchivo("/data/data/com.makita.ubiapp/files/picking_data_capturados.txt" ,
                            itemPendiente!!
                        )
                        showDialog = false
                },colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00909E)  // Color de fondo del botón
                    )) {
                    Text("Ignorar")
                }
            }
        )
    }
    val fetchPickingListByFolio: (String) -> Unit = { folioValue ->
        Log.d("*MAKITA*", "Texto que voy ingresando: $folioValue")

        // Evitar que entre en loading cuando ya no estamos llamando a la API
        isLoading = false


        // Filtrar la lista para encontrar todos los elementos que contengan el texto ingresado
        val matchingItems = pickingList?.filter {
            it.CorrelativoOrigen.toString().contains(folioValue, ignoreCase = true) // Busca coincidencias en cualquier parte del valor
        }

        if (matchingItems.isNullOrEmpty()) {
            errorMessage = "No se encontraron coincidencias para el folio proporcionado."
        } else {
            pickingList = matchingItems // Actualiza la lista a solo los elementos que coinciden
            errorMessage = null // Limpia cualquier mensaje de error previo
        }
    }


    // Fondo degradado
    Box(modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00909E),
                        Color(0xFF80CBC4)
                    )
                )
            )
            .padding(10.dp))
    {
        Column(
            modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(Color.White, shape = RoundedCornerShape(30.dp)),
            verticalArrangement = Arrangement.Top, // Coloca todo en la parte superior
            horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente
        )
        {
            Titulo()
            Separar()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 0.dp) // Menor margen
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    pickingList?.let {
                        EscanearItemTextField(
                            text = folioText,
                            onTextChange = { folioText = it },
                            onApiCall = { folio -> fetchPickingListByFolio(folio) },
                            onReloadData = {
                                folioText=""
                                cargarTodaLaData()

                            },
                            pickingList = it
                        )
                    }
                }
                ErrorMessage(errorMessage)
            }


            Spacer(modifier = Modifier.height(25.dp))


            if(isLoading){
                LoadingIndicator()
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp) // Menor margen
                )
                    {
                        PickingListTable(navController, pickingList , usuarioActivo , area1, vigencia , idUsuario, token , actividades )
                    }
            }

            Separar()
            Footer(navController, onActualizarClick = {
                cargarTodaLaData()
                folioText= ""
            },username , area ,vigencia , idUsuario, token , actividades)
        }
    }
}
@Composable
fun Titulo() {
    Text(
        text = "LISTADO PIKING DESPACHO",
        fontSize =20.sp, // Tamaño de fuente más pequeño
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .width(500.dp) // Ajusta el ancho a 200 dp (cambia según tus necesidades)
            .height(70.dp) // Ajusta la altura a 40 dp (cambia según tus necesidades)
            .padding(top = 40.dp)
            .padding(start = 50.dp),
        color = Color(0xFF00909E), // Color verde makit

    )
}

@Composable
fun Separar(){
    Divider(
        color = Color(0xFFFF7F50),
        thickness = 2.dp,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .padding(10.dp)
        ,

    )
}

@Composable
fun EscanearItemTextField(
    text: String,
    onTextChange: (String) -> Unit,
    onApiCall: (String) -> Unit,
    onReloadData: () -> Unit,
    pickingList: List<PickingItem>) {

    val coroutineScope = rememberCoroutineScope()
    var debounceJob by remember { mutableStateOf<Job?>(null) }


    OutlinedTextField(
        value = text.uppercase(),  // Muestra el texto en mayúsculas
        onValueChange = { newText ->

            val value = newText.filter { it.isDigit() }.take(10)
            onTextChange(value)
            debounceJob?.cancel()

            debounceJob = coroutineScope.launch {
                delay(500) // Tiempo de debounce (ajustable)

                if (value.isEmpty()) {
                    onReloadData()
                } else {

                    val filteredList = pickingList.filter {
                        it.CorrelativoOrigen.toString().contains(value, ignoreCase = true)
                    }
                    onApiCall(value)
                }
            }// Actualiza el estado
        },
        label = {Text("Buscar Folio" , fontWeight = FontWeight.Bold) },  // Etiqueta del campo
        modifier = Modifier
            .width(200.dp)
            .height(60.dp)
            .padding(start = 20.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF00909E),
            unfocusedBorderColor = Color(0xFF00909E),
            focusedLabelColor = Color(0xFF00909E),
            unfocusedLabelColor = Color(0xFF00909E),
            cursorColor = Color(0xFF00909E)
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)

    )
}

fun leerArchivo(rutaArchivo: String): List<String> {
    return try {
        File(rutaArchivo).readLines()
    } catch (e: Exception) {
        Log.e("Error", "No se pudo leer el archivo: ${e.localizedMessage}")
        emptyList()
    }
}

// Función para eliminar el archivo
fun eliminarArchivo(rutaArchivo: String, itemPendiente : PickingItem) {
    try {
        CoroutineScope(Dispatchers.IO).launch {

            val request = CorrelativoRequest(itemPendiente.correlativo)
            RetrofitClient.apiService.updateCapturaSolicitado(request)

        }
        val archivo = File(rutaArchivo)
        if (archivo.exists()) {
            val eliminado = archivo.delete()
            if (eliminado) {
                Log.d("ELIMINAR", "Archivo eliminado correctamente.")
            } else {
                Log.e("ELIMINAR", "No se pudo eliminar el archivo.")
            }
        }
    } catch (e: Exception) {
        Log.e("ELIMINAR", "Error al eliminar el archivo: ${e.localizedMessage}")
    }
}

@Composable
fun PickingListTable(navController: NavController,
                     pickingList: List<PickingItem>? ,
                     usuarioActivo : String?,
                     area : String?,
                     vigencia :Long,
                     idUsuario: Int,
                     token: String,
                     actividades: List<ActividadItem>) {
    Log.d("*MAKITA*", ": $pickingList")

    // Definir las cabeceras y los campos que deseas mostrar
    val headers = listOf("Folio", "Documento Origen", "Entidad", "Fecha Documento", "Nombre Cliente")
    val fields = listOf<(PickingItem) -> String>(
        { item -> item.CorrelativoOrigen.toString() },
        { item -> item.DocumentoOrigen ?: "Sin Documento" },
        { item -> item.entidad ?: "Sin Entidad" },
        { item -> formatDate(item.Fecha ?: "Sin Fecha") },
        { item -> item.nombrecliente ?: "Sin Nombre" }
    )

    // Contenedor principal
    Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        Column {
            // Crear las cabeceras en una fila fija
            Row(modifier = Modifier.fillMaxWidth()) {
                headers.forEach { header ->
                    Text(
                        text = header,
                        modifier = Modifier
                            .width(130.dp)
                            .padding(horizontal = 5.dp)
                            .padding(vertical = 10.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        maxLines = 1,
                        color = GreenMakita
                    )
                }
            }

            // Usar LazyColumn para el desplazamiento vertical de los datos
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .padding(top = 8.dp)
            ) {
                items(pickingList ?: emptyList()) { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        fields.forEachIndexed { index, field ->
                            val textColor = if (index == 0) Color.Blue else Color.Black
                            Text(
                                text = field(item),
                                color = textColor,
                                modifier = Modifier
                                    .width(130.dp)
                                    .padding(horizontal = 5.dp)
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        if (index == 0) { // Solo permitir clics en el primer campo
                                            val itemJson = Gson().toJson(item) // Serializa el objeto PickingItem a JSON
                                            val actividadesJson = Gson().toJson(actividades)
                                            val actividadesJsonEncoded = URLEncoder.encode(actividadesJson, StandardCharsets.UTF_8.toString())
                                            Log.d("*MAKITA", "$itemJson $usuarioActivo $area")
                                            navController.navigate("cabecera-documento/$itemJson/$usuarioActivo/$area/$vigencia/$idUsuario/$token/$actividadesJsonEncoded")
                                        }
                                    },
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Footer(navController: NavController, onActualizarClick: () -> Unit,
           username: String,
           area: String,
           vigencia :Long,
           idUsuario: Int,
           token: String,
           actividades: List<ActividadItem>) {
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo el tamaño de la pantalla
            .padding(bottom = 16.dp)
    ) {

        Spacer(modifier = Modifier.fillMaxSize())
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
        ) {
            Button(
                onClick = {
                    val actividadesJson = Gson().toJson(actividades)
                    val actividadesJsonEncoded = URLEncoder.encode(actividadesJson, StandardCharsets.UTF_8.toString())
                    navController.navigate("menu/$username/$area/$vigencia/$idUsuario/$token/0/$actividadesJsonEncoded")
                },
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio disponible
                    .padding(end = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00909E)  // Color de fondo del botón
                )
            ) {
                Text("Volver", color = Color.White)
            }

            Button(
                onClick = { onActualizarClick() },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00909E)
                )
            ) {
                Text("Actualizar", color = Color.White)
            }
        }
    }
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
@Composable
fun ErrorMessage(errorMessage: String?) {
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = Color.Red,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), // Texto en negrita
            modifier = Modifier
                .wrapContentWidth()
                .padding(start = 20.dp)
                .padding(top = 8.dp),
            maxLines = 1, // Limitar a una sola línea
            overflow = TextOverflow.Ellipsis // Muestra "..." si el texto se desborda
        )
    }
}

fun formatDate(isoDate: String): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val localDateTime = LocalDateTime.parse(isoDate, formatter)
    return localDateTime.toLocalDate().toString() // Solo devuelve la parte de la fecha
}




@Preview(showBackground = true)
@Composable
fun CapturaSerieScreenView() {
    val navController = rememberNavController()
    val usuario = "juanito Mena"
    val area = "Accesorios"
   // CapturaSerieScreen(navController = navController , usuario , area)
}


































