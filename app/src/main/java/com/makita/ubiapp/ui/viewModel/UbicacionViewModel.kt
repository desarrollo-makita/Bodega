package com.makita.ubiapp.ui.viewModel


import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.makita.ubiapp.ActualizaUbicacionRequest
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.UbicacionResponse
import com.makita.ubiapp.ui.component.database.AppDatabase
import com.makita.ubiapp.ui.component.entity.RegistraUbicacionEntity
import com.makita.ubiapp.ui.component.ubicaciones.formatTimestamp
import kotlinx.coroutines.launch

class UbicacionViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = RetrofitClient.apiService
    private val db = AppDatabase.getDatabase(application)
    private val registrarUbicacionDao = db.registrarUbicacion()

    var response = mutableStateOf<List<UbicacionResponse>>(emptyList())
    var errorState = mutableStateOf<String?>(null)
    var successMessage = mutableStateOf<String?>(null)
    var successMail = mutableStateOf<String?>(null)
    var registrosMessage = mutableStateOf<String?>(null)
    var clearRequested = mutableStateOf(false)
    var isTextFieldEnabled = mutableStateOf(true)
    var showTerminateButton = mutableStateOf(false)
    var showLimpiarButton = mutableStateOf(false)
    var showContinuarProcesoButton = mutableStateOf(false)

    fun fetchUbicaciones(item: String) {
        viewModelScope.launch {
            try {
                val ubicaciones = apiService.obtenerUbicacion(item)
                response.value = ubicaciones
                errorState.value = null
                successMessage.value = null

                if (ubicaciones.isEmpty()) {
                    errorState.value = "No se encontraron datos para el ítem proporcionado"
                }
            } catch (e: Exception) {
                errorState.value = if (e.message?.contains("404") == true) {
                    "No se encontraron datos para el ítem proporcionado"
                } else {
                    "Error al obtener ubicación: ${e.message}"
                }
            }
        }
    }

    fun updateUbicacion(
        nuevaUbicacion: String,
        item: String,
        tipoItem: String,
        username: String
    ) {
        viewModelScope.launch {
            try {
                val request = ActualizaUbicacionRequest(
                    nuevaUbicacion = nuevaUbicacion,
                    empresa = "Makita",
                    item = item,
                    tipoItem = tipoItem
                )
                apiService.actualizaUbicacion(request)

                val requestRegistro = RegistraUbicacionEntity(
                    username = username,
                    timestamp = formatTimestamp(System.currentTimeMillis()),
                    item = item,
                    ubicacionAntigua = response.value.firstOrNull()?.Ubicacion ?: "",
                    nuevaUbicacion = nuevaUbicacion,
                    tipoItem = tipoItem
                )

                registrarUbicacionDao.registraUbicacion(requestRegistro)

                successMessage.value = "Ubicación actualizada exitosamente"
                clearRequested.value = true
                showTerminateButton.value = true
                showLimpiarButton.value = true
                showContinuarProcesoButton.value = false
            } catch (e: Exception) {
                errorState.value = "Error al actualizar ubicación: ${e.message}"
            }
        }
    }

    fun clearState() {
        response.value = emptyList()
        errorState.value = null
        successMessage.value = null
        successMail.value = null
        showTerminateButton.value = false
        showLimpiarButton.value = false
        showContinuarProcesoButton.value = false
        registrosMessage.value = null
        clearRequested.value = false
    }
}

