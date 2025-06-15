package com.example.prueba20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba20.data.model.ResultadoNivel
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.PrimaryButton
import com.example.prueba20.util.QuizSessionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaResultScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Resumen de Resultados") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(24.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Detalle de respuestas por test",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            val comparacionesGlobales = remember { mutableStateMapOf<Int, List<Int>?>() }

            QuizSessionState.respuestasPorTest.forEachIndexed { index, respuestas ->
                val promedio = respuestas.average().toInt()
                val resultado = calcularResultado(promedio)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Brush.verticalGradient(colors = resultado.coloresGradiente)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(resultado.emoji, style = MaterialTheme.typography.displayLarge)
                        Text("$promedio%", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                        Text(resultado.titulo, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    }
                }

                Text(
                    text = resultado.mensaje,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(AppTheme.Spacing.md)) {
                        Text(
                            text = QuizSessionState.nombresTest.getOrNull(index) ?: "Test ${index + 1}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        respuestas.forEachIndexed { i, respuesta ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Pregunta ${i + 1}")
                                Text(text = "$respuesta%", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Media total
            val medias = QuizSessionState.resultadosPorTipo.values
            val mediaGlobal = if (medias.isNotEmpty()) medias.sum() / medias.size else 0
            val resultadoGlobal = calcularResultado(mediaGlobal)

            Text(
                "Media total de todos los tests",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(colors = resultadoGlobal.coloresGradiente)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(resultadoGlobal.emoji, style = MaterialTheme.typography.displayLarge)
                    Text("$mediaGlobal%", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                    Text(resultadoGlobal.titulo, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
            }

            Text(
                text = resultadoGlobal.mensaje,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Volver a inicio",
                onClick = {
                    navController.navigate("home") {
                        popUpTo("quiz_selection") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )
        }
    }
}

fun calcularResultado(promedio: Int): ResultadoNivel = when (promedio) {
    in 0..20 -> ResultadoNivel("Necesitas mejorar", "Tu nivel de actividad física es muy bajo. Te recomendamos comenzar con pequeñas rutinas diarias y aumentar gradualmente.", Color(0xFFFF5252), "😔", listOf(Color(0xFFFF5252), Color(0xFFFF867F)))
    in 21..40 -> ResultadoNivel("Puedes mejorar", "Estás comenzando, pero hay mucho margen para mejorar. Intenta incorporar más actividad física en tu rutina.", Color(0xFFFF9800), "😐", listOf(Color(0xFFFF9800), Color(0xFFFFC46B)))
    in 41..50 -> ResultadoNivel("Estás en el camino", "Vas por buen camino, pero aún puedes mejorar. Sigue esforzándote y verás los resultados.", Color(0xFFFFC107), "🙂", listOf(Color(0xFFFFC107), Color(0xFFFFEB3B)))
    in 51..75 -> ResultadoNivel("¡Buen trabajo!", "Mantienes un buen nivel de actividad física. Sigue así y considera nuevos desafíos.", Color(0xFF4CAF50), "😊", listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)))
    in 76..90 -> ResultadoNivel("¡Excelente!", "Tu nivel de actividad física es muy bueno. Eres un ejemplo a seguir para otros.", Color(0xFF2196F3), "😍", listOf(Color(0xFF2196F3), Color(0xFF64B5F6)))
    else -> ResultadoNivel("¡Increíble!", "Eres un atleta. Tu nivel de actividad física es excepcional. ¡Sigue inspirando a otros!", Color(0xFF9C27B0), "🤩", listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)))
}