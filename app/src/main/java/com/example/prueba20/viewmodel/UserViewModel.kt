package com.example.prueba20.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prueba20.data.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserViewModel(
    private val prefs: UserPreferences,
    private val logDao: LoginLogDao,
    private val onLogoutSuccess: () -> Unit
) : ViewModel() {

    private val _user = MutableStateFlow<UserData?>(null)
    val user: StateFlow<UserData?> = _user

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            prefs.userData.collect { userData ->
                _user.value = userData
            }
        }
    }

    fun login(email: String, password: String, callback: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseRepository.iniciarSesion(
                email = email,
                password = password,
                onSuccess = {
                    FirebaseRepository.cargarDatosUsuario(
                        email = email,
                        onSuccess = { name: String, birthDate: String, grupo: String ->
                            comprobarPermisoAdmin(email) { esAdmin ->
                                viewModelScope.launch {
                                    val userData = UserData(
                                        name = name,
                                        email = email,
                                        birthDate = birthDate,
                                        grupo = grupo,
                                        isLoggedIn = true,
                                        isAdmin = esAdmin
                                    )
                                    prefs.saveUserData(name, email, birthDate, grupo)
                                    _user.value = userData
                                    _error.value = null
                                    _isLoading.value = false
                                    callback(true, esAdmin)
                                }
                            }
                        },
                        onError = { e: Exception ->
                            _error.value = e.message
                            _isLoading.value = false
                            callback(false, false)
                        }
                    )
                },
                onError = { e ->
                    _error.value = "Error de inicio de sesión: ${e.message}"
                    _isLoading.value = false
                    callback(false, false)
                }
            )
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        birthDate: String,
        pais: String,
        grupo: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseRepository.registrarUsuario(
                name = name,
                email = email,
                password = password,
                birthDate = birthDate,
                pais = pais,
                grupo = grupo,
                onSuccess = {
                    viewModelScope.launch {
                        prefs.saveUserData(name, email, birthDate, grupo)
                        _isLoading.value = false
                        _user.value = UserData(name, email, birthDate, grupo, isLoggedIn = true)
                        onSuccess()
                    }
                },
                onError = { e ->
                    _isLoading.value = false
                    _error.value = e.message
                }
            )
        }
    }

    fun saveUser(name: String, email: String, birthDate: String, grupo: String) {
        viewModelScope.launch {
            val userData = UserData(name, email, birthDate, grupo, isLoggedIn = true)
            prefs.saveUserData(name, email, birthDate, grupo)
            _user.value = userData
        }
    }

    fun saveUserWithLog(context: Context, name: String, email: String, birthDate: String, grupo: String) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val loginEntry = LoginEntry(name, email, birthDate, timestamp)
            prefs.saveUserData(name, email, birthDate, grupo)
            logDao.saveLogin(loginEntry)
            _user.value = UserData(name, email, birthDate, grupo, isLoggedIn = true)
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _isLoggingOut.value = true
                prefs.clearUserData()
                delay(500)
                _user.value = null
                onLogoutSuccess()
            } catch (e: Exception) {
                _error.value = "Error al cerrar sesión"
            } finally {
                _isLoggingOut.value = false
            }
        }
    }

    fun comprobarPermisoAdmin(userEmail: String, onResult: (Boolean) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios")
            .document(userEmail)
            .get()
            .addOnSuccessListener { document ->
                val esAdmin = document.getBoolean("permiso_admin") ?: false
                onResult(esAdmin)
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun setError(message: String) {
        _error.value = message
    }
}