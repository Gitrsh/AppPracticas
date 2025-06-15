package com.example.prueba20.data

import android.util.Log
import com.example.prueba20.util.QuizSessionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Objeto singleton que maneja todas las operaciones con Firebase:
 * - Autenticación (registro/login).
 * - Firestore (guardar/cargar datos de usuario y respuestas).
 */
object FirebaseRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registrarUsuario(
        name: String,
        email: String,
        password: String,
        birthDate: String,
        pais: String,
        grupo: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val userMap = hashMapOf(
                    "nombre" to name,
                    "email" to email,
                    "fechaNacimiento" to birthDate,
                    "pais" to pais,
                    "grupo" to grupo
                )
                FirebaseFirestore.getInstance().collection("usuarios")
                    .document(email)
                    .set(userMap)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e) }
            }
            .addOnFailureListener { e -> onError(e) }
    }

    fun iniciarSesion(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("FirebaseAuth", "Inicio de sesión exitoso")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAuth", "Error al iniciar sesión", e)
                onError(e)
            }
    }

    fun guardarUsuario(
        name: String,
        email: String,
        birthDate: String,
        pais: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val data = mapOf(
            "name" to name,
            "email" to email,
            "birthDate" to birthDate,
            "pais" to pais
        )

        db.collection("usuarios")
            .document(email)
            .set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Usuario guardado en Firestore")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al guardar usuario", e)
                onError(e)
            }
    }

    fun guardarRespuestas(
        email: String,
        respuestas: List<Int>,
        tipoTest: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val data = mapOf(
            "email" to email.lowercase(),
            "respuestas" to respuestas,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection(tipoTest)
            .document(email.lowercase()) // Sobrescribe por email
            .set(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Respuestas sobrescritas en colección $tipoTest")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al guardar respuestas", e)
                onError(e)
            }
    }

    fun obtenerUsuarioActual(): String? {
        return auth.currentUser?.email
    }

    fun cargarDatosUsuario(
        email: String,
        onSuccess: (name: String, birthDate: String, grupo: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val birthDate = document.getString("birthDate") ?: ""
                    val grupo = document.getString("grupo") ?: ""
                    onSuccess(name, birthDate, grupo)
                } else {
                    onError(Exception("El usuario no existe en Firestore"))
                }
            }
            .addOnFailureListener { e ->
                onError(e)
            }
    }



}
