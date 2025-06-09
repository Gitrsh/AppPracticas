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

/**
 * ViewModel que gestiona autenticación, registro, logout y verificación de permisos.
 */
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
                        onSuccess = { name, birthDate ->
                            saveUser(name, email, birthDate)
                            comprobarPermisoAdmin(email) { esAdmin ->
                                _error.value = null
                                _isLoading.value = false
                                _user.value = _user.value?.copy(isAdmin = esAdmin, isLoggedIn = true)
                                callback(true, esAdmin)
                            }
                        },
                        onError = {
                            _error.value = it.message
                            _isLoading.value = false
                            callback(false, false)
                        }
                    )
                },
                onError = {
                    _error.value = "Error de inicio de sesión: ${it.message}"
                    _isLoading.value = false
                    callback(false, false)
                }
            )
        }
    }

    fun register(name: String, email: String, password: String, birthDate: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            FirebaseRepository.registrarUsuario(
                name = name,
                email = email,
                password = password,
                birthDate = birthDate,
                onSuccess = {
                    saveUser(name, email, birthDate)
                    _error.value = null
                    _isLoading.value = false
                    callback(true)
                },
                onError = {
                    _error.value = "Error de registro: ${it.message}"
                    _isLoading.value = false
                    callback(false)
                }
            )
        }
    }

    fun saveUser(name: String, email: String, birthDate: String) {
        viewModelScope.launch {
            val userData = UserData(name, email, birthDate, isLoggedIn = true)
            prefs.saveUserData(name, email, birthDate)
            _user.value = userData
        }
    }

    fun saveUserWithLog(context: Context, name: String, email: String, birthDate: String) {
        viewModelScope.launch {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val loginEntry = LoginEntry(name, email, birthDate, timestamp)
            prefs.saveUserData(name, email, birthDate)
            logDao.saveLogin(loginEntry)
            _user.value = UserData(name, email, birthDate, isLoggedIn = true)
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
            .whereEqualTo("email", userEmail)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val document = result.documents.first()
                    val esAdmin = document.getBoolean("permiso_admin") ?: false
                    onResult(esAdmin)
                } else {
                    onResult(false)
                }
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

    fun setError(message: String) {
        _error.value = message
    }
}

