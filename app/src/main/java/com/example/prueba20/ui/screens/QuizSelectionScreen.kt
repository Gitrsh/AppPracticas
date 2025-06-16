package com.example.prueba20.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba20.R

object SessionState {
    val sessionResults = mutableStateListOf<Int>()
    val sessionAnswers = mutableStateListOf<Pair<String, Int>>()
    val completedQuizzes = mutableStateListOf<String>()
}

fun String.inList(list: List<String>) = list.contains(this)

data class ResultadoNivel(
    val titulo: String,
    val mensaje: String,
    val color: Color,
    val emoji: String,
    val coloresGradiente: List<Color>
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizSelectionScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cuestionarios") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Selecciona una modalidad para evaluar tu actividad física",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            QuizOptionCard(
                iconRes = R.drawable.ic_familia,
                title = "Cuestionario en casa",
                description = "Evalúa tu nivel de actividad en el entorno familiar.",
                quizKey = "familia",
                enabled = !"familia".inList(SessionState.completedQuizzes),
                onClick = {
                    navController.navigate("quiz_screen/familia")
                    SessionState.completedQuizzes.add("familia")
                }
            )

            QuizOptionCard(
                iconRes = R.drawable.ic_amigos,
                title = "Cuestionario con amigos",
                description = "Descubre cómo influye tu círculo social en tu actividad física.",
                quizKey = "amigos",
                enabled = !"amigos".inList(SessionState.completedQuizzes),
                onClick = {
                    navController.navigate("quiz_screen/amigos")
                    SessionState.completedQuizzes.add("amigos")
                }
            )

            QuizOptionCard(
                iconRes = R.drawable.ic_colegio,
                title = "Cuestionario mi centro educativo",
                description = "Evalúa tu actividad física en el ámbito escolar.",
                quizKey = "centro_educativo",
                enabled = !"centro_educativo".inList(SessionState.completedQuizzes),
                onClick = {
                    navController.navigate("quiz_screen/centro_educativo")
                    SessionState.completedQuizzes.add("centro_educativo")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("home") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Volver a la pantalla de bienvenida")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizOptionCard(
    iconRes: Int,
    title: String,
    description: String,
    quizKey: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val cardAlpha = if (enabled) 1f else 0.5f

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            onClick = if (enabled) onClick else ({}),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .alpha(cardAlpha)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (!enabled) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = {
                    SessionState.completedQuizzes.remove(quizKey)
                },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 1f)
                ),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reactivar",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reactivar", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

fun refrescarTests(title: String): String = when (title) {
    "Cuestionario en casa" -> "familia"
    "Cuestionario con amigos" -> "amigos"
    "Cuestionario mi centro educativo" -> "centro_educativo"
    else -> ""
}

fun markQuizAsCompleted(route: String) {
    if (!SessionState.completedQuizzes.contains(route)) {
        SessionState.completedQuizzes.add(route)
    }
}
