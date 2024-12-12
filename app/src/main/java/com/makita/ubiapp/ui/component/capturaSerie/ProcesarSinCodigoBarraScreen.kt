package com.makita.ubiapp.ui.component.capturaSerie
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues


import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.PowerOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.makita.ubiapp.ui.component.ubicaciones.leerArchivo

import com.makita.ubiapp.ui.theme.GreenMakita
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


val TextFieldValueSinCodigo: Saver<TextFieldValue, String> = Saver(
    save = { it.text }, // Guarda solo el texto
    restore = { TextFieldValue(it) } // Restaura el estado del texto en un nuevo TextFieldValue
)
@Composable
fun ProcesarSinCodigoBarraScreen(
    navController: NavController ,
    item: PickingDetalleItem,
    correlativoOrigen : Int ,
    correlativo : Int,
    username : String,
    area : String
    )
{

    var showExitoso by remember { mutableStateOf(false) }
    var isSwitchChecked by remember { mutableStateOf(false) } // Estado del Switch
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
                TituloProcesoSinCodigo()
                Separar()
                Item(item)
                Descripcion(item)
                CantidadPedida(item)
                ProcesarSinSerieSwitch(
                    isChecked = isSwitchChecked,
                    onCheckedChange = { isChecked ->
                        isSwitchChecked = isChecked // Actualizar estado del Switch
                    }
                )
                if (isSwitchChecked) {
                    Series() // Mostrar Series si el Switch está activado
                }
                if(!isSwitchChecked){
                    SeriesManual()
                }
                Separar()
                FooterProcesarSinCodigo(navController, item , correlativoOrigen, correlativo , username , area,
                    onSuccess = {
                        // Cuando el proceso sea exitoso, mostramos el mensaje
                        showExitoso = true
                    })
            }

        if (showExitoso) {
            AlertDialog(
                onDismissRequest = { showExitoso = false },
                title = {
                    Text(text = "¡Éxito!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                text = {
                    Text("El proceso se ha completado correctamente.", fontSize = 16.sp)
                },
                confirmButton = {
                    Button(onClick = { showExitoso = false }) {
                        Text("Aceptar")
                    }
                },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun TituloProcesoSinCodigo() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        Text(
            text = "DETALLE LECTURA ITEM",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center),
            color = Color(0xFF00909E)
        )

    }

}

