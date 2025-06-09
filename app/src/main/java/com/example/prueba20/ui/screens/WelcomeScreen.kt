package com.example.prueba20.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prueba20.R
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.ScreenWithBackground

/**
 * Pantalla de bienvenida con botones para ir a login o registro.
 * @param onNavigateToLogin Callback al presionar "Iniciar sesión".
 * @param onNavigateToRegister Callback al presionar "Registrarse".
 */
@Composable
fun WelcomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    ScreenWithBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppTheme.Spacing.xl),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Alfabetización Física",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppTheme.Spacing.sm))

            Text(
                "Descubre tu nivel de actividad física",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(AppTheme.Spacing.xl))

            Image(
                painter = painterResource(R.drawable.icono),
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(AppTheme.Spacing.xl))

            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = AppTheme.Shapes.xl,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = AppTheme.Elevation.md,
                    pressedElevation = AppTheme.Elevation.lg
                )
            ) {
                Text("Iniciar sesión", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(AppTheme.Spacing.md))

            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = AppTheme.Shapes.xl,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Registrarse", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}