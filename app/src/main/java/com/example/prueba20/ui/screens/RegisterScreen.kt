package com.example.prueba20.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.ScreenWithBackground
import com.example.prueba20.viewmodel.UserViewModel
import kotlinx.coroutines.delay
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import androidx.compose.ui.viewinterop.AndroidView
import com.hbb20.CountryCodePicker

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterScreen(
    userViewModel: UserViewModel,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var paisSeleccionado by remember { mutableStateOf("") }

    val datePickerState = rememberDatePickerState()

    val isLoading by userViewModel.isLoading.collectAsState()
    val errorMessage by userViewModel.error.collectAsState()
    val user by userViewModel.user.collectAsState()

    var showFinalLoader by remember { mutableStateOf(false) }

    LaunchedEffect(user?.isLoggedIn) {
        if (user?.isLoggedIn == true) {
            showFinalLoader = true
            delay(3000)
            onRegisterSuccess()
        }
    }

    if (isLoading || showFinalLoader) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    selectedMillis?.let {
                        birthDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.padding(16.dp),
                showModeToggle = true
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de acceso") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        ScreenWithBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppTheme.Spacing.xl),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (birthDate != null) {
                    val age = Period.between(birthDate, LocalDate.now()).years
                    Text(
                        text = "Edad: $age años",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp, bottom = AppTheme.Spacing.md)
                    )
                } else {
                    Spacer(modifier = Modifier.height(AppTheme.Spacing.md))
                }

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
                Spacer(modifier = Modifier.height(AppTheme.Spacing.md))

                OutlinedTextField(
                    value = birthDate?.toString() ?: "",
                    onValueChange = {},
                    label = { Text("Fecha de nacimiento") },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true }
                )

                Spacer(modifier = Modifier.height(AppTheme.Spacing.md))

                Text("Selecciona tu país:", style = MaterialTheme.typography.labelLarge)

                AndroidView(
                    factory = { context ->
                        CountryCodePicker(context).apply {

                            setOnCountryChangeListener {
                                paisSeleccionado = selectedCountryName
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                if (paisSeleccionado.isNotBlank()) {
                    Text("País seleccionado: $paisSeleccionado")
                }

                Spacer(modifier = Modifier.height(AppTheme.Spacing.lg))

                Button(
                    onClick = {
                        when {
                            !email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) ->
                                userViewModel.setError("Correo no válido")

                            password.length < 6 ->
                                userViewModel.setError("Contraseña mínima de 6 caracteres")

                            birthDate == null ->
                                userViewModel.setError("Por favor selecciona tu fecha de nacimiento")

                            paisSeleccionado.isBlank() ->
                                userViewModel.setError("Selecciona un país válido")

                            else -> userViewModel.register(
                                name, email, password, birthDate.toString(), paisSeleccionado
                            ) {}
                        }
                    },
                    enabled = birthDate != null,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Comenzar test")
                }

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(AppTheme.Spacing.md))
                    Text(errorMessage ?: "", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

