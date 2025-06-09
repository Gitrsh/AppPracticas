 package com.example.prueba20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.ScreenWithBackground
import com.example.prueba20.viewmodel.UserViewModel
import kotlinx.coroutines.delay

/**
 * Pantalla de inicio de sesión con campos para email y contraseña.
 * Incluye manejo de estado de carga y verificación de rol (admin o no).
 *
 * @param userViewModel ViewModel que maneja la lógica de autenticación.
 * @param onLoginSuccess Callback al iniciar sesión exitosamente: (loginExitoso, esAdmin)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userViewModel: UserViewModel,
    onLoginSuccess: (Boolean, Boolean) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.error.collectAsState()
    val user by userViewModel.user.collectAsState()

    var showFinalLoader by remember { mutableStateOf(false) }

    LaunchedEffect(user?.isLoggedIn) {
        if (user?.isLoggedIn == true) {
            showFinalLoader = true
            delay(2000)
            onLoginSuccess(true, user?.isAdmin == true)
        }
    }

    if (isLoading || showFinalLoader) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    ScreenWithBackground {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(AppTheme.Spacing.md))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(AppTheme.Spacing.md))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(AppTheme.Spacing.lg))

            Button(
                onClick = {
                    userViewModel.login(email, password) { loginSuccess, esAdmin ->
                        if (loginSuccess) {
                        } else {
                            onLoginSuccess(false, false)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(AppTheme.Spacing.md))
                Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
            }

            if (isLoading) {
                Spacer(modifier = Modifier.height(AppTheme.Spacing.md))
                CircularProgressIndicator()
            }
        }
    }
}

