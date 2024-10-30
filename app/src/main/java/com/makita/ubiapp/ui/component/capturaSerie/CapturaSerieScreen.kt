package com.makita.ubiapp.ui.component.ubicaciones


import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.makita.ubiapp.PickingItem
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// conserva los datos cuando cambia de orientacion el dispositivo
val TextFieldValueCapturaSerie: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun CapturaSerieScreen(navController: NavController) {

    var folioText by remember { mutableStateOf("") }
    var pickingList by remember { mutableStateOf<List<PickingItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Estado para el loading
    val coroutineScope = rememberCoroutineScope() // Remember a coroutine scope

    fun cargarTodaLaData() {
        isLoading = true
        coroutineScope.launch {
            try {
                delay(1000) // Simulación de espera
                val response = RetrofitClient.apiService.obtenerPickinglist()
                if (response.isSuccessful && response.body() != null) {
                    pickingList = response.body()!!.data
                    errorMessage = null
                    Log.d("*MAKITA*", "CapturaSerieScreen obtenerPickinglist ${pickingList}")
                } else {
                    errorMessage = "Error al obtener los datos: ${response.code()}"
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

    val fetchPickingListByFolio: (String) -> Unit = { folioValue ->
        Log.d("*MAKITA*" , "texto que voy ingresando : $folioValue ")
        isLoading = true
        coroutineScope.launch {
            try {
                val responseFolio = RetrofitClient.apiService.obtenerPickingFolio(folioValue)
                if (responseFolio.isSuccessful && responseFolio.body() != null) {
                    pickingList = responseFolio.body()!!.data
                    errorMessage = null
                } else {
                    errorMessage = "No se encontró el folio proporcionado."
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
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
                    EscanearItemTextField(
                        text = folioText,
                        onTextChange = { folioText = it },
                        onApiCall = fetchPickingListByFolio,
                        onReloadData = {
                            folioText=""
                            cargarTodaLaData()

                        }
                    )
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
                        PickingListTable(pickingList)
                    }
            }

            Separar()
            Footer(navController, onActualizarClick = {
                cargarTodaLaData()
                folioText= ""
            })
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
    onReloadData: () -> Unit) {

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
                    onReloadData() // Llama a la función para recargar los datos si está vacío
                } else {
                    debounceJob = coroutineScope.launch {
                        delay(500) // Tiempo de debounce (ajustable)
                        if (value.isNotEmpty()) {
                            onApiCall(value) // Llama a la función que consulta la API
                        }
                    }
                }
            }// Actualiza el estado
        },
        label = {                                                                                                             Text("Buscar Folio") },  // Etiqueta del campo
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
@Composable
fun PickingListTable(pickingList: List<PickingItem>?) {
    Log.d("*MAKITA*", ": $pickingList")

    // Definir las cabeceras y los campos que deseas mostrar
    val headers = listOf(
        "Folio","Documento Origen", "Entidad", "Fecha Documento" , "Nombre Cliente"
    )

    val fields = listOf(

        { item: PickingItem -> item.CorrelativoOrigen.toString() },
        { item: PickingItem -> item.DocumentoOrigen ?: "Sin Documento" },
        { item: PickingItem -> item.entidad ?: "Sin Entidad" },
        { item: PickingItem -> formatDate(item.Fecha ?: "Sin Fecha") },
        { item: PickingItem -> item.nombrecliente ?: "Sin Nombre" },


        )

    // Contenedor principal
    Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
        // Columna que contiene tanto las cabeceras como las filas
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
                        color = GreenMakita,

                    )
                }
            }

            // Usar LazyColumn para el desplazamiento vertical de los datos
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.5f)
                    .padding(top = 8.dp)
                    ,
            ) {
                // Mostrar los elementos de la lista, omitiendo los primeros 9 elementos
                items(pickingList ?: emptyList()) { item ->
                    // Fila que contiene los datos de cada item
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        fields.forEach { field ->
                            Text(
                                text = field(item),
                                color = Color.Black,
                                modifier = Modifier
                                    .width(130.dp) // Ajusta el ancho según sea necesario
                                    .padding(horizontal = 5.dp)
                                    .padding(vertical = 8.dp),
                                fontSize = 12.sp,
                                maxLines = 1, // Permite un máximo de 1 línea
                                overflow = TextOverflow.Ellipsis // desbordamineto
                            )
                        }
                    }

                }
            }
        }
    }
}
@Composable
fun Footer(navController: NavController, onActualizarClick: () -> Unit) {
    var isButtonVolver by remember { mutableStateOf(false) }
    var isButtonActualizar by remember { mutableStateOf(false) }

    // Botón de búsqueda con animación

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .weight(1f) // Este botón ocupará el espacio restante
                .padding(horizontal = 8.dp)
                .padding(start = 10.dp, end = 5.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00909E)  // GreenMakita
            )// Ajuste de padding

        ) {
            Text("Volver", color = Color.White)
        }

        Button(
            onClick = { onActualizarClick() },
            modifier = Modifier
                .weight(1f) // Este botón también ocupará el espacio restante
                .padding(horizontal = 8.dp)
                .padding(start = 5.dp, end = 10.dp), // Ajuste de padding

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00909E)  // GreenMakita
            )
        ) {
            Text("Actualizar", color = Color.White)
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

































