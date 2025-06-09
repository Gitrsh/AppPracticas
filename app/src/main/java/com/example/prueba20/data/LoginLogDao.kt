package com.example.prueba20.data

import android.content.Context
import java.io.File

data class LoginEntry(
    val name: String,
    val email: String,
    val birthDate: String,
    val loginTime: String
)

class LoginLogDao(private val context: Context) {
    private val fileName = "logins.csv"

    fun saveLogin(entry: LoginEntry) {
        val file = File(context.filesDir, fileName)
        val line = "${entry.name},${entry.email},${entry.birthDate},${entry.loginTime}\n"
        file.appendText(line)
    }
}