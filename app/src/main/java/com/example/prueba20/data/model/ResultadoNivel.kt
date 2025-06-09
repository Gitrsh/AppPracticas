package com.example.prueba20.data.model

import androidx.compose.ui.graphics.Color

data class ResultadoNivel(
    val titulo: String,
    val mensaje: String,
    val color: Color,
    val emoji: String,
    val coloresGradiente: List<Color>
)