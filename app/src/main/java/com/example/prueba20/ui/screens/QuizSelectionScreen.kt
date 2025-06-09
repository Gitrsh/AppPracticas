package com.example.prueba20.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun QuizSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Selecciona un cuestionario")

        Button(onClick = { navController.navigate("quiz_amigos") }) {
            Text("Cuestionario Amigos")
        }

        Button(onClick = { navController.navigate("quiz_familia") }) {
            Text("Cuestionario Familia")
        }

        Button(onClick = { navController.navigate("quiz_educative") }) {
            Text("Cuestionario Centro Educativo")
        }
    }
}