@Composable
fun Item(item : PickingDetalleItem) {
    // Estado para el valor del texto
    val item = remember { mutableStateOf(TextFieldValue(item.item)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = item.value,
            onValueChange = { newValue -> item.value = newValue },
            label = { Text(
                "Item" , fontSize = 15.sp, color = Color(0xFF00909E), fontWeight = FontWeight.Bold) },
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
fun Descripcion(item : PickingDetalleItem) {

    // Estado para el valor del texto
    val descripcion = remember { mutableStateOf(TextFieldValue(item.Descripcion)) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        OutlinedTextField(
            value = descripcion.value,
            onValueChange = { newValue -> descripcion.value = newValue },
            label = { Text(
                "Descripcion" , fontSize = 15.sp, color = Color(0xFF00909E), fontWeight = FontWeight.Bold) },
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
            enabled = false, // Cambiar a true si deseas que sea editable
            maxLines = 5, // Permitir hasta 5 líneas (ajusta según tus necesidades)
            singleLine = false // Permite que el texto ocupe varias líneas
        )
    }
}

@Composable
fun CantidadPedida(item: PickingDetalleItem) {
    // Estado para el valor de los campos
    val cantidadPedida = remember { mutableStateOf(item.CantidadPedida.toString()) } // Convertimos a String
    val cantidadPikeada = remember { mutableStateOf("") } // Inicializamos vacío

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    ) {
        // Campo de texto para Cantidad Pedida
        OutlinedTextField(
            value = cantidadPedida.value.toString(),
            onValueChange = { newValue ->
                cantidadPedida.value =
                    (newValue.toIntOrNull() ?: cantidadPedida.value).toString() // Validar entrada
            },

            label = { Text(
                "Cantidad Pedida" , fontSize = 15.sp, color = Color(0xFF00909E), fontWeight = FontWeight.Bold) },
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
            enabled = false, // Cambiar a true si deseas que sea editable
            maxLines = 5, // Permitir hasta 5 líneas (ajusta según tus necesidades)
            singleLine = false // Permite que el texto ocupe varias líneas
        )

        // Campo de texto para Cantidad Pikeada
        OutlinedTextField(
            value = cantidadPikeada.value,
            onValueChange = { newValue ->
                // Validar entrada: permitir solo números o dejarlo vacío
                cantidadPikeada.value = newValue.filter { it.isDigit() }
            },
            label = {
                Text(
                    "Cantidad",
                    fontSize = 15.sp,
                    color = Color(0xFF00909E),
                    fontWeight = FontWeight.Bold
                )
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .height(60.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF00909E),
                unfocusedBorderColor = Color(0xFF00909E),
                cursorColor = Color(0xFF00909E)
            ),
            textStyle = TextStyle(
                color = GreenMakita,
                fontSize = 16.sp
            ),
            enabled = true, // Editable
            singleLine = true // Campo de una línea
        )
    }
}


@Composable
fun SeriesManual() {

    // Estado para el valor del texto
    val serieInicioManual = remember { mutableStateOf(0) }
    val SerieFinalManual = remember { mutableStateOf(0) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    )
    {
        OutlinedTextField(
            value = serieInicioManual.value.toString(),
            onValueChange = { newValue ->
                serieInicioManual.value = newValue.toIntOrNull() ?: serieInicioManual.value // Validar entrada
            },
            label = { Text(
                "Serie Inicio" , fontSize = 15.sp, color = Color(0xFF00909E), fontWeight = FontWeight.Bold) },
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
            enabled = false, // Cambiar a true si deseas que sea editable
            maxLines = 5, // Permitir hasta 5 líneas (ajusta según tus necesidades)
            singleLine = false // Permite que el texto ocupe varias líneas
        )

        OutlinedTextField(
            value = SerieFinalManual.value.toString(),
            onValueChange = { newValue ->
                SerieFinalManual.value = newValue.toIntOrNull() ?: SerieFinalManual.value // Validar entrada y mantener el valor anterior si no es un número
            },
            label = { Text(
                "Serie Final" , fontSize = 15.sp, color = Color(0xFF00909E), fontWeight = FontWeight.Bold) },
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
            enabled = false, // Cambiar a true si deseas que sea editable
            maxLines = 5, // Permitir hasta 5 líneas (ajusta según tus necesidades)
            singleLine = false // Permite que el texto ocupe varias líneas
        )
    }
}

@Composable
fun Series() {
    val series = remember { mutableStateOf(TextFieldValue("Sin Serie")) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp), // Añadir padding al Row
        horizontalArrangement = Arrangement.SpaceBetween // Espacio entre elementos
    )  {
        OutlinedTextField(
            value = series.value,
            onValueChange = { newValue -> series.value = newValue },
            label = { Text("Series", fontSize = 15.sp, color = Color(0xFF00909E),fontWeight = FontWeight.Bold) },
            modifier = Modifier
                .weight(1f) // El campo de texto ocupa el espacio disponible
                .padding(end = 8.dp)
                .height(80.dp),
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
            enabled = false, // Cambiar a true si deseas que sea editable
            maxLines = 5, // Permite varias líneas
            singleLine = false
        )
    }
}

@Composable
fun FooterProcesarSinCodigo(
    navController : NavController ,
    item: PickingDetalleItem,
    correlativoOrigen : Int ,
    correlativo : Int,
    username : String,
    area : String,
    onSuccess: () -> Unit){

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp), // Margen alrededor del Row
        horizontalArrangement = Arrangement.SpaceEvenly // Espacio uniforme entre los botones
    ) {
        Button(
            onClick = {

                escribirArchivoSinSerie(
                    navController,
                    item,
                    correlativoOrigen,
                    correlativo,
                    username ,
                    area,
                    onSuccess = { setShowDialog(true) }
                )
              // navController.popBackStack()

            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
            contentPadding = PaddingValues(8.dp),

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
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E)),
            contentPadding = PaddingValues(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically // Centra el texto y el ícono verticalmente
            ) {
                Icon(Icons.Default.Cancel, contentDescription = "Cancelar", tint = Color.White)
                Text(
                    text = "Cancelar",
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp) // Espaciado a la izquierda del texto
                )
            }
        }
    }
}
@Composable
fun ProcesarSinSerieSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF00909E),
                uncheckedThumbColor = Color(0xFFD9534F),
                checkedTrackColor = Color(0xFFBCE4E6),
                uncheckedTrackColor = Color(0xFFFFD2D2)
            ),
            modifier = Modifier.padding(end = 20.dp)
        )
        Text(
            text =  if (isChecked) "Procesar Sin Serie" else "Procesar Con Serie",
            color = if (isChecked)  Color(0xFF00909E)  else  Color(0xFFD9534F) ,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,

        )


    }
}


