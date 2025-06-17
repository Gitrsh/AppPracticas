package com.example.prueba20.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prueba20.ui.theme.AppTopBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.prueba20.data.model.ResultadoNivel
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch

@Composable
fun ComparisonScreen(navController: NavController, tipoTest: String) {
    var mostrarGraficoGlobal by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    var mediasGlobales by remember { mutableStateOf<Map<String, Float>>(emptyMap()) }

    LaunchedEffect(Unit) {
        scope.launch {
            mediasGlobales = obtenerMediasGlobales()
        }
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Pantalla de Comparación",
                navigationIcon = Icons.Default.ArrowBack,
                navigationAction = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            RadarChartTestsSection(
                comparacionEntries = if (mostrarGraficoGlobal) getGlobalTestRadarEntries(mediasGlobales) else null
            )

            Button(
                onClick = { mostrarGraficoGlobal = !mostrarGraficoGlobal },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (mostrarGraficoGlobal) "Ocultar gráfico global" else "Mostrar gráfico global")
            }

            val titulosPreguntas = listOf("Motivación", "Confianza", "Competencia", "Comprensión")

            titulosPreguntas.forEachIndexed { index, titulo ->
                ComparisonCard(
                    preguntaTitulo = "Pregunta ${index + 1}: $titulo",
                    preguntaIndex = index,
                    tipoTest = tipoTest
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonCard(preguntaTitulo: String, preguntaIndex: Int, tipoTest: String) {
    var selectedCategory by remember { mutableStateOf("Global") }
    var puntuacionUsuario by remember { mutableStateOf<Float?>(null) }
    var mediana by remember { mutableStateOf<Float?>(null) }
    var totalUsuarios by remember { mutableStateOf(0) }
    var respuestasGlobal by remember { mutableStateOf<List<Float>>(emptyList()) }
    var grupoUsuario by remember { mutableStateOf<String?>(null) }

    val resultadoNivel = remember(puntuacionUsuario) {
        puntuacionUsuario?.let { calcularResultadoComparacion(it.toInt()) }
    }

    val porcentajeSuperado = remember(puntuacionUsuario, respuestasGlobal) {
        if (puntuacionUsuario != null && respuestasGlobal.isNotEmpty()) {
            val total = respuestasGlobal.size
            val menores = respuestasGlobal.count { it < puntuacionUsuario!! }
            (menores * 100f / total).toInt()
        } else null
    }

    LaunchedEffect(Unit) {
        val emailActual = FirebaseAuth.getInstance().currentUser?.email?.lowercase()
        val db = FirebaseFirestore.getInstance()

        emailActual?.let { email ->
            val userDoc = db.collection("usuarios").document(email).get().await()
            grupoUsuario = userDoc.getString("grupo")
        }

        val (media, puntuacion, total, respuestas) = obtenerDatosComparacion(
            tipoTest, preguntaIndex, selectedCategory, grupoUsuario
        )
        mediana = media
        puntuacionUsuario = puntuacion
        totalUsuarios = total
        respuestasGlobal = respuestas
    }

    LaunchedEffect(selectedCategory) {
        val (media, puntuacion, total, respuestas) = obtenerDatosComparacion(
            tipoTest, preguntaIndex, selectedCategory, grupoUsuario
        )
        mediana = media
        puntuacionUsuario = puntuacion
        totalUsuarios = total
        respuestasGlobal = respuestas
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(preguntaTitulo, style = MaterialTheme.typography.titleMedium)

            Column(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Global", "País").forEach { label ->
                        FilterChip(
                            selected = selectedCategory == label,
                            onClick = { selectedCategory = label },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Edad", "Grupo").forEach { label ->
                        FilterChip(
                            selected = selectedCategory == label,
                            onClick = { selectedCategory = label },
                            label = { Text(label) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Slider(
                    value = (puntuacionUsuario ?: 0f) / 100f,
                    onValueChange = {},
                    valueRange = 0f..1f,
                    steps = 9,
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    colors = resultadoNivel?.color?.let {
                        SliderDefaults.colors(
                            thumbColor = it,
                            activeTrackColor = it,
                            inactiveTrackColor = it.copy(alpha = 0.3f)
                        )
                    } ?: SliderDefaults.colors()
                )
                Text(
                    "Tu puntuación: ${puntuacionUsuario?.toString() ?: "-"} | Mediana: ${mediana?.toString() ?: "-"}",
                    fontSize = 14.sp,
                    color = resultadoNivel?.color ?: Color.Unspecified
                )
                Text("Has superado al ${porcentajeSuperado ?: "-"}% de los estudiantes", fontSize = 12.sp, color = Color.Gray)

                if (puntuacionUsuario != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    DistribucionGrafica(puntuacionUsuario!!)

                    if (porcentajeSuperado != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(12.dp)
                        ) {
                            Text("¿Cómo interpretar esta gráfica?", style = MaterialTheme.typography.titleMedium)

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Bandas de color:", fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            listOf(
                                Pair("Superior (P85+): Rendimiento excepcional", Color(0xFFFFCDD2)),
                                Pair("Alto (P50-P85): Por encima del promedio", Color(0xFFFFF9C4)),
                                Pair("Medio (P15-P50): Dentro del rango típico", Color(0xFFC8E6C9)),
                                Pair("Bajo (-P15): Área de mejora potencial", Color(0xFFD7CCC8))
                            ).forEach { (desc, color) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(color, shape = RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(desc, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Tu posición:", fontSize = 14.sp)
                            Text("• Tu puntuación (${puntuacionUsuario}) está en el percentil $porcentajeSuperado")
                            Text("• Esto significa que superas al $porcentajeSuperado% de los estudiantes")

                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Comparación:", fontSize = 14.sp)
                            Text("• Total de estudiantes: $totalUsuarios")
                            Text("• Mediana del grupo: ${mediana ?: "-"}")
                        }
                    }
                }
            }
        }
    }
}

data class ComparisonStats(
    val mediana: Float?,
    val puntuacionUsuario: Float?,
    val totalUsuarios: Int,
    val respuestas: List<Float>
)

suspend fun obtenerDatosComparacion(
    tipoTest: String,
    preguntaIndex: Int,
    filtro: String,
    grupoUsuario: String?
): ComparisonStats {
    val db = FirebaseFirestore.getInstance()
    val emailActual = FirebaseAuth.getInstance().currentUser?.email?.lowercase()
    val snapshot = db.collection(tipoTest).get().await()

    val respuestas = mutableListOf<Float>()
    var puntuacionUsuario: Float? = null

    for (doc in snapshot.documents) {
        val emailDoc = doc.getString("email")?.lowercase() ?: continue

        if (filtro == "Grupo" && grupoUsuario != null) {
            val usuarioDoc = db.collection("usuarios").document(emailDoc).get().await()
            val grupoDeUsuario = usuarioDoc.getString("grupo")
            if (grupoDeUsuario != grupoUsuario) continue
        }

        val lista = doc.get("respuestas") as? List<*> ?: continue
        val valor = lista.getOrNull(preguntaIndex) as? Number ?: continue
        val valorFloat = valor.toFloat()
        respuestas.add(valorFloat)

        if (emailDoc == emailActual) {
            puntuacionUsuario = valorFloat
        }
    }

    val mediana = respuestas.sorted().let {
        if (it.isEmpty()) null
        else if (it.size % 2 == 1) it[it.size / 2]
        else (it[it.size / 2 - 1] + it[it.size / 2]) / 2
    }

    return ComparisonStats(
        mediana = mediana,
        puntuacionUsuario = puntuacionUsuario,
        totalUsuarios = respuestas.size,
        respuestas = respuestas
    )
}


fun calcularResultadoComparacion(promedio: Int): ResultadoNivel = when (promedio) {
    in 0..20 -> ResultadoNivel("Necesitas mejorar", "Nivel bajo.", Color(0xFFFF5252), "😔", listOf())
    in 21..40 -> ResultadoNivel("Puedes mejorar", "Margen de mejora.", Color(0xFFFF9800), "😐", listOf())
    in 41..60 -> ResultadoNivel("Vas bien", "Progreso adecuado.", Color(0xFFFFC107), "🙂", listOf())
    in 61..80 -> ResultadoNivel("¡Muy bien!", "Por encima de la media.", Color(0xFF4CAF50), "😊", listOf())
    in 81..100 -> ResultadoNivel("¡Excelente!", "Rendimiento sobresaliente.", Color(0xFF2196F3), "😍", listOf())
    else -> ResultadoNivel("Desconocido", "Sin datos.", Color.Gray, "❓", listOf())
}

@Composable
fun DistribucionGrafica(puntuacion: Float) {
    val alturaTotal = 300.dp
    val marcadorAlturaPx = with(LocalDensity.current) { alturaTotal.toPx() }

    val puntuacionClamped = puntuacion.coerceIn(0f, 100f)
    val offsetProporcion = (1f - (puntuacionClamped / 100f))
    val offsetYPx = offsetProporcion * marcadorAlturaPx

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(alturaTotal)
    ) {
        // Columna con etiquetas P95-P15
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("P95", "P85", "P50", "P15").forEach {
                Text(it, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        // Columna con bandas + marcador
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(0.1f)
                        .fillMaxWidth()
                        .background(Color(0xFFFFCDD2).copy(alpha = 0.4f)) // P95+
                )
                Box(
                    modifier = Modifier
                        .weight(0.25f)
                        .fillMaxWidth()
                        .background(Color(0xFFFFF9C4).copy(alpha = 0.4f)) // P85–P50
                )
                Box(
                    modifier = Modifier
                        .weight(0.35f)
                        .fillMaxWidth()
                        .background(Color(0xFFC8E6C9).copy(alpha = 0.4f)) // P50–P15
                )
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxWidth()
                        .background(Color(0xFFD7CCC8).copy(alpha = 0.4f)) // –P15
                )
            }

            // Marcador del usuario
            Box(
                modifier = Modifier
                    .offset { IntOffset(0, offsetYPx.toInt()) }
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(50))
                    .border(1.dp, Color.Gray, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "%.1f".format(puntuacion), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Icono usuario",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

fun getGlobalTestRadarEntries(medias: Map<String, Float>): List<TestRadarEntry> {
    val labels = listOf("Amigos", "Familia", "Centro")
    val tipos = listOf("amigos", "familia", "centro_educativo")

    return tipos.mapIndexed { i, tipo ->
        val valor = medias[tipo]
        if (valor != null) {
            TestRadarEntry(labels[i], valor)
        } else {
            TestRadarEntry(labels[i], 0f)
        }
    }
}

suspend fun obtenerMediasGlobales(): Map<String, Float> {
    val db = FirebaseFirestore.getInstance()
    val colecciones = listOf("amigos", "familia", "centro_educativo")
    val medias = mutableMapOf<String, Float>()

    for (coleccion in colecciones) {
        val snapshot = db.collection(coleccion).get().await()
        val respuestasTotales = snapshot.documents
            .mapNotNull { it.get("respuestas") as? List<*> }
            .mapNotNull { respuestas ->
                val promedio = respuestas
                    .mapNotNull { it as? Number }
                    .map { it.toFloat() }
                    .average()
                if (promedio.isNaN()) null else promedio.toFloat()
            }

        if (respuestasTotales.isNotEmpty()) {
            medias[coleccion] = respuestasTotales.average().toFloat()
        } else {
            medias[coleccion] = 0f
        }
    }

    return medias
}