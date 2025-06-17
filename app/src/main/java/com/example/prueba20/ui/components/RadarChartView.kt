package com.example.prueba20.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*
import android.graphics.Paint

@Composable
fun RadarChartView(
    valores: List<Int>,
    valoresComparacion: List<Int>? = null,
    labels: List<String> = listOf("Motivación", "Confianza", "Competencia", "Comprensión"),//cambiado
    modifier: Modifier = Modifier,
    chartSize: Dp = 180.dp,
    labelTextSize: TextUnit = 12.sp
) {
    val maxValor = 100f
    val sides = valores.size

    Canvas(modifier = modifier.size(chartSize)) {
        val w = size.width
        val h = size.height
        val center = Offset(w / 2, h / 2)
        val radius = size.minDimension / 2.8f // más compacto y deja margen superior para el título
        val angleStep = (2 * PI / sides).toFloat()

        // Líneas guias
        for (i in 0 until sides) {
            val angle = angleStep * i - PI.toFloat() / 2
            val end = Offset(
                x = center.x + radius * cos(angle),
                y = center.y + radius * sin(angle)
            )
            drawLine(Color.LightGray, start = center, end = end, strokeWidth = 1f)
        }

        // Polígono (respuestas)
        val path = Path()
        valores.forEachIndexed { i, valor ->
            val angle = angleStep * i - PI.toFloat() / 2
            val r = (valor / maxValor) * radius
            val point = Offset(
                x = center.x + r * cos(angle),
                y = center.y + r * sin(angle)
            )
            if (i == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
        }
        path.close()

        drawPath(path, color = Color(0xFF2196F3).copy(alpha = 0.4f))
        drawPath(path, color = Color(0xFF2196F3), style = Stroke(width = 2f))

        labels.forEachIndexed { i, label ->
            val angle = angleStep * i - PI.toFloat() / 2
            val labelRadius = radius + 36f // más margen
            val x = center.x + labelRadius * cos(angle)
            val y = center.y + labelRadius * sin(angle)

            val paint = android.graphics.Paint().apply {
                textSize = labelTextSize.toPx()
                color = android.graphics.Color.BLACK
                isAntiAlias = true

                textAlign = when {
                    angle in (-PI / 4)..(PI / 4) -> Paint.Align.LEFT      // derecha
                    angle in (3 * PI / 4)..PI || angle < -3 * PI / 4 -> Paint.Align.RIGHT  // izquierda
                    else -> Paint.Align.CENTER
                }
            }

            // Polígono comparativo
            valoresComparacion?.let {
                val pathComparacion = Path()
                it.forEachIndexed { i, valor ->
                    val angle = angleStep * i - PI.toFloat() / 2
                    val r = (valor / maxValor) * radius
                    val point = Offset(
                        x = center.x + r * cos(angle),
                        y = center.y + r * sin(angle)
                    )
                    if (i == 0) pathComparacion.moveTo(point.x, point.y) else pathComparacion.lineTo(point.x, point.y)
                }
                pathComparacion.close()

                drawPath(pathComparacion, color = Color(0xFF7E57C2).copy(alpha = 0.25f)) // PAra el color del poligono
                drawPath(pathComparacion, color = Color(0xFF7E57C2), style = Stroke(width = 2f)) // esto para el contorno
            }

            // Ajustes para que el texto no se meta en el gráfico
            val yAdjusted = when {
                angle < -PI / 2 -> y + 20f
                angle > PI / 2 -> y + 20f
                else -> y - 10f
            }

            drawContext.canvas.nativeCanvas.drawText(label, x, yAdjusted, paint)
        }
    }
}