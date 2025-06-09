package com.example.prueba20.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.Period
import com.example.prueba20.data.UserData

/**
 * Barra superior que muestra el nombre y edad del usuario con un botón para cerrar sesión.
 * @param user Datos del usuario (nombre, email, fecha de nacimiento).
 * @param onLogout Callback al pulsar "Cerrar sesión".
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHeader(
    user: UserData,
    onLogout: () -> Unit
) {
    val age = remember(user.birthDate) {
        val birth = LocalDate.parse(user.birthDate)
        Period.between(birth, LocalDate.now()).years
    }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Usuario",
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Edad: $age", style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        actions = {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cerrar sesión", color = Color.White)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
    )
}