package com.example.prueba20.util

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf

object QuizSessionState {
    //val sessionResults = mutableStateListOf<Int>()
    val respuestasPorTest = mutableStateListOf<List<Int>>()
    val resultadosPorTipo = mutableStateMapOf<String, Int>() // <-- Asegúrate de tener esto
    val nombresTest = listOf("familia", "amigos", "centro_educativo")
}

fun QuizSessionState.getMedia(tipo: String): Float {
    return resultadosPorTipo[tipo]?.toFloat() ?: 0f
}
