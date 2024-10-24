package com.makita.ubiapp.ui.dialogs

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.makita.ubiapp.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign

import com.makita.ubiapp.ActividadItem


@Composable
fun MenuScreen(
    nombreUsuario: String,
    area: String,
    vigencia: Long,
    idUsuario: Int,
    token: String,
    actividades: List<ActividadItem>,
    navController: NavController
) {

    Log.d("*MAKITA*" , "MenuScreen Iniciando composable MenuScreen")

    Log.d("*MAKITA*" , "MenuScreen- inicio(nombreUsuario :) $nombreUsuario")
    Log.d("*MAKITA*" , "MenuScreen- inicio(area :) $area|")
    Log.d("*MAKITA*" , "MenuScreen- inicio(vigencia :) $vigencia")
    Log.d("*MAKITA*" , "MenuScreen- inicio(idUsuario :) $idUsuario")
    Log.d("*MAKITA*" , "MenuScreen- inicio(token :) $token")
    Log.d("*MAKITA*" , "MenuScreen- inicio(actividades:) $actividades")

    val scrollState = rememberScrollState()
    var showVigenciaDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vigencia) {
        if (vigencia <= 90) {
            showVigenciaDialog = true
        }
    }

    if (showVigenciaDialog) {
        VigenciaDialog(
            vigencia = vigencia,
            idUsuario = idUsuario,
            nombreUsuario = nombreUsuario,
            token= token,
            onDismiss = { showVigenciaDialog = false },
        )
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
            .padding(16.dp)
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
            MenuHeader(nombreUsuario = nombreUsuario, area = area)
            MenuOptions(navController = navController, nombreUsuario = nombreUsuario , actividades = actividades)
        }
    }
}

@Composable
fun MenuHeader(nombreUsuario: String, area: String) {
    Image(
        painter = painterResource(id = R.drawable.makitafondoblanco),
        contentDescription = "Logo de Makita",
        modifier = Modifier
            .size(120.dp)
            .padding(top = 15.dp)
            .background(Color(0xFF80CBC4), CircleShape)
    )

    Text(
        text = "Bienvenido: $nombreUsuario",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF004D40),
        modifier = Modifier.padding(top = 12.dp)
    )

    Text(
        text = "Área: $area",
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF00796B),
        modifier = Modifier.padding(top = 4.dp)
    )

    Divider(
        color = Color(0xFF004D40),
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 16.dp)
    )
}

@Composable
fun MenuOptions(navController: NavController, nombreUsuario: String , actividades: List<ActividadItem>) {
    Text(
        text = "¿Qué deseas hacer?",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF004D40),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Registramos la lista de actividades para verificar
        Log.d("*MAKITA", "Actividades: $actividades")

        // Usamos la lista de actividades para generar los elementos de menú
        actividades.forEach { actividad ->
            Log.d("*MAKITA", "actividad: ${actividad.ruta}")
            MenuItem(
                icon = getIconForActividad(actividad.nombreActividad), // Usamos la función de mapeo
                text = actividad.nombreActividad, // Usamos el nombre de la actividad
                onClick = {
                    navController.navigate("${actividad.ruta}${nombreUsuario}") // O alguna acción relacionada
                }
            )
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp))
            .background(Color(0xFFE0F2F1), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier
                .size(48.dp)
                .padding(8.dp),
            tint = Color(0xFF00796B)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color(0xFF004D40),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

fun getIconForActividad(actividad: String): ImageVector {
    return when (actividad) {
        "Ubicacion" -> Icons.Default.Place
        "Inventario" -> Icons.Default.List
        "Almacenamiento" -> Icons.Default.Dataset
        "Etiquetado" -> Icons.Default.Label
        else -> Icons.Default.Help // Icono por defecto si no se encuentra una coincidencia
    }
}