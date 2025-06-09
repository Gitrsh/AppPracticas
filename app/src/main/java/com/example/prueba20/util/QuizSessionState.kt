package com.example.prueba20.util

import androidx.compose.runtime.mutableStateListOf

object QuizSessionState {
    val sessionResults = mutableStateListOf<Int>()
    val respuestasPorTest = mutableStateListOf<List<Int>>()
    val nombresTest = listOf("Familia", "Amigos", "Trabajo")
}