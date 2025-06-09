package com.example.prueba20.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class RespuestaItem(
    val id: String,
    val correo: String,
    val respuestas: List<Int>
)

@Composable
fun EstadisticasAdminScreen(
    onLogout: () -> Unit
) {
    var respuestas by remember { mutableStateOf<List<RespuestaItem>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<RespuestaItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("respuestas").get().await()
        respuestas = snapshot.documents.mapNotNull { doc ->
            val correo = doc.getString("email")
            val respuestasList = doc.get("respuestas") as? List<Long>
            if (correo != null && respuestasList != null) {
                RespuestaItem(
                    id = doc.id,
                    correo = correo,
                    respuestas = respuestasList.map { it.toInt() }
                )
            } else null
        }
        isLoading = false
    }

    val filteredRespuestas = respuestas.filter {
        it.correo.contains(searchQuery.trim(), ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Panel de Administrador", style = MaterialTheme.typography.headlineSmall)
            Button(onClick = onLogout) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por correo") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (filteredRespuestas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron resultados.")
                }
            } else {
                LazyColumn {
                    items(filteredRespuestas) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = item.correo,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Respuestas: ${item.respuestas.joinToString()}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(onClick = {
                                        editingItem = item
                                        showDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                                    }
                                    IconButton(onClick = {
                                        eliminarRespuesta(item.id) {
                                            respuestas = respuestas.filterNot { it.id == item.id }
                                        }
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Diálogo de edición
        if (showDialog && editingItem != null) {
            var newText by remember { mutableStateOf(editingItem!!.respuestas.joinToString(",")) }

            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(onClick = {
                        val nuevosValores = newText
                            .split(",")
                            .mapNotNull { it.trim().toIntOrNull() }

                        actualizarRespuesta(editingItem!!.id, nuevosValores) {
                            respuestas = respuestas.map {
                                if (it.id == editingItem!!.id) it.copy(respuestas = nuevosValores)
                                else it
                            }
                            showDialog = false
                        }
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("Editar respuestas") },
                text = {
                    OutlinedTextField(
                        value = newText,
                        onValueChange = { newText = it },
                        label = { Text("Respuestas (separadas por comas)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
        }
    }
}

fun eliminarRespuesta(id: String, onSuccess: () -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("respuestas")
        .document(id)
        .delete()
        .addOnSuccessListener { onSuccess() }
}

fun actualizarRespuesta(id: String, nuevasRespuestas: List<Int>, onSuccess: () -> Unit) {
    FirebaseFirestore.getInstance()
        .collection("respuestas")
        .document(id)
        .update("respuestas", nuevasRespuestas)
        .addOnSuccessListener { onSuccess() }
}


