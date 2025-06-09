package com.example.prueba20.data

import android.content.Context

object Form {
    fun returnQuestion(context: Context, fileName: String, opt: Int): String? {
        return DAOQuestions.readQuestion(context, fileName, opt)
    }

    fun getAll(context: Context, fileName: String): List<String> {
        return DAOQuestions.getAllQuestions(context, fileName)
    }
}

