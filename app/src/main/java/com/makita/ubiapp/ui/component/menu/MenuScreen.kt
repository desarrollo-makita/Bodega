import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.makita.ubiapp.R

@Composable
fun MenuScreen(nombreUsuario: String, area: String, navController: NavController) {
    // Estado de desplazamiento vertical
    val scrollState = rememberScrollState()

    // Scaffold para manejar el diseño general
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .verticalScroll(scrollState), // Habilita el desplazamiento vertical
        verticalArrangement = Arrangement.Top, // Ajustar la disposición vertical
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen en la parte superior
        Image(
            painter = painterResource(id = R.drawable.makitafondoblanco),
            contentDescription = "Logo de Makita",
            modifier = Modifier
                .size(150.dp)
                .padding(top = 15.dp)
        )

        // Texto de bienvenida
        Text(
            text = "Bienvenido: $nombreUsuario",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Texto para "Área"
        Text(
            text = "Área: $area",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Título para los iconos
        Text(
            text = "¿Qué deseas hacer?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00909E)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sección de iconos
        Column(modifier = Modifier.fillMaxWidth()) {
            // Cambiar Ubicación
            MenuItem(
                icon = Icons.Default.LocationOn,
                text = "Cambio Ubicación",
                onClick = { navController.navigate("ubicacion/$nombreUsuario") }
            )

            // Inventario
            MenuItem(
                icon = Icons.Default.List,
                text = "Inventario",
                onClick = { /* Acción para Inventario */ }
            )

            // Almacenamiento
            MenuItem(
                icon = Icons.Default.Warning,
                text = "Almacenamiento",
                onClick = { /* Acción para Almacenamiento */ })
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
            .padding(vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            modifier = Modifier
                .size(50.dp)
                .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(8.dp))
                .padding(8.dp),
            tint = Color(0xFF00909E)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 20.sp,
            color = Color(0xFF00909E),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}
