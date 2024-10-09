package com.makita.ubiapp.ui.util

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.makita.ubiapp.CambioClaveRequest
import com.makita.ubiapp.Data
import com.makita.ubiapp.DataDispositivo
import com.makita.ubiapp.DataDispositivoRequest
import com.makita.ubiapp.ReplaceClaveRequest
import com.makita.ubiapp.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DeviceInfoUtil {

    // Función para obtener la información del dispositivo
    fun obtenerInformacionDispositivo(context: Context): Map<String, String> {
        val informacion = mutableMapOf<String, String>()

        // Obtener información del dispositivo
        informacion["fabricante"] = "${Build.MANUFACTURER}"
        informacion["modelo"] = "${Build.MODEL}"
        informacion["sistema_operativo"] = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"
        informacion["numero_serie"] = Build.SERIAL
        informacion["id_android"] = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

        return informacion
    }

    // Función para registrar la información del dispositivo
    fun registrarInformacionDispositivo(context: Context, nombreUsuario: String) {
        // Llamar a la función que obtiene la información del dispositivo
        val informacionDispositivo = obtenerInformacionDispositivo(context)

        // Obtener la información desde el Map
        val fabricante = informacionDispositivo["fabricante"]
        val modelo = informacionDispositivo["modelo"]
        val sistemaOperativo = informacionDispositivo["sistema_operativo"]
        val numeroSerie = informacionDispositivo["numero_serie"]
        val idAndroid = informacionDispositivo["id_android"]

        // Imprimir todas las variables en el Log
        Log.d("*MAKITA*", """
            Información del dispositivo:
            Usuario: $nombreUsuario
            Modelo: $modelo
            Fabricante: $fabricante
            Sistema Operativo: $sistemaOperativo
            Número de Serie: $numeroSerie
            ID Android: $idAndroid
        """.trimIndent())

        // Llamada a la API con Retrofit para registrar la información del dispositivo
        val dataDispositivo = DataDispositivo(
            usuario = nombreUsuario,
            modelo = modelo ?: "",
            fabricante = fabricante ?: "",
            sistemaOperativo = sistemaOperativo ?: "",
            numeroSerie = numeroSerie ?: "",
            idAndroid = idAndroid ?: ""
        )

        val request = DataDispositivoRequest(dataDispositivo = dataDispositivo)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.insertarInfoDspositivo(request)
                if (response.isSuccessful) {
                    Log.d("*MAKITA*", "Dispositivo registrado con éxito")
                } else {
                    Log.e("*MAKITA*", "Error en la respuesta de la API: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("*MAKITA*", "Error al registrar dispositivo: ${e.message}")
            }
        }

    }



    //val response = RetrofitClient.apiService.insertarInfoDspositivo(cambioClaveRequest)
}
