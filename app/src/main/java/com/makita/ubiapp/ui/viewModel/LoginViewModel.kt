package com.makita.ubiapp.ui.component.login

import android.os.Build
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.makita.ubiapp.LoginRequest
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.UserData
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var nombreUsuario = mutableStateOf("")
    var clave = mutableStateOf("")
    var errorState = mutableStateOf<String?>(null)
    var isUsernameFocused = mutableStateOf(false)
    var isPasswordFocused = mutableStateOf(false)
    val dispositivo = "${Build.MANUFACTURER} ${Build.MODEL}"
    val sistemaOperativo = "Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})"



    fun login(onLoginSuccess: (String, String, Long, Int ,String , Int) -> Unit) {
        viewModelScope.launch {
            try {
                val request = LoginRequest(nombreUsuario.value, clave.value)
                val response = RetrofitClient.apiService.login(request)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        val userData = loginResponse.data
                        Log.d("*MAKITA*", "NombreUsuario ${userData.NombreUsuario}")
                        Log.d("*MAKITA*", "Area ${userData.Area}")
                        Log.d("*MAKITA*", "vigencia ${userData.vigencia}")
                        Log.d("*MAKITA*", "UsuarioID ${userData.UsuarioID}")
                        Log.d("*MAKITA*", "token ${userData.token}")
                        Log.d("*MAKITA*", "recuperarClave ${userData.recuperarClave}")
                        if (userData != null) {

                            onLoginSuccess(userData.NombreUsuario, userData.Area, userData.vigencia, userData.UsuarioID , userData.token , userData.recuperarClave)
                        } else {
                            errorState.value = "Datos de usuario vacíos"
                        }
                    } ?: run {
                        errorState.value = "Respuesta vacía del servidor"
                    }
                } else {
                    errorState.value = "Nombre de usuario o contraseña incorrectos. Por favor, inténtalo de nuevo."
                }
            } catch (e: Exception) {
                errorState.value = "Error de conexión: ${e.message}"
            }
        }
    }
}
