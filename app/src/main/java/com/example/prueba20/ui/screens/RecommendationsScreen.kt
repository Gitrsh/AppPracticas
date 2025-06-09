package com.example.prueba20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.prueba20.ui.theme.AppTopBar

@Composable
fun RecommendationsScreen(average: Int, onBack: () -> Unit) {
    val recommendations = getRecommendations(average)

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Recomendaciones",
                navigationIcon = Icons.Default.ArrowBack,
                navigationAction = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Basado en tu resultado, te sugerimos:",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            recommendations.forEachIndexed { index, rec ->
                Text(
                    "${index + 1}. $rec",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

fun getRecommendations(average: Int): List<String> {
    return when (average) {
        in 0..20 -> listOf(
            "Camina al menos 15 minutos al día.",
            "Evita el uso excesivo del automóvil para trayectos cortos."
        )
        in 21..40 -> listOf(
            "Haz una rutina de ejercicios suaves 3 veces por semana.",
            "Opta por escaleras en vez del ascensor."
        )
        in 41..50 -> listOf(
            "Agrega 10 minutos más de actividad física diaria.",
            "Haz pausas activas durante tu jornada laboral."
        )
        in 51..75 -> listOf(
            "Prueba una nueva actividad como ciclismo o natación.",
            "Únete a un grupo de caminatas o clases de ejercicio."
        )
        in 76..90 -> listOf(
            "Participa en retos deportivos mensuales.",
            "Comparte tu progreso con amigos para motivarlos."
        )
        else -> listOf(
            "Considera entrenar para un evento deportivo.",
            "Ayuda a otros a empezar su camino hacia un estilo de vida activo."
        )
    }
}
