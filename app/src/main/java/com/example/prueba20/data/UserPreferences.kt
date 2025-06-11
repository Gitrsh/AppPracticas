package com.example.prueba20.data

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore("user_prefs")

object UserPrefsKeys {
    val NAME = stringPreferencesKey("name")
    val EMAIL = stringPreferencesKey("email")
    val BIRTHDATE = stringPreferencesKey("birthdate")
    val GRUPO = stringPreferencesKey("grupo")
    val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

data class UserData(
    val name: String,
    val email: String,
    val birthDate: String,
    val grupo: String = "",
    val isLoggedIn: Boolean = false,
    val isAdmin: Boolean = false
)

/**
 * Maneja el almacenamiento local de datos del usuario usando DataStore.
 * Guarda: nombre, email, fecha de nacimiento, grupo y estado de sesión.
 */
class UserPreferences(private val context: Context) {
    val userData: Flow<UserData> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            UserData(
                name = prefs[UserPrefsKeys.NAME] ?: "",
                email = prefs[UserPrefsKeys.EMAIL] ?: "",
                birthDate = prefs[UserPrefsKeys.BIRTHDATE] ?: "",
                grupo = prefs[UserPrefsKeys.GRUPO] ?: "",
                isLoggedIn = prefs[UserPrefsKeys.IS_LOGGED_IN] ?: false
            )
        }

    suspend fun saveUserData(name: String, email: String, birthDate: String, grupo: String) {
        context.dataStore.edit { prefs ->
            prefs[UserPrefsKeys.NAME] = name
            prefs[UserPrefsKeys.EMAIL] = email
            prefs[UserPrefsKeys.BIRTHDATE] = birthDate
            prefs[UserPrefsKeys.GRUPO] = grupo
            prefs[UserPrefsKeys.IS_LOGGED_IN] = true
        }
    }

    suspend fun clearUserData() {
        context.dataStore.edit { preferences ->
            preferences[UserPrefsKeys.NAME] = ""
            preferences[UserPrefsKeys.EMAIL] = ""
            preferences[UserPrefsKeys.BIRTHDATE] = ""
            preferences[UserPrefsKeys.GRUPO] = ""
            preferences[UserPrefsKeys.IS_LOGGED_IN] = false
        }
    }
}