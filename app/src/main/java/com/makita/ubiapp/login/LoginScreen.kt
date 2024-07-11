package com.makita.ubiapp.login

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makita.ubiapp.R
import com.makita.ubiapp.database.AppDatabase
import com.makita.ubiapp.entity.LoginEntity
import com.makita.ubiapp.ui.theme.GreenMakita
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLoginSuccess: (String, String) -> Unit) {
var username by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var errorState by remember { mutableStateOf<String?>(null) }
    var isUsernameFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appVersion = getAppVersion(context)
    val db = AppDatabase.getDatabase(context)
    val loginDao = db.loginDao()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.makitafondoblanco),
            contentDescription = "Logo de Makita",
            modifier = Modifier
                .size(150.dp)
                .padding(top = 24.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = {
                Text(
                    text = "Usuario",
                    style = TextStyle(
                        fontSize = 18.sp ,
                        fontWeight = if (isUsernameFocused) FontWeight.Bold else FontWeight.Normal,
                    )
                )
            },
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    isUsernameFocused = it.isFocused
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = GreenMakita,
                cursorColor = GreenMakita,
            )
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = {
                Text(
                    text = "Password",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = if (isPasswordFocused) FontWeight.Bold else FontWeight.Normal,
                    )
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier
                .width(350.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .onFocusChanged {
                    isPasswordFocused = it.isFocused
                }
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenMakita,
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = GreenMakita,
                cursorColor = GreenMakita,
            )
        )

        errorState?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        LaunchedEffect(errorState) {
            if (errorState != null) {
                delay(2000)
                errorState = null
            }
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val isValid = validateLogin(username.text, password.text)
                    if (isValid) {
                        val loginEntity = LoginEntity(
                            username = username.text,
                            password = password.text,
                            loginTime = System.currentTimeMillis()
                        )
                        loginDao.insertLogin(loginEntity)
                        onLoginSuccess(username.text, password.text)
                    } else {
                        errorState = "Usuario y/o Password incorrectos."
                        username = TextFieldValue()
                        password = TextFieldValue()
                    }
                }
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .width(150.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00909E)  // GreenMakita
            )
        ) {
            Text("Ingresar")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = "Versión $appVersion",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}

// Función para validar el login (simulada)
private fun validateLogin(username: String, password: String): Boolean {
    return username == "admin" && password == "admin123"
}

fun getAppVersion(context: Context): String {
    return try {
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        "${pInfo.versionName} (${pInfo.versionCode})"
    } catch (e: PackageManager.NameNotFoundException) {
        "N/A"
    }
}




