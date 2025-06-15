package com.example.prueba20.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.prueba20.util.QuizSessionState
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.PI
import kotlin.math.sin

/** Modelo de entrada para cada punto del radar */
data class TestRadarEntry(val label: String, val value: Float)

/** Composable que dibuja una gráfica de radar con comparación opcional */
@Composable
fun RadarChartTests(
    entries: List<TestRadarEntry>,
    comparacionEntries: List<TestRadarEntry>? = null,
    maxValue: Float = 100f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.height(300.dp).fillMaxWidth()) {
        val sizeMin = min(size.width, size.height)
        val radius = sizeMin / 2.8f
        val center = Offset(size.width / 2, size.height / 2)
        val angleStep = 2 * PI / entries.size

        // Líneas radiales
        entries.forEachIndexed { i, _ ->
            val angle = angleStep * i - PI / 2
            val x = center.x + radius * cos(angle).toFloat()
            val y = center.y + radius * sin(angle).toFloat()
            drawLine(Color.LightGray, center, Offset(x, y), strokeWidth = 2f)
        }

        // Polígono del usuario
        val userPoints = entries.mapIndexed { i, entry ->
            val angle = angleStep * i - PI / 2
            val r = (entry.value / maxValue) * radius
            Offset(
                center.x + r * cos(angle).toFloat(),
                center.y + r * sin(angle).toFloat()
            )
        }

        drawPath(
            path = Path().apply {
                moveTo(userPoints.first().x, userPoints.first().y)
                userPoints.drop(1).forEach { lineTo(it.x, it.y) }
                close()
            },
            color = Color(0xFF2196F3).copy(alpha = 0.4f)
        )

        drawPath(
            path = Path().apply {
                moveTo(userPoints.first().x, userPoints.first().y)
                userPoints.drop(1).forEach { lineTo(it.x, it.y) }
                close()
            },
            color = Color(0xFF2196F3),
            style = Stroke(width = 3f)
        )

        // Polígono de comparación si existe
        comparacionEntries?.let {
            val comparacionPoints = it.mapIndexed { i, entry ->
                val angle = angleStep * i - PI / 2
                val r = (entry.value / maxValue) * radius
                Offset(
                    center.x + r * cos(angle).toFloat(),
                    center.y + r * sin(angle).toFloat()
                )
            }

            drawPath(
                path = Path().apply {
                    moveTo(comparacionPoints.first().x, comparacionPoints.first().y)
                    comparacionPoints.drop(1).forEach { lineTo(it.x, it.y) }
                    close()
                },
                color = Color(0xFF8BC34A).copy(alpha = 0.3f)
            )

            drawPath(
                path = Path().apply {
                    moveTo(comparacionPoints.first().x, comparacionPoints.first().y)
                    comparacionPoints.drop(1).forEach { lineTo(it.x, it.y) }
                    close()
                },
                color = Color(0xFF689F38),
                style = Stroke(width = 2f)
            )
        }

        // Etiquetas
        entries.forEachIndexed { i, entry ->
            val angle = angleStep * i - PI / 2
            val labelRadius = radius + 32f
            val x = center.x + labelRadius * cos(angle).toFloat()
            val y = center.y + labelRadius * sin(angle).toFloat()
            drawContext.canvas.nativeCanvas.drawText(
                entry.label,
                x,
                y,
                Paint().apply {
                    textAlign = Paint.Align.CENTER
                    textSize = 30f
                    color = android.graphics.Color.BLACK
                }
            )
        }
    }
}

//Función auxiliar para mostrar la gráfica en pantalla
@Composable
fun RadarChartTestsSection(
    comparacionEntries: List<TestRadarEntry>? = null
) {
    val labels = listOf("Amigos", "Familia", "Centro")
    val tipos = listOf("amigos", "familia", "centro_educativo")

    val entries = tipos.mapIndexed { i, tipo ->
        val valor = QuizSessionState.resultadosPorTipo[tipo]
        if (valor != null) {
            TestRadarEntry(labels[i], valor.toFloat())
        } else {
            TestRadarEntry("${labels[i]}\n(Faltan datos)", 0f)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Comparación global por test", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        RadarChartTests(entries = entries, comparacionEntries = comparacionEntries)
    }
}
