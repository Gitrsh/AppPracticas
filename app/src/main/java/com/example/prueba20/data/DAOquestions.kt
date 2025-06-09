package com.example.prueba20.data

import android.content.Context

object DAOQuestions {
    fun readQuestion(context: Context, fileName: String, index: Int): String? {
        return try {
            val inputStream = context.assets.open(fileName)
            val lines = inputStream.bufferedReader().readLines()
            if (index in lines.indices) lines[index] else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllQuestions(context: Context, fileName: String): List<String> {
        return try {
            context.assets.open(fileName)
                .bufferedReader()
                .readLines()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
