package com.makita.ubiapp.ui.component.capturaSerie

import android.util.Log
import androidx.compose.foundation.background

import androidx.compose.foundation.horizontalScroll

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue


import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation.compose.rememberNavController


import com.makita.ubiapp.PickingDetalleItem

import com.makita.ubiapp.PickingItem
import com.makita.ubiapp.RetrofitClient



import com.makita.ubiapp.ui.theme.GreenMakita
import com.makita.ubiapp.ui.util.procesarTextoEscaneado

import kotlinx.coroutines.launch


// conserva los datos cuando cambia de orientacion el dispositivo
val TextFieldValueCapturaSeries: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun DetalleDocumentoScreen(navController: NavController, item:PickingItem) {
    Log.d("*MAKITA*" , "lOGOGOGOGOGO : $item")
    val coroutineScope = rememberCoroutineScope() // Remember a coroutine scope
    var pickingList by remember { mutableStateOf<List<PickingDetalleItem>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }



    // Llama a cargarTodaLaData al entrar a CapturaSerieScreen
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {

                val response = RetrofitClient.apiService.obtenerPickingCorrelativoDetalle(item.correlativo.toString())

                if (response.isSuccessful && response.body() != null) {
                    pickingList = response.body()!!.data
                    errorMessage = null
                    Log.d("*MAKITA*", "DetalleDocumentoScreen  ${pickingList}")
                } else {
                    errorMessage = "Error al obtener los datos: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de red: ${e.localizedMessage}"
            } finally {

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
        .padding(8.dp))
    {

        Column(
            modifier = Modifier
                .fillMaxSize()
                // Agrega el scroll vertical
                .background(Color.White, shape = RoundedCornerShape(10.dp)),
            verticalArrangement = Arrangement.Top, // Coloca todo en la parte superior
            horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente
        )
        {
            HeaderDetalle(item)
            SepararDetalle()
            ItemListTable(navController , pickingList)
            SepararDetalle()
            FooterProcesar(navController)
            CapturaScanner(pickingList)



        }

    }

}
@Composable
fun HeaderDetalle(item: PickingItem) {
    // Estado para el valor del texto
    val correlativo = remember { mutableStateOf(TextFieldValue(item.correlativo.toString())) }
    val folio = remember { mutableStateOf(TextFieldValue(item.CorrelativoOrigen.toString())) }
    val cliente = remember { mutableStateOf(TextFieldValue(item.nombrecliente)) }

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
        }
    }
}
@Composable
fun SepararDetalle(){
    Divider(
        color = Color(0xFFFF7F50),
        thickness = 2.dp,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .padding(8.dp),
    )
}

@Composable
fun ItemListTable(navController: NavController, pickingList: List<PickingDetalleItem>?) {
    Log.d("*MAKITA*", ": $pickingList")

    // Definir las cabeceras y los campos que deseas mostrar
    val headers = listOf("Item", "Descripcion", "Cantidad", "Catidad Pedido" , "Tipo Documento" , "Tipo item",  "Unidad","Ubicacion")

    val fields = listOf<(PickingDetalleItem) -> String>(
        { item -> item.item?: "Sin item" },
        { item -> item.Descripcion?: "Sin item" },
        { item -> item.Cantidad.toString() ?: "Sin Caantidad" },
        { item -> item.CantidadPedida.toString() ?: "sin cantidadPedida" },
        { item -> item.TipoDocumento ?: "Sin TipoDocumento" },
        { item -> item.Tipoitem ?: "Sin tipoItem" },
        { item -> item.Unidad ?: "Sin Unidad" },
        { item -> item.Ubicacion ?: "Sin ubicacion" }
    )
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
                        maxLines = 2,
                        color = GreenMakita
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                items(pickingList ?: emptyList()) { item ->

                    val backgroundColor = if (item.Cantidad == item.CantidadPedida) GreenMakita else Color.Transparent

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(backgroundColor),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        fields.forEachIndexed { index, field ->

                            Text(
                                text = field(item),
                                modifier = Modifier
                                    .width(130.dp)
                                    .padding(horizontal = 5.dp)
                                    .padding(vertical = 8.dp),
                                fontSize = 12.sp,
                                maxLines = 2,

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
    Column(modifier = Modifier.fillMaxWidth()) {

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E))
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Procesar", tint = Color.White)
            Text("Procesar", color = Color.White)
        }

        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E))
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
            Text("Actualizar", color = Color.White)
        }

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E))
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = "Salir", tint = Color.White)
            Text("Salir", color = Color.White)
        }
    }
}


@Composable
fun CapturaScanner(pickingList: List<PickingDetalleItem>?) {

    val textoEntrada = remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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

                // Nuevo OutlinedTextField para el cliente que ocupa el ancho completo
                OutlinedTextField(
                    value = textoEntrada.value,
                    onValueChange = { newValue ->
                        textoEntrada.value = newValue
                        val resultado = procesarTextoEscaneado(newValue.text , pickingList)
                        println("Resultado procesado: $resultado")
                                    },
                    label = { Text("Scanear!!", fontSize = 15.sp, color = Color(0xFF00909E)) },
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa todo el ancho disponible
                        .height(70.dp)
                        .focusRequester(focusRequester), // Mantiene la altura consistente
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

                )
            }
        }
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