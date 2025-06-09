package com.example.prueba20.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba20.data.FirebaseRepository
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.AppTopBar
import com.example.prueba20.ui.theme.PrimaryButton
import com.example.prueba20.ui.theme.SecondaryButton
import com.example.prueba20.viewmodel.UserViewModel
import kotlin.math.roundToInt

data class ResultLevel(
    val title: String,
    val message: String,
    val color: Color,
    val emoji: String,
    val gradientColors: List<Color>
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenBase(
    questions: List<String>,
    userViewModel: UserViewModel,
    navController: NavController,
    onBack: () -> Unit = {}
) {
    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay preguntas disponibles")
        }
        return
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var numericAnswer by remember { mutableStateOf(50f) }
    val responses = remember { mutableStateMapOf<Int, Int>() }
    var quizFinished by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentQuestionIndex) ?: ""

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Cuestionario",
                navigationIcon = Icons.Default.ArrowBack,
                navigationAction = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.Spacing.lg),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!quizFinished) {
                LinearProgressIndicator(
                    progress = (currentQuestionIndex + 1).toFloat() / questions.size,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primaryContainer
                )

                Spacer(modifier = Modifier.height(AppTheme.Spacing.md))

                Text(
                    "Pregunta ${currentQuestionIndex + 1} de ${questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Spacer(modifier = Modifier.height(AppTheme.Spacing.lg))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = AppTheme.Elevation.sm
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(AppTheme.Spacing.lg)
                    ) {
                        Text(
                            text = currentQuestion,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(bottom = AppTheme.Spacing.md)
                        )

                        val sliderColor: Color
                        val emoji: String

                        when (numericAnswer.roundToInt()) {
                            in 0..30 -> {
                                sliderColor = MaterialTheme.colorScheme.error
                                emoji = "\uD83D\uDE1E"
                            }
                            in 40..70 -> {
                                sliderColor = Color(0xFFFF9800)
                                emoji = "\uD83D\uDE10"
                            }
                            in 80..100 -> {
                                sliderColor = Color(0xFF4CAF50)
                                emoji = "\uD83D\uDE42"
                            }
                            else -> {
                                sliderColor = MaterialTheme.colorScheme.onSurfaceVariant
                                emoji = ""
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppTheme.Spacing.md)
                        ) {
                            Slider(
                                value = numericAnswer,
                                onValueChange = { numericAnswer = it },
                                valueRange = 0f..100f,
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = sliderColor,
                                    activeTrackColor = sliderColor,
                                    inactiveTrackColor = sliderColor.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                (0..100 step 10).forEach { value ->
                                    Text(
                                        text = "$value",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (numericAnswer.roundToInt() == value) sliderColor
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(AppTheme.Spacing.sm))

                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = sliderColor.copy(alpha = 0.1f),
                                        shape = MaterialTheme.shapes.medium
                                    )
                                    .padding(vertical = AppTheme.Spacing.sm)
                            ) {
                                Text(
                                    "Tu respuesta: ${numericAnswer.roundToInt()}% $emoji",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = sliderColor
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.Spacing.lg))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SecondaryButton(
                        text = "Anterior",
                        onClick = {
                            if (currentQuestionIndex > 0) currentQuestionIndex--
                        },
                        modifier = Modifier.weight(1f),
                        enabled = currentQuestionIndex > 0
                    )

                    Spacer(modifier = Modifier.width(AppTheme.Spacing.md))

                    PrimaryButton(
                        text = if (currentQuestionIndex == questions.lastIndex) "Finalizar" else "Siguiente",
                        onClick = {
                            responses[currentQuestionIndex] = numericAnswer.roundToInt()
                            numericAnswer = 50f

                            if (currentQuestionIndex < questions.lastIndex) {
                                currentQuestionIndex++
                            } else {
                                quizFinished = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                val numericResponses = responses.values.filterIsInstance<Int>().toList()
                val average = if (numericResponses.isNotEmpty()) numericResponses.sum() / numericResponses.size else 0

                val (title, message, color, emoji, gradientColors) = when (average) {
                    in 0..20 -> ResultLevel("Necesitas mejorar", "Tu nivel de actividad física es muy bajo. Te recomendamos comenzar con pequeñas rutinas diarias y aumentar gradualmente.", MaterialTheme.colorScheme.error, "\uD83D\uDE14", listOf(Color(0xFFFF5252), Color(0xFFFF867F)))
                    in 21..40 -> ResultLevel("Puedes mejorar", "Estás comenzando, pero hay mucho margen para mejorar. Intenta incorporar más actividad física en tu rutina.", Color(0xFFFF9800), "\uD83D\uDE10", listOf(Color(0xFFFF9800), Color(0xFFFFC46B)))
                    in 41..50 -> ResultLevel("Estás en el camino", "Vas por buen camino, pero aún puedes mejorar. Sigue esforzándote y verás los resultados.", Color(0xFFFFC107), "\uD83D\uDE42", listOf(Color(0xFFFFC107), Color(0xFFFFEB3B)))
                    in 51..75 -> ResultLevel("¡Buen trabajo!", "Mantienes un buen nivel de actividad física. Sigue así y considera nuevos desafíos.", Color(0xFF4CAF50), "\uD83D\uDE0A", listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)))
                    in 76..90 -> ResultLevel("¡Excelente!", "Tu nivel de actividad física es muy bueno. Eres un ejemplo a seguir para otros.", Color(0xFF2196F3), "\uD83D\uDE0D", listOf(Color(0xFF2196F3), Color(0xFF64B5F6)))
                    else -> ResultLevel("¡Increíble!", "Eres un atleta. Tu nivel de actividad física es excepcional. ¡Sigue inspirando a otros!", Color(0xFF9C27B0), "\uD83E\uDD29", listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)))
                }

                val user = userViewModel.user.collectAsState().value
                val email = user?.email ?: "anonimo@desconocido"

                LaunchedEffect(Unit) {
                    FirebaseRepository.guardarRespuestas(
                        email = email,
                        respuestas = numericResponses,
                        onSuccess = { Log.d("Firestore", "Guardado exitosamente") },
                        onError = { e -> Log.e("Firestore", "Error al guardar", e) }
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Resultado visual
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

                    // Detalles de respuestas
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
                            responses.values.forEachIndexed { index, value ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Pregunta ${index + 1}")
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

                    // Botón volver al inicio
                    PrimaryButton(
                        text = "Volver al inicio",
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("quiz") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // ✅ Botón de recomendaciones
                    SecondaryButton(
                        text = "Ver recomendaciones",
                        onClick = {
                            navController.navigate("recomendaciones/$average")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .height(50.dp)
                    )
                }
            }
        }
    }
}
