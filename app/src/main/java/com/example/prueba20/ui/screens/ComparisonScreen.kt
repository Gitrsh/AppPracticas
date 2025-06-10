package com.example.prueba20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.prueba20.ui.theme.AppTopBar

@Composable
fun ComparisonScreen(navController: NavController) {
    Scaffold(
        topBar = {
            AppTopBar(
                title = "Pantalla de Comparación",
                navigationIcon = Icons.Default.ArrowBack,
                navigationAction = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Pantalla de comparación en construcción",
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}