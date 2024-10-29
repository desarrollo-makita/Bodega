package com.makita.ubiapp.ui.component.ubicaciones


import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween

import androidx.compose.foundation.background

import androidx.compose.foundation.horizontalScroll

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider

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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.PickingItem
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


// conserva los datos cuando cambia de orientacion el dispositivo
val TextFieldValueCapturaSerie: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun CapturaSerieScreen() {

    var folioText by remember { mutableStateOf("") }
    var pickingList by remember { mutableStateOf<List<PickingItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) } // Estado para el loading

    LaunchedEffect(Unit) {
        try {
            delay(1000)
            val response = RetrofitClient.apiService.obtenerPickinglist()
            if (response.isSuccessful && response.body() != null) {
                pickingList = response.body()!!.data
                Log.d("*MAKITA*", "CapturaSerieScreen obtenerPickinglist ${pickingList}")
            } else {
                errorMessage = "Error al obtener los datos: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Error de red: ${e.localizedMessage}"
        }finally {

            isLoading = false // Cambia el estado de loading a false al finalizar
        }
    }

    // Fondo degradado
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
            .padding(10.dp),


        ) {

        Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(1.dp)
                    .background(Color.White, shape = RoundedCornerShape(30.dp)),
                verticalArrangement = Arrangement.Top, // Coloca todo en la parte superior
                horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente



            ) {
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
                        EscanearItemTextField(text = remember { mutableStateOf(folioText) })
                        BuscarButton()

                    }

                }
                Spacer(modifier = Modifier.height(25.dp))

            if(isLoading){
                LoadingIndicator()
            }else{
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp) // Menor margen
                ) {

                    PickingListTable(pickingList)
                }
            }

                Separar()
                Footer()
            }
        }
}


@Composable
fun BuscarButton(){
    var isButtonPressed by remember { mutableStateOf(false) }
    // Botón de búsqueda con animación
    val buttonColor by animateColorAsState(
        targetValue = if (isButtonPressed) Color(0xFF00796B) else Color(0xFF009688),
        animationSpec = tween(durationMillis = 300)
    )
    val buttonElevation by animateDpAsState(
        targetValue = if (isButtonPressed) 4.dp else 8.dp,
        animationSpec = tween(durationMillis = 300)
    )
    Button(
        onClick = { isButtonPressed = !isButtonPressed },
        modifier = Modifier
            .width(120.dp)
            .height(38.dp) // Ajuste de altura para coincidir con TextField
            .padding(horizontal = 8.dp)
            .padding(start = 10.dp),

        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = buttonElevation)
    ) {
        Text("Buscar", color = Color.White)
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
        color = Color(0xFF009688), // Color verde makit

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
fun EscanearItemTextField(text: MutableState<String>,  // Cambia a MutableState para poder modificarlo
) {
    OutlinedTextField(
        value = text.value.uppercase(),
        onValueChange = { newText ->
            // Actualiza el estado cuando el texto cambia
            text.value = newText.take(20).trim() // Limita a 20 caracteres
        },
        label = {
            Text("Buscar Folio") // Agrega un label descriptivo
        },
        modifier = Modifier
            .width(200.dp) // Ajusta el ancho a 200 dp (cambia según tus necesidades)
            .height(60.dp) // Ajusta la altura a 40 dp (cambia según tus necesidades)
            .padding(start = 20.dp),
        shape =  RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenMakita,
            unfocusedBorderColor = GreenMakita,
            focusedLabelColor = GreenMakita,
            unfocusedLabelColor = GreenMakita,
            cursorColor = GreenMakita
        )
        )
}


@Composable
fun PickingListTable(pickingList: List<PickingItem>?) {
    Log.d("*MAKITA*", ": $pickingList")

    // Definir las cabeceras y los campos que deseas mostrar
    val headers = listOf(
        "Correlativo","Documento Origen", "Entidad", "Fecha Documento" , "Nombre Cliente"
    )

    val fields = listOf(

        { item: PickingItem -> item.correlativo.toString() },
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
                        fontSize = 12.sp,
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
                items(pickingList?.drop(9) ?: emptyList()) { item ->
                    // Fila que contiene los datos de cada item
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween) {
                        fields.forEach { field ->
                            Text(
                                text = field(item),
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
fun Footer() {
    var isButtonVolver by remember { mutableStateOf(false) }
    var isButtonActualizar by remember { mutableStateOf(false) }

    // Botón de búsqueda con animación
    val buttonColor by animateColorAsState(
        targetValue = if (isButtonVolver || isButtonActualizar) Color(0xFF00796B) else Color(0xFF009688),
        animationSpec = tween(durationMillis = 300)
    )
    val buttonElevation by animateDpAsState(
        targetValue = if (isButtonVolver || isButtonActualizar) 4.dp else 8.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { isButtonVolver = !isButtonVolver },
            modifier = Modifier
                .weight(1f) // Este botón ocupará el espacio restante
                .padding(horizontal = 8.dp)
                .padding(start = 10.dp, end = 5.dp), // Ajuste de padding

            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = buttonElevation)
        ) {
            Text("Volver", color = Color.White)
        }

        Button(
            onClick = { isButtonActualizar = !isButtonActualizar },
            modifier = Modifier
                .weight(1f) // Este botón también ocupará el espacio restante
                .padding(horizontal = 8.dp)
                .padding(start = 5.dp, end = 10.dp), // Ajuste de padding

            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = buttonElevation)
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

fun formatDate(isoDate: String): String {
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val localDateTime = LocalDateTime.parse(isoDate, formatter)
    return localDateTime.toLocalDate().toString() // Solo devuelve la parte de la fecha
}






























@Preview(showBackground = true)
@Composable
fun PreviewCapturaSerieScreen() {
    CapturaSerieScreen()
}




