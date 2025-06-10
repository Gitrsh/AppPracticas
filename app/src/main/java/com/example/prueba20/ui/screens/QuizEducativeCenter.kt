package com.example.prueba20.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.prueba20.data.DAOQuestions
import com.example.prueba20.viewmodel.UserViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun QuizEducativeCenterScreen(
    userViewModel: UserViewModel,
    navController: NavController,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val questions = remember { DAOQuestions.getAllQuestions(context, "questionsEducativeCenter.txt") }

    QuizScreenBase(
        questions = questions,
        userViewModel = userViewModel,
        navController = navController,
        tipoTest = "centro_educativo", // nombre de la colección dnd se guradará
        onBack = onBack
    )
}
