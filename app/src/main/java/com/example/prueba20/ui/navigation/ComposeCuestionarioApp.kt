package com.example.prueba20.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import com.example.prueba20.ui.components.LogoutLoadingScreen
import com.example.prueba20.ui.screens.*
import com.example.prueba20.ui.theme.HomeScreen
import com.example.prueba20.viewmodel.UserViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import androidx.compose.runtime.getValue
import androidx.navigation.compose.composable
import com.example.prueba20.ui.components.FullScreenLoader
import com.example.prueba20.ui.screens.MediaResultScreen

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ComposeCuestionarioApp(userViewModel: UserViewModel, isAdmin: Boolean = false) {
    val isLoggingOut by userViewModel.isLoggingOut.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val navController = rememberNavController()
    val user by userViewModel.user.collectAsState()

    if (isLoggingOut) {
        LogoutLoadingScreen()
        return
    }

    if (isLoading) {
        FullScreenLoader()
        return
    }

    val startDestination = when {
        user?.isLoggedIn == true && user?.isAdmin == true -> "admin"
        user?.isLoggedIn == true -> "home"
        else -> "welcome"
    }

    AnimatedNavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate("login") },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onLoginSuccess = { loginSuccess, esAdmin ->
                    if (loginSuccess) {
                        navController.navigate(if (esAdmin) "admin" else "home") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("register") {
            RegisterScreen(
                userViewModel = userViewModel,
                onRegisterSuccess = {
                    navController.navigate("home") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                userViewModel = userViewModel,
                onStartQuiz = { navController.navigate("quiz_selection") },
                onLogout = { userViewModel.logout() },
                onAboutUs = { navController.navigate("about") }
            )
        }



        composable("about") {
            AboutScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("admin") {
            EstadisticasAdminScreen(
                onLogout = { userViewModel.logout() }
            )
        }

        composable("recomendaciones/{average}") { backStackEntry ->
            val average = backStackEntry.arguments?.getString("average")?.toIntOrNull() ?: 0
            RecommendationsScreen(
                average = average,
                onBack = { navController.popBackStack() }
            )

        }
        composable("quiz_selection") {
            QuizSelectionScreen(navController = navController)
        }

        composable("quiz_amigos") {
            QuizAmigosScreen(
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable("quiz_familia") {
            QuizFamiliaScreen(
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable("quiz_educative") {
            QuizEducativeCenterScreen(
                userViewModel = userViewModel,
                navController = navController
            )
        }

        composable("media_result") {
            MediaResultScreen(navController = navController)
        }

        composable("comparison") {
            ComparisonScreen(navController)
        }


    }
}



