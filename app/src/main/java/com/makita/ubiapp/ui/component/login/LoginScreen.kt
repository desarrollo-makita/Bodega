package com.makita.ubiapp.ui.component.login

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.LoginRequest
import com.makita.ubiapp.R
import com.makita.ubiapp.RetrofitClient
import com.makita.ubiapp.ui.component.database.AppDatabase
import com.makita.ubiapp.ui.component.entity.LoginEntity
import com.makita.ubiapp.ui.component.ubicaciones.formatTimestamp
import com.makita.ubiapp.ui.dialogs.PasswordRecoveryDialog
import com.makita.ubiapp.ui.theme.GreenMakita

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.makita.ubiapp.ui.component.dao.LoginDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun LoginScreen(onLoginSuccess: (String, String, Long) -> Unit) {
    val context = LocalContext.current
    val appVersion = getAppVersion(context)
    val db = AppDatabase.getDatabase(context)
    val loginDao = db.loginDao()
    val coroutineScope = rememberCoroutineScope()

    var nombreUsuario by remember { mutableStateOf(TextFieldValue()) }
    var clave by remember { mutableStateOf(TextFieldValue()) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showRecoveryDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LogoImage()
        UsernameField(value = nombreUsuario, onValueChange = { nombreUsuario = it })
        PasswordField(value = clave, onValueChange = { clave = it }, passwordVisible = passwordVisible, onVisibilityChange = { passwordVisible = !passwordVisible })
        ErrorMessage(errorState)
        Spacer(modifier = Modifier.height(25.dp))
        LoginButton { performLogin(nombreUsuario.text, clave.text, loginDao, coroutineScope, onLoginSuccess) }
        Spacer(modifier = Modifier.height(16.dp))
        PasswordRecoveryLink(onClick = { showRecoveryDialog = true })
        Spacer(modifier = Modifier.height(100.dp))
        VersionInfo(appVersion)
    }

    if (showRecoveryDialog) {
        PasswordRecoveryDialog(onDismiss = { showRecoveryDialog = false })
    }
}

@Composable
fun LogoImage() {
    Image(
        painter = painterResource(id = R.drawable.makitafondoblanco),
        contentDescription = "Logo de Makita",
        modifier = Modifier
            .size(150.dp)
            .padding(top = 24.dp)
    )
}

@Composable
fun UsernameField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Usuario", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenMakita)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .onFocusChanged { /* handle focus change if needed */ },
        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenMakita,
            unfocusedBorderColor = GreenMakita,
            focusedLabelColor = GreenMakita,
            cursorColor = GreenMakita,
        )
    )
}

@Composable
fun PasswordField(value: TextFieldValue, onValueChange: (TextFieldValue) -> Unit, passwordVisible: Boolean, onVisibilityChange: () -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Password", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenMakita)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp)),
        textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
        singleLine = true,
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = onVisibilityChange) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GreenMakita,
            unfocusedBorderColor = GreenMakita,
            focusedLabelColor = GreenMakita,
            cursorColor = GreenMakita,
        )
    )
}

@Composable
fun ErrorMessage(errorState: String?) {
    errorState?.let { error ->
        Text(
            text = error,
            color = Color.Red,
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun LoginButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .width(150.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00909E))
    ) {
        Text("Ingresar")
    }
}

@Composable
fun PasswordRecoveryLink(onClick: () -> Unit) {
    Text(
        text = "¿Olvidaste tu contraseña?",
        color = GreenMakita,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun VersionInfo(appVersion: String) {
    Text(
        text = "Versión $appVersion",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(8.dp)
    )
}

fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        "${pInfo.versionName} (${pInfo.versionCode})"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A"
    }
}

fun performLogin(nombreUsuario: String, clave: String, loginDao: LoginDao, coroutineScope: CoroutineScope, onLoginSuccess: (String, String, Long) -> Unit) {
    coroutineScope.launch {
        try {
            val request = LoginRequest(nombreUsuario, clave)
            val response = RetrofitClient.apiService.login(request)
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    val userData = loginResponse.data
                    if (userData != null) {
                        val loginEntity = LoginEntity(
                            username = nombreUsuario,
                            password = clave,
                            loginTime = formatTimestamp(System.currentTimeMillis())
                        )
                        loginDao.insertLogin(loginEntity)
                        val fechaFin = userData.FechaFin
                        val formatter = DateTimeFormatter.ISO_DATE_TIME
                        val fechaFinDate = LocalDate.parse(fechaFin, formatter)
                        val fechaActual = LocalDate.now()
                        val vigencia = ChronoUnit.DAYS.between(fechaActual, fechaFinDate)
                        onLoginSuccess(userData.NombreUsuario, userData.Area, vigencia)
                    } else {
                        Log.e("*MAKITA*", "El campo 'data' en la respuesta es nulo")
                    }
                } ?: run {
                    Log.e("*MAKITA*", "Respuesta vacía del servidor")
                }
            } else {
                Log.e("*MAKITA*", "Nombre de usuario o contraseña incorrectos")
            }
        } catch (e: Exception) {
            Log.e("*MAKITA*", "Error fetching logins: ${e.message}")
        }
    }
}
