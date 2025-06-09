package com.example.prueba20.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Estilos de texto predefinidos para la app (títulos, cuerpo, subtítulos).
 * Se adaptan a Material Design 3 con [toMaterialTypography].
 */
object AppTypography {
    val h1 = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, lineHeight = 40.sp)
    val h2 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
    val body = TextStyle(fontSize = 16.sp, lineHeight = 24.sp)
    val caption = TextStyle(fontSize = 14.sp, lineHeight = 20.sp)

    fun toMaterialTypography() = Typography(
        headlineLarge = h1,
        titleLarge = h2,
        bodyLarge = body,
        labelLarge = caption
    )
}