fun escribirArchivoSinSerie(
    navController: NavController,
    item: PickingDetalleItem,
    correlativoOrigen: Int,
    correlativo: Int,
    username: String,
    area: String,
    onSuccess: () -> Unit
) {
    val fechaHoraActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    val nombreArchivo = "picking_data_capturados.txt"
    val archivo = File("/data/data/com.makita.ubiapp/files", nombreArchivo)


    // Verificar si el archivo existe, si no, crearlo
    if (!archivo.exists()) {
        try {
            archivo.createNewFile()
            Log.d("*MAKITA*", "Archivo creado: $nombreArchivo")
        } catch (e: Exception) {
            Log.e("*MAKITA*", "Error al crear el archivo: ${e.message}")
            return
        }
    }

    // Generar la línea que se debe agregar
    val linea = "Makita;${item.TipoDocumento};${correlativo};${item.linea};${item.Tipoitem};" +
            "${item.item};${item.Descripcion};${item.Unidad};${item.Ubicacion};1;0;" +
            "SIN_SERIE;${correlativoOrigen};${username};$fechaHoraActual"

    // Usar repeat para agregar la línea tantas veces como la cantidad
    val cantidad = item.CantidadPedida ?: 1 // Evitar null en cantidad
    Log.d("*MAKITA*", "Cantidad a procesar: $cantidad") // Verifica la cantidad

    val lineas = mutableListOf<String>()

    repeat(cantidad) { i ->
        lineas.add(linea)  // Agregar la misma línea tantas veces como la cantidad
        Log.d("*MAKITA*", "Línea $i agregada")
    }

    // Escribir las líneas en el archivo
    try {
        if (lineas.isNotEmpty()) {
            archivo.appendText(lineas.joinToString("\n") + "\n")
            Log.d("*MAKITA*", "Líneas escritas correctamente:\n${lineas.joinToString("\n")}")
        } else {
            Log.e("*MAKITA*", "No hay líneas para escribir")
        }
    } catch (e: Exception) {
        Log.e("*MAKITA*", "Error al escribir en el archivo: ${e.message}")
    }

    onSuccess()

    navController.popBackStack()

}

@Composable
fun ShowSuccessDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Éxito") },
            text = { Text("Se procesó sin serie exitosamente.") },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Aceptar")
                }
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun ProcesarSinCodigoScreenView() {
    val exampleItem = PickingDetalleItem(
        Descripcion = "1",
        item = "Doc123",
        CantidadPedida = 1,
        Cantidad = 1,
        linea = 1,
        serie = "serie213",
        Unidad = "UN",
        Tipoitem = "01-HERRAMIENTAS",
        Ubicacion = "VCDDSF",
        TipoDocumento = "TipoDocumento"
    )

    val navController = rememberNavController()
    val correlativo = 123
    val correlativoOrigen =123
    val username = "ombre"
    val area = "Heerami"
    ProcesarSinCodigoBarraScreen(navController, exampleItem , correlativo, correlativoOrigen, username, area)
}
