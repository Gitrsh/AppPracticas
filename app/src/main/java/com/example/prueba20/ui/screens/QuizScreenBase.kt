package com.example.prueba20.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba20.data.FirebaseRepository
import com.example.prueba20.ui.theme.*
import com.example.prueba20.util.QuizSessionState
import com.example.prueba20.viewmodel.UserViewModel
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreenBase(
    questions: List<String>,
    userViewModel: UserViewModel,
    navController: NavController,
    tipoTest: String,
    onBack: () -> Unit = {}
) {
    if (questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No hay preguntas disponibles")
        }
        return
    }

    val context = LocalContext.current
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
        val scrollState = rememberScrollState()

        val columnModifier = if (!quizFinished) {
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.Spacing.lg)
                .verticalScroll(scrollState)
        } else {
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.Spacing.lg)
        }

        Column(
            modifier = columnModifier,
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
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = AppTheme.Elevation.sm)
                ) {
                    Column(modifier = Modifier.padding(AppTheme.Spacing.lg)) {
                        val resourceName = "${tipoTest.lowercase()}_${currentQuestionIndex + 1}"
                        val imageResId = remember(resourceName) {
                            context.resources.getIdentifier(resourceName, "drawable", context.packageName)
                        }

                        if (imageResId != 0) {
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                            )
                            Spacer(modifier = Modifier.height(AppTheme.Spacing.md))
                        }

                        Text(
                            text = currentQuestion,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(bottom = AppTheme.Spacing.md)
                        )

                        val sliderColor = numericAnswer?.let {
                            when (it.roundToInt()) {
                                in 0..30 -> MaterialTheme.colorScheme.error
                                in 40..70 -> Color(0xFFFF9800)
                                in 80..100 -> Color(0xFF4CAF50)
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        } ?: MaterialTheme.colorScheme.onSurfaceVariant

                        val emoji = numericAnswer?.let {
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
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
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
                                        val average = if (numericResponses.isNotEmpty())
                                            numericResponses.sum() / numericResponses.size else 0

                                        Log.d("QuizSession", "Guardando media: $average")
                                        Log.d("QuizSession", "Respuestas: $numericResponses")

                                        QuizSessionState.resultadosPorTipo[tipoTest] = average
                                        QuizSessionState.respuestasPorTest.add(numericResponses)

                                        val email = user?.email ?: "anonimo@desconocido"
                                        FirebaseRepository.guardarRespuestas(
                                            email = email,
                                            respuestas = numericResponses,
                                            tipoTest = tipoTest,
                                            onSuccess = { Log.d("Firestore", "Guardado exitosamente") },
                                            onError = { e -> Log.e("Firestore", "Error al guardar", e) }
                                        )
                                        quizFinished = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                QuizResultView(
                    average = QuizSessionState.resultadosPorTipo[tipoTest] ?: 0,
                    responses = responses,
                    navController = navController,
                    tipoTest = tipoTest
                )
            }
        }
    }
}