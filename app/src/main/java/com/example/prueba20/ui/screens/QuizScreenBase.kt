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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba20.data.FirebaseRepository
import com.example.prueba20.data.model.ResultadoNivel
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.AppTopBar
import com.example.prueba20.ui.theme.PrimaryButton
import com.example.prueba20.ui.theme.SecondaryButton
import com.example.prueba20.viewmodel.UserViewModel
import kotlin.math.roundToInt
import com.example.prueba20.util.QuizSessionState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenBase(
    questions: List<String>,
    userViewModel: UserViewModel,
    navController: NavController,
    tipoTest: String, // parametro nuevo para guardar cada test en su correspondiente colección
    onBack: () -> Unit = {}
) {
    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay preguntas disponibles")
        }
        return
    }

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var numericAnswer by remember { mutableStateOf<Float?>(null) }
    val responses = remember { mutableStateMapOf<Int, Int>() }
    var quizFinished by remember { mutableStateOf(false) }
    var hasInteracted by remember { mutableStateOf(false) }
    val user by userViewModel.user.collectAsState()

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
                    Column(modifier = Modifier.padding(AppTheme.Spacing.lg)) {
                        Text(
                            text = currentQuestion,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(bottom = AppTheme.Spacing.md)
                        )

                        val sliderColor: Color = numericAnswer?.let {
                            when (it.roundToInt()) {
                                in 0..30 -> MaterialTheme.colorScheme.error
                                in 40..70 -> Color(0xFFFF9800)
                                in 80..100 -> Color(0xFF4CAF50)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        } ?: MaterialTheme.colorScheme.onSurfaceVariant

                        val emoji: String = numericAnswer?.let {
                            when (it.roundToInt()) {
                                in 0..30 -> "😞"
                                in 40..70 -> "😐"
                                in 80..100 -> "🙂"
                                else -> ""
                            }
                        } ?: ""

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppTheme.Spacing.md)
                        ) {
                            Slider(
                                value = numericAnswer ?: 0f,
                                onValueChange = {
                                    numericAnswer = it
                                    hasInteracted = true
                                },
                                valueRange = 0f..100f,
                                steps = 9,
                                colors = SliderDefaults.colors(
                                    thumbColor = if (hasInteracted) sliderColor else Color.Transparent,
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
                                        color = if (numericAnswer?.roundToInt() == value) sliderColor
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
                                if (!hasInteracted) {
                                    Text(
                                        "Selecciona un valor para continuar",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    )
                                } else {
                                    Text(
                                        "Tu respuesta: ${numericAnswer!!.roundToInt()}% $emoji",
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = sliderColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppTheme.Spacing.lg))

                val isEnabled = hasInteracted && numericAnswer != null

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SecondaryButton(
                        text = "Anterior",
                        onClick = {
                            if (currentQuestionIndex > 0) currentQuestionIndex--
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(AppTheme.Spacing.md))

                    Box(modifier = Modifier.weight(1f).alpha(if (isEnabled) 1f else 0.5f)) {
                        PrimaryButton(
                            text = if (currentQuestionIndex == questions.lastIndex) "Finalizar" else "Siguiente",
                            onClick = {
                                if (isEnabled) {
                                    responses[currentQuestionIndex] = numericAnswer!!.roundToInt()

                                    if (currentQuestionIndex < questions.lastIndex) {
                                        currentQuestionIndex++
                                        numericAnswer = null
                                        hasInteracted = false
                                    } else {
                                        val numericResponses = responses.map { it.value }
                                        val average = if (numericResponses.isNotEmpty()) numericResponses.sum() / numericResponses.size else 0

                                        Log.d("QuizSession", "Guardando media: $average")
                                        Log.d("QuizSession", "Respuestas: $numericResponses")

                                        QuizSessionState.sessionResults.add(average)
                                        QuizSessionState.respuestasPorTest.add(numericResponses)

                                        val email = user?.email ?: "anonimo@desconocido"
                                        FirebaseRepository.guardarRespuestas(
                                            email = email,
                                            respuestas = numericResponses,
                                            tipoTest = tipoTest, // ← parámetro añadido
                                            onSuccess = { Log.d("Firestore", "Guardado exitosamente") },
                                            onError = { e -> Log.e("Firestore", "Error al guardar", e) }
                                        )
                                        quizFinished = true
                                    }
                                }

                                        if (currentQuestionIndex == questions.lastIndex) {

                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                val lastAverage = QuizSessionState.sessionResults.lastOrNull() ?: 0

                val resultado = when (lastAverage) {
                    in 0..20 -> ResultadoNivel("Necesitas mejorar", "Tu nivel de actividad física es muy bajo. Te recomendamos comenzar con pequeñas rutinas diarias y aumentar gradualmente.", MaterialTheme.colorScheme.error, "😔", listOf(Color(0xFFFF5252), Color(0xFFFF867F)))
                    in 21..40 -> ResultadoNivel("Puedes mejorar", "Estás comenzando, pero hay mucho margen para mejorar. Intenta incorporar más actividad física en tu rutina.", Color(0xFFFF9800), "😐", listOf(Color(0xFFFF9800), Color(0xFFFFC46B)))
                    in 41..50 -> ResultadoNivel("Estás en el camino", "Vas por buen camino, pero aún puedes mejorar. Sigue esforzándote y verás los resultados.", Color(0xFFFFC107), "🙂", listOf(Color(0xFFFFC107), Color(0xFFFFEB3B)))
                    in 51..75 -> ResultadoNivel("¡Buen trabajo!", "Mantienes un buen nivel de actividad física. Sigue así y considera nuevos desafíos.", Color(0xFF4CAF50), "😊", listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)))
                    in 76..90 -> ResultadoNivel("¡Excelente!", "Tu nivel de actividad física es muy bueno. Eres un ejemplo a seguir para otros.", Color(0xFF2196F3), "😍", listOf(Color(0xFF2196F3), Color(0xFF64B5F6)))
                    else -> ResultadoNivel("¡Increíble!", "Eres un atleta. Tu nivel de actividad física es excepcional. ¡Sigue inspirando a otros!", Color(0xFF9C27B0), "🤩", listOf(Color(0xFF9C27B0), Color(0xFFBA68C8)))
                }

                val (title, message, color, emoji, gradientColors) = resultado

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
                            Text("$lastAverage%", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.ExtraBold), color = Color.White)
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

                    SecondaryButton(
                        text = "Ver media de todos los tests",
                        onClick = {
                            navController.navigate("media_result")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PrimaryButton(
                        text = "Comparar",
                        onClick = {
                            navController.navigate("comparison")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )
                }
            }
        }
    }
}
