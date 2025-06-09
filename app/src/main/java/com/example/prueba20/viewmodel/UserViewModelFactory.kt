package com.example.prueba20.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.prueba20.data.LoginLogDao
import com.example.prueba20.data.UserPreferences

/**
 * Factory para crear instancias de [UserViewModel] con las dependencias necesarias.
 * Requiere [Application] para inicializar [UserPreferences] y [LoginLogDao].
 */
class UserViewModelFactory(
    private val application: Application,
    private val onLogoutSuccess: () -> Unit
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val prefs = UserPreferences(application)
        val logDao = LoginLogDao(application)
        return UserViewModel(prefs, logDao, onLogoutSuccess) as T
    }
}