package com.example.prueba20

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.prueba20.ui.navigation.ComposeCuestionarioApp
import com.example.prueba20.ui.theme.AppMaterialTheme
import com.example.prueba20.viewmodel.UserViewModel
import com.example.prueba20.viewmodel.UserViewModelFactory
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(application) {}
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userEmail = currentUser?.email

        if (userEmail != null) {
            userViewModel.comprobarPermisoAdmin(userEmail) { esAdmin ->
                setContent {
                    AppMaterialTheme {
                        ComposeCuestionarioApp(userViewModel = userViewModel, isAdmin = esAdmin)
                    }
                }
            }
        } else {
            setContent {
                AppMaterialTheme {
                    ComposeCuestionarioApp(userViewModel = userViewModel)
                }
            }
        }
    }
}



