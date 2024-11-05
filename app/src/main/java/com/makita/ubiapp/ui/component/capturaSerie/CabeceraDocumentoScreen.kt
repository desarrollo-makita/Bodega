package com.makita.ubiapp.ui.component.capturaSerie

import android.util.Log
import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height



import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState


import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Divider

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.google.gson.Gson
import com.makita.ubiapp.PickingItem
import com.makita.ubiapp.ui.theme.GreenMakita


// conserva los datos cuando cambia de orientacion el dispositivo
val TextFieldValueCapturaSerie: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)

@Composable
fun CabeceraDocumentoScreen(navController: NavController , item: PickingItem) {

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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState) // Agrega el scroll vertical
                .background(Color.White, shape = RoundedCornerShape(10.dp)),
            verticalArrangement = Arrangement.Top, // Coloca todo en la parte superior
            horizontalAlignment = Alignment.CenterHorizontally // Centra horizontalmente
        )
        {
            Titulo()
            Separar()
            PrimeraLineaTextField(item)
            SegundaLineaTextField(item)
            EmpresaTextField(item)
            ClienteTextField(item)
            DireccionTextField(item)
            BodegaTextField(item)
            TotalItemTextField(item)
            ReadOnlyTextArea(item)
            Separar()
            Footer(navController , item)

        }

    }

}

@Composable
fun Titulo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Text(
            text = "PIKING DESPACHO",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center),
            color = Color(0xFF00909E)
        )
    }
}

@Composable
fun Separar(){
    Divider(
        color = Color(0xFFFF7F50),
        thickness = 2.dp,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .padding(8.dp),
        )
}

@Composable
fun PrimeraLineaTextField(item : PickingItem) {

    // Estado para el valor del texto
    val documentoOrigen = remember { mutableStateOf(TextFieldValue(item.DocumentoOrigen)) }
    val nuemeroDocumento = remember { mutableStateOf(TextFieldValue(item.correlativo.toString())) }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = documentoOrigen.value,
            onValueChange = { newValue -> documentoOrigen.value = newValue },
            label = { Text(
                "Documento" , fontSize = 15.sp, color = Color(0xFF00909E)) },
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
            value = nuemeroDocumento.value,
            onValueChange = { newValue -> nuemeroDocumento.value = newValue },
            label = { Text("N° Documento" , fontSize = 15.sp ,color = Color(0xFF00909E)) }, // Label que dice "Documento"
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
            ), enabled = false
        )

        // Aquí puedes agregar otros elementos si lo necesitas (como un botón)
    }
}

@Composable
fun SegundaLineaTextField(item : PickingItem) {

    // Estado para el valor del texto
    val numeroFolio = remember { mutableStateOf(TextFieldValue(item.CorrelativoOrigen.toString())) }
    val rut = remember { mutableStateOf(TextFieldValue(item.entidad)) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = numeroFolio.value,
            onValueChange = { newValue -> numeroFolio.value = newValue },
            label = { Text("N° Folio" , fontSize = 15.sp , color = Color(0xFF00909E)) },
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
            value = rut.value,
            onValueChange = { newValue -> rut.value = newValue },
            label = { Text("Rut" , fontSize = 15.sp , color = Color(0xFF00909E) ) }, // Label que dice "Documento"
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

        // Aquí puedes agregar otros elementos si lo necesitas (como un botón)
    }
}

