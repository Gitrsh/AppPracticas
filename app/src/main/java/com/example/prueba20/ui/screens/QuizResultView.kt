package com.example.prueba20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.prueba20.ui.components.RadarChartView
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.PrimaryButton
import com.example.prueba20.ui.theme.SecondaryButton
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun QuizResultView(
    average: Int,
    responses: Map<Int, Int>,
    navController: NavController,
    tipoTest: String
) {
    val resultado = when (average) {
        in 0..20 -> ResultadoNivel("Necesitas mejorar", "Tu nivel de actividad física es muy bajo. Te recomendamos comenzar con pequeñas rutinas diarias y aumentar gradualmente.", MaterialTheme.colorScheme.error, "😔", listOf(Color(0xFFFF5252), Color(0xFFFF867F)))
        in 21..40 -> ResultadoNivel("Puedes mejorar", "Estás comenzando, pero hay mucho margen para mejorar. Intenta incorporar más actividad física en tu rutina.", Color(0xFFFF9800), "😐", listOf(Color(0xFFFF9800), Color(0xFFFFC46B)))
        in 41..50 -> ResultadoNivel("Estás en el camino", "Vas por buen camino, pero aún puedes mejorar. Sigue esforzándote y verás los resultados.", Color(0xFFFFC107), "🙂", listOf(Color(0xFFFFC107), Color(0xFFFFEB3B)))
        in 51..75 -> ResultadoNivel("¡Buen trabajo!", "Mantienes un buen nivel de actividad física. Sigue así y considera nuevos desafíos.", Color(0xFF4CAF50), "😊", listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)))
        in 76..90 -> ResultadoNivel("¡Excelente!", "Tu nivel de actividad física es muy bueno. Eres un ejemplo a seguir para otros.", Color(0xFF2196F3), "😍", listOf(Color(0xFF2196F3), Color(0xFF64B5F6)))
        else -> ResultadoNivel("¡Increíble!", "Eres un atleta. Tu nivel de actividad física es excepcional. ¡Sigue inspirando a otros!", Color(0xFF9C27B0), "🤩", listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)))
    }

    val (title, message, _, emoji, gradientColors) = resultado
    var valoresGlobales by remember { mutableStateOf<List<Int>?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(AppTheme.Spacing.lg)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Brush.verticalGradient(colors = gradientColors)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(emoji, style = MaterialTheme.typography.displayLarge)
                Text("$average%", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
                Text(title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(12.dp),
            shadowElevation = AppTheme.Elevation.md,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Detalle de tus respuestas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                val titulos = listOf("Motivación", "Confianza", "Competencia", "Comprensión")
                responses.values.forEachIndexed { index, value ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Prg. ${index + 1}. ${titulos.getOrElse(index) { "Sin título" }}")
                        Text(
                            "$value%",
                            fontWeight = FontWeight.Bold,
                            color = when {
                                value < 30 -> MaterialTheme.colorScheme.error
                                value < 70 -> Color(0xFFFF9800)
                                else -> Color(0xFF4CAF50)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Representación gráfica del test",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        RadarChartView(
            valores = responses.values.toList(),
            valoresComparacion = valoresGlobales
        )

        Spacer(modifier = Modifier.height(24.dp))

        SecondaryButton(
            text = if (valoresGlobales == null) "Comparar gráfica" else "Ocultar comparación",
            onClick = {
                if (valoresGlobales != null) {
                    valoresGlobales = null
                } else {
                    FirebaseFirestore.getInstance().collection(tipoTest)
                        .get()
                        .addOnSuccessListener { result ->
                            val listas = result.mapNotNull {
                                it.get("respuestas") as? List<Long>
                            }.filter { it.size == 4 }

                            if (listas.isNotEmpty()) {
                                val promedios = (0..3).map { i ->
                                    listas.map { it[i] }.average().toInt()
                                }
                                valoresGlobales = promedios
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Elegir otro test",
            onClick = {
                navController.navigate("quiz_selection") {
                    popUpTo("quiz") { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Analizar tests",
            onClick = {
                navController.navigate("comparison/$tipoTest")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}