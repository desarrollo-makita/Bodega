package com.makita.ubiapp.ui.component.etiquetas


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.core.content.ContextCompat


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.makita.ubiapp.ItemResponse
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.component.capturaSerie.Separar
import com.makita.ubiapp.ui.component.ubicaciones.formatTimestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtiquetasScreen(modifier: Modifier = Modifier)
{

    var text by remember { mutableStateOf("") }

    var extractedText by remember { mutableStateOf("") }
    var extractedText2 by remember { mutableStateOf("") }
    var extractedText3 by remember { mutableStateOf("") }
    var extractedText4 by remember { mutableStateOf("") }
    var textFieldValue2 by remember { mutableStateOf("") }
    var response by rememberSaveable { mutableStateOf<List<ItemResponse>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") } // Estado para manejar posibles errores
    var errorState by rememberSaveable { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    var bitmap by remember  { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val timestamp = formatTimestampEti(System.currentTimeMillis())
    val focusRequester = remember { FocusRequester() }
    val apiService = RetrofitClient.apiService
    val printerMacAddress = "8c:d5:4a:16:6d:92"
    val scope = rememberCoroutineScope()
    var selectedDevice: BluetoothDevice? by remember { mutableStateOf(null) }
    var pdfPath by remember { mutableStateOf<String?>(null) }
    val REQUEST_CODE_BLUETOOTH = 1
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("*MAKITA*", "Permiso de ubicación concedido")
        } else {
            Toast.makeText(context, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
        }
    }

    var showErrorDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    var CodigoChileNN by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    // val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val (printers, setPrinters) = remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
    /*  val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
      val (printers, setPrinters) = remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
      var showDialog by remember { mutableStateOf(false) }
      var selectedPrinterName by remember { mutableStateOf("") }
      var bitmap2 by remember  { mutableStateOf<Bitmap?>(null) }
      var CodigoChileNN by remember { mutableStateOf("") }

  */
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
            .padding(10.dp)
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White, shape = RoundedCornerShape(20.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Impresion Etiqueta Cargador, Makita",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00909E)
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(
                text = "$timestamp ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00909E)
            )

            Separar()

            Text(
                text = "Código PDF417",
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color(0xFF00909E)
            )
            LaunchedEffect(Unit) {
                // Solicita el foco en el primer TextField
                focusRequester.requestFocus()
            }

            TextField(
                value = text,
                label = { Text("Codigo Item Fabrica") },
                readOnly = false,
                onValueChange = { newText ->
                    text = newText

                    if (newText.length >= 20) {
                        extractedText = newText.substring(0, 20) // Primeros 20 caracteres //item
                        extractedText2 = newText.substring(20, newText.length.coerceAtMost(29))//serie_desde
                        extractedText3 = newText.substring(29, newText.length.coerceAtMost(38))//serie_hasta
                        extractedText4 = newText.substring(39, newText.length.coerceAtMost(52))//ean

                    } else {
                        extractedText =
                            newText // Si hay menos de 20 caracteres, se toma todo el texto en el primer campo
                        extractedText2 = "" // No hay texto para el segundo campo
                        extractedText3 = "" // No hay texto para el segundo campo
                        extractedText4 = ""
                    }

                },

                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .padding(bottom = 14.dp),

                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Text
                )
            )

            Spacer(modifier = Modifier.height(10.dp))
            // Texto "Modelo"
            Text(
                text = "Item",
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color(0xFF00909E)

            )


            // Segundo TextField: donde se muestran los primeros caracteres
            TextField(
                value = extractedText,
                onValueChange = { /* No se permite la edición */ },
                label = { Text("00 - 20") },
                readOnly = true, // Este campo es solo de lectura
                modifier = Modifier
                    .width(250.dp) // Definir ancho
                    .height(70.dp),
                textStyle = TextStyle(
                    fontSize = 18.sp, // Tamaño del texto
                    color = Color.Red, // Color del texto
                    fontFamily = FontFamily.Serif, // Familia de fuentes
                    fontWeight = FontWeight.Bold // Peso de la fuente
                ),
                enabled = false

            )

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Serie Desde",
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color(0xFF00909E)
            )

            TextField(
                value = extractedText2,
                onValueChange = { /* No se permite la edición */ },
                label = { Text("21 - 28") },
                readOnly = true, // Este campo es solo de lectura
                modifier = Modifier
                    .width(250.dp)
                    .height(70.dp),
                textStyle = TextStyle(
                    fontSize = 18.sp, // Tamaño del texto
                    color = Color.Black, // Color del texto
                    fontFamily = FontFamily.Serif, // Familia de fuentes
                    fontWeight = FontWeight.Bold // Peso de la fuente
                ),
                enabled = false
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Serie Hasta",
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color(0xFF00909E),
            )

            TextField(
                value = extractedText3,
                onValueChange = { /* No se permite la edición */ },
                label = { Text("29 - 38") },
                readOnly = true, // Este campo es solo de lectura
                modifier = Modifier
                    .width(250.dp) // Definir ancho
                    .height(70.dp),

                textStyle = TextStyle(
                    fontSize = 18.sp, // Tamaño del texto
                    color = Color.Black, // Color del texto
                    fontFamily = FontFamily.Serif, // Familia de fuentes
                    fontWeight = FontWeight.Bold // Peso de la fuente

                ),
                enabled = false
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Código EAN",
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 8.dp),
                color = Color(0xFF00909E),
            )


            TextField(
                value = extractedText4,
                onValueChange = { /* No se permite la edición */ },
                label = { Text("40 al 52") },
                readOnly = true, // Este campo es solo de lectura
                modifier = Modifier
                    .width(250.dp)
                    .height(70.dp),
                textStyle = TextStyle(
                    fontSize = 18.sp, // Tamaño del texto
                    color = Color.Black, // Color del texto
                    fontFamily = FontFamily.Serif, // Familia de fuentes
                    fontWeight = FontWeight.Bold // Peso de la fuente
                ),
                enabled = false
            )
            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = textFieldValue2,
                onValueChange = { textFieldValue2 = it },
                modifier = Modifier
                    .width(250.dp)
                    .height(70.dp),
                label = { Text("Codigo Cargador Comercial Chile") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                textStyle = TextStyle(
                    fontSize = 25.sp, // Tamaño del texto
                    color = Color.Red // Color del texto
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Red, // Color del texto enfocado
                    unfocusedTextColor = Color.Red, // Color del texto no enfocado
                    focusedIndicatorColor = Color.Blue, // Color del borde cuando está enfocado
                    unfocusedIndicatorColor = Color.Gray, // Color del borde cuando no está enfocado
                    cursorColor = Color.Red, // Color del cursor
                    focusedLabelColor = Color.Blue, // Color de la etiqueta cuando está enfocado
                    unfocusedLabelColor = Color.Gray // Color de la etiqueta cuando no está enfocado
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }



            LaunchedEffect(extractedText)
            {
                if (!extractedText.isNullOrEmpty()) {
                    Log.d("*MAKITA*", "NOESVACIOELTEXTO $extractedText")
                    try {

                        if (isNetworkAvailable(context)) {
                            // Inicia una corrutina
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val apiResponse = apiService.obtenerHerramienta(extractedText)
                                    withContext(Dispatchers.Main) {
                                        // Maneja la respuesta en el hilo principal
                                        Toast.makeText(context, "Respuesta obtenida correctamente", Toast.LENGTH_SHORT).show()
                                        // Haz algo con la respuesta, por ejemplo, actualizar UI
                                        response = apiResponse
                                        if (apiResponse.isNullOrEmpty()) {
                                            // No es error , no se encuentra definido en tabla HerramientasCargador
                                            Log.d("*MAKITA*", "ES XX EMPTY: ${apiResponse}")
                                            errorState = " No se encontraron datos para el item proporcionado"
                                            mostrarDialogo(
                                                context,
                                                "Advertencia",
                                                "Item sin Cargador Definido (Consulte a Comex)"
                                            )
                                        }

                                        //Log.d("*MAKITA*", "Tamaño de la respuesta: ${response}")

                                        errorState = null


                                        val tieneValoresNulos = apiResponse.any { it.item == null }

                                        if (tieneValoresNulos) {
                                            Log.d("*MAKITA*", "La respuesta contiene valores nulos")

                                            errorMessage = "Advertencia Item sin Cargador Definido"
                                            showErrorDialog = true
                                            text = ""
                                            extractedText = ""
                                            extractedText2 = ""
                                            extractedText3 = ""
                                            extractedText4 = ""
                                            textFieldValue2 = ""
                                            response = emptyList()
                                            focusRequester.requestFocus()

                                        } else
                                        {
                                            // Procesar la respuesta si no hay valores nulos
                                            response = apiResponse

                                            Toast.makeText(context, "Item con Cargador Definido $response", Toast.LENGTH_LONG).show()

                                        }

                                        println(apiResponse)
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        // Manejo de errores
                                        Toast.makeText(context, "Error al obtener los datos desde el servidor, revise wifi MCL-Bodega: ${e.message}", Toast.LENGTH_LONG).show()
                                        errorMessage = "Error al obtener los datos: ${e.message}"
                                        showErrorDialog = true
                                        text = ""
                                        extractedText = ""
                                        extractedText2 = ""
                                        extractedText3 = ""
                                        extractedText4 = ""
                                        textFieldValue2 = ""
                                        response = emptyList()
                                        focusRequester.requestFocus()

                                    }
                                }
                            }
                        } else {
                            // Muestra un mensaje indicando que no hay conexión
                            Toast.makeText(context, "No hay conexión a Internet", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {

                        // errorState =
                        if (e.message?.contains("404") == true) {
                            mostrarDialogo(context, "Error", "No se encontraron datos para el item")
                        } else {
                            "Error al obtener cargador: ${e.message}"
                        }
                        e.printStackTrace()

                        Log.e("*MAKITA*", "Error al obtener datos 1 : ${e.message}")
                        // Manejar error de la API+
                        mostrarDialogo(context, "Error", "Error al obtener datos 2: ${e.message}")

                    }
                }

                else {

                    Toast.makeText(context, "Capture Herramienta", Toast.LENGTH_SHORT).show()
                }
            }

            if (response.isNotEmpty()) {
                Text(
                    text = "Resultados de la API:",
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Itera sobre la lista de respuesta
                response.forEach { item ->
                    Text(

                        text = "Item: ${item.item}, Descripción: ${item.descripcion}, Código Chile 1: ${item.CodigoChile1}",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                    textFieldValue2 = item.CodigoChile1.padStart(10, '0')
                    CodigoChileNN = item.CodigoChile1.trim()
                }

                // Si hay un error, mostrar el mensaje de error
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = "Error: $errorMessage",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Spacer(modifier = Modifier.height(16.dp))  // Añade espacio entre el botón y la imagen

            Column(
                modifier = Modifier.fillMaxSize(),  // Ajusta la columna a todo el espacio disponible
                horizontalAlignment = Alignment.CenterHorizontally,  // Centra los elementos horizontalmente
                verticalArrangement = Arrangement.Center  // Centra los elementos verticalmente
            ) {
                Button(
                    onClick =
                    {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                (context as Activity),
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                100
                            )
                        }
                        else
                        {
                            showDialog = true
                            //SE DEBE DESCOMENTAR PAR APROBAR
                            //startBluetoothDiscovery(context, bluetoothAdapter, setPrinters)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0xFF00909E),
                        contentColor = Color.White   // Color del texto
                    )

                )
                {
                    Text("Seleccionar Impresora Bluetooth")
                }

                Spacer(modifier = Modifier.height(16.dp))  // Añade espacio entre el botón y la imagen
                Button(
                    onClick = {
                        text = ""
                        extractedText = ""
                        extractedText2 = ""
                        extractedText3 = ""
                        extractedText4 = ""
                        textFieldValue2 = ""
                        response = emptyList()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color(0xFF00909E),
                        contentColor = Color.White   // Color del texto
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Limpiar Datos")
                }
            }

        }
    }
}

fun formatTimestampEti(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("America/Santiago") // Zona horaria de Chile
    return formatter.format(date)
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = connectivityManager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnected
}
fun mostrarDialogo(context: Context, titulo: String, mensaje: String) {
    val builder = AlertDialog.Builder(context)
    builder.setTitle(titulo)
    builder.setMessage(mensaje)
    builder.setPositiveButton("OK", null)
    builder.show()
}

fun startBluetoothDiscovery(
    context: Context,
    bluetoothAdapter: BluetoothAdapter?,
    setDevices: (List<BluetoothDevice>) -> Unit
): BroadcastReceiver? {
    if (bluetoothAdapter == null) {
        Log.e("BluetoothDiscovery", "El adaptador Bluetooth es nulo.")
        return null
    }

    // Lista para almacenar dispositivos encontrados
    val foundDevices = mutableListOf<BluetoothDevice>()

    // Receptor para dispositivos encontrados
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice? = intent?.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                device?.let {
                    // Manejo seguro del nombre del dispositivo
                    val deviceName: String = if (ActivityCompat.checkSelfPermission(
                            context!!,
                            Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        it.name ?: "" // Obtener el nombre si hay permiso
                    } else {
                        Log.w("BluetoothDiscovery", "Permiso BLUETOOTH_CONNECT no otorgado. Usando nombre vacío.")
                        "" // Nombre vacío si no hay permiso
                    }
                    Log.e("*MAKITA*", "isZebraPrinter. $deviceName")
                    // Filtrar impresoras Zebra
                    if (isZebraPrinter(deviceName) && !foundDevices.contains(it))
                    // if (  !foundDevices.contains(it))
                    {
                        Log.e("*MAKITA*", "isZebraPrinter. $deviceName")
                        foundDevices.add(it)
                        setDevices(foundDevices)
                    }
                }
            }
        }
    }

    // Registrar el receptor para detectar dispositivos Bluetooth
    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)

    context.registerReceiver(receiver, filter)

    // Manejo explícito de permisos
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
        != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e("BluetoothDiscovery", "Permisos insuficientes para escaneo Bluetooth.")
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.BLUETOOTH_SCAN),
            1001
        )
        return null
    }

    try {
        if (!bluetoothAdapter.startDiscovery()) {
            Log.e("BluetoothDiscovery", "No se pudo iniciar el descubrimiento Bluetooth.")
            context.unregisterReceiver(receiver)
            return null
        }
    } catch (e: SecurityException) {
        Log.e("BluetoothDiscovery", "Error al iniciar el descubrimiento: ${e.message}")
        context.unregisterReceiver(receiver)
        return null
    }

    return receiver
}

/**
 * Verifica si el dispositivo es una impresora Zebra.
 */
private fun isZebraPrinter(deviceName: String): Boolean {
    return deviceName.contains("Zebra", ignoreCase = true) ||
            deviceName.startsWith("ZQ", ignoreCase = true)
}



@Preview(showBackground = true)
@Composable
fun EtiquetasScreenView() {
    EtiquetasScreen()
}