@Composable
fun EmpresaTextField(item : PickingItem) {

    val empresa = remember { mutableStateOf(TextFieldValue(item.empresa.uppercase())) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = empresa.value,
            onValueChange = { newValue -> empresa.value = newValue },
            label = { Text("Empresa" , fontSize = 15.sp , color = Color(0xFF00909E) ) },
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
        ) // Aquí puedes agregar otros elementos si lo necesitas (como un botón)
    }
}
@Composable
fun ClienteTextField(item : PickingItem) {

    val cliente = remember { mutableStateOf(TextFieldValue(item.nombrecliente.uppercase())) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = cliente.value,
            onValueChange = { newValue -> cliente.value = newValue },
            label = { Text("Cliente" , fontSize = 15.sp , color = Color(0xFF00909E) ) },
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
        ) // Aquí puedes agregar otros elementos si lo necesitas (como un botón)
    }
}
@Composable
fun DireccionTextField(item : PickingItem) {

    val direccion = remember { mutableStateOf(TextFieldValue(item.Direccion.uppercase())) }
    val comuna = remember { mutableStateOf(TextFieldValue(item.comuna.uppercase())) }
    val ciudad = remember { mutableStateOf(TextFieldValue(item.Ciudad.uppercase())) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = direccion.value,
            onValueChange = { newValue -> direccion.value = newValue },
            label = { Text("Direccion", fontSize = 15.sp, color = Color(0xFF00909E)) },
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
        ) // Aquí puedes agregar otros elementos si lo necesitas (como un botón)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = comuna.value,
            onValueChange = { newValue -> comuna.value = newValue },
            label = { Text("Comuna", fontSize = 15.sp, color = Color(0xFF00909E)) },
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
            value = ciudad.value,
            onValueChange = { newValue -> ciudad.value = newValue },
            label = { Text("Ciudad", fontSize = 15.sp, color = Color(0xFF00909E)) },
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

}

@Composable
fun BodegaTextField(item : PickingItem) {

    val bodegaOrigen = remember { mutableStateOf(TextFieldValue(item.Bodorigen)) }
    val bodegaDestino = remember { mutableStateOf(TextFieldValue(item.Boddestino)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = bodegaOrigen.value,
            onValueChange = { newValue -> bodegaOrigen.value = newValue },
            label = { Text("Bod. Origen", fontSize = 15.sp, color = Color(0xFF00909E)) },
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
            value = bodegaDestino.value,
            onValueChange = { newValue -> bodegaDestino.value = newValue },
            label = { Text("Bod. Destino", fontSize = 15.sp, color = Color(0xFF00909E)) },
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




}

@Composable
fun TotalItemTextField(item : PickingItem) {
    val totalItems = remember { mutableStateOf(TextFieldValue(item.Total_Items.toString())) }
    Log.d("*Makita" ,"TOTALITEMS : $totalItems")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = totalItems.value,
            onValueChange = { newValue -> totalItems.value = newValue },
            label = { Text("Total Items", fontSize = 15.sp, color = Color(0xFF00909E)) },
            modifier = Modifier
                .wrapContentWidth() // Ajusta el ancho al contenido
                .padding(end = 10.dp) // Ajusta el padding si es necesario
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
}


@Composable
fun ReadOnlyTextArea(item: PickingItem) {
    val glosa = remember { mutableStateOf(TextFieldValue(item.glosa)) }

    OutlinedTextField(
        value = glosa.value,
        onValueChange = { newValue -> glosa.value = newValue },
        label = { Text("Glosa", fontSize = 15.sp, color = Color(0xFF00909E)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .height(80.dp), // Ajusta la altura para simular un textarea
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
            color = GreenMakita,
            fontSize = 16.sp
        ),
        enabled = false, // Si necesitas que sea solo lectura
        maxLines = 5, // Permite varias líneas
        singleLine = false
    )
}


@Composable
fun Footer(navController: NavController , item: PickingItem) {
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
            onClick = {
                val itemJson = Gson().toJson(item)
                navController.navigate("detalle-documento/$itemJson")},
            modifier = Modifier
                .weight(1f) // Este botón también ocupará el espacio restante
                .padding(horizontal = 8.dp)
                .padding(start = 5.dp, end = 10.dp),

            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00909E)  // GreenMakita
            )
        ) {
            Text("Siguiente", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CabeceraDocumentoScreenPreview() {
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

    // Usar un navController simulado (puedes usar rememberNavController)
    val navController = rememberNavController()

    // Llamar al composable que deseas previsualizar
    CabeceraDocumentoScreen(navController = navController, item = exampleItem)
}
