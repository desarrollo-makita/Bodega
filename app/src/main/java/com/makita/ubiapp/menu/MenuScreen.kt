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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.Storage
import androidx.navigation.NavController
import com.makita.ubiapp.R

@Composable
fun MenuScreen(nombreUsuario: String, area: String,  navController: NavController) {

    // Column principal que ocupa toda la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(Color.White, shape = RoundedCornerShape(10.dp)),
        verticalArrangement = Arrangement.Top, // Ajustar la disposición vertical
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Imagen en la parte superior
        Image(
            painter = painterResource(id = R.drawable.makitafondoblanco),
            contentDescription = "Logo de Makita",
            modifier = Modifier
                .size(150.dp)
                .padding(top = 24.dp) // Ajustar el espaciado si es necesario
        )

        // Eliminar el espaciado entre la imagen y el texto de bienvenida
        // Ahora el texto de bienvenida sigue directamente a la imagen
        Text(
            text = "Bienvenido: $nombreUsuario",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        // Espaciado entre el texto de bienvenida y la línea
        Spacer(modifier = Modifier.height(16.dp))

        // Texto para "Área"
        Text(
            text = "Área: $area",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
        // Línea horizontal para separar el contenido
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Espaciado entre el Divider y el título de los iconos
        Spacer(modifier = Modifier.height(16.dp))

        // Título para los iconos
        Text(
            text = "¿Qué deseas hacer?",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00909E),

        )

        // Espaciado entre el título y los iconos
        Spacer(modifier = Modifier.height(16.dp))

        // Sección de iconos
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clickable { navController.navigate("ubicacion/$nombreUsuario") }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Icono de ubicación",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    tint = Color(0xFF00909E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cambio Ubicación",
                    fontSize = 20.sp,
                    color = Color(0xFF00909E),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            // Espaciado entre los iconos
            Spacer(modifier = Modifier.height(16.dp))

            // Icono de Inventario
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = "Icono de inventario",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    tint = Color(0xFF00909E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Inventario",
                    fontSize = 20.sp,
                    color = Color(0xFF00909E),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            // Icono de Almacenamiento (Bodega)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Icono de almacenamiento",
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color(0xFFE0F7FA), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    tint = Color(0xFF00909E)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Almacenamiento",
                    fontSize = 20.sp,
                    color = Color(0xFF00909E),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}
