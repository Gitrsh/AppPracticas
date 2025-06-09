package com.example.prueba20.data

import android.util.Log
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
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                guardarUsuario(name, email, birthDate, onSuccess, onError)
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseAuth", "Error al registrar usuario", e)
                onError(e)
            }
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
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val data = mapOf(
            "name" to name,
            "email" to email,
            "birthDate" to birthDate
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
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val data = mapOf(
            "email" to email,
            "respuestas" to respuestas,
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("respuestas")
            .add(data)
            .addOnSuccessListener {
                Log.d("Firestore", "Respuestas guardadas")
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
        onSuccess: (name: String, birthDate: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection("usuarios")
            .document(email)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("name") ?: ""
                    val birthDate = document.getString("birthDate") ?: ""
                    onSuccess(name, birthDate)
                } else {
                    onError(Exception("El usuario no existe en Firestore"))
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al cargar usuario", e)
                onError(e)
            }
    }
}
