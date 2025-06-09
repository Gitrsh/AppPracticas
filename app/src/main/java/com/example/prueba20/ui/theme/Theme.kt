package com.example.prueba20.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object AppTheme {
    object Spacing {
        val xs = 4.dp
        val sm = 8.dp
        val md = 16.dp
        val lg = 24.dp
        val xl = 32.dp
        val xxl = 48.dp
    }

    object Shapes {
        val small = RoundedCornerShape(4.dp)
        val medium = RoundedCornerShape(8.dp)
        val large = RoundedCornerShape(16.dp)
        val xl = RoundedCornerShape(24.dp)
        val pill = RoundedCornerShape(50)
    }

    object Elevation {
        val none = 0.dp
        val sm = 2.dp
        val md = 4.dp
        val lg = 8.dp
        val xl = 12.dp
    }

    object Animation {
        const val short = 300
        const val medium = 500
        const val long = 800
    }
}

@Composable
fun AppMaterialTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography.toMaterialTypography(),
        shapes = Shapes(
            small = AppTheme.Shapes.small,
            medium = AppTheme.Shapes.medium,
            large = AppTheme.Shapes.large
        ),
        content = content
    )
}

