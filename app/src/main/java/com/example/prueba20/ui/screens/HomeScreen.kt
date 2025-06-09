package com.example.prueba20.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prueba20.R
import com.example.prueba20.viewmodel.UserViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period

/**
 * Pantalla principal después del login. Muestra:
 * - Perfil del usuario.
 * - Carrusel de imágenes.
 * - Botón para iniciar el cuestionario.
 */
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalAnimationApi::class, ExperimentalPagerApi::class)
@Composable
fun HomeScreen(
    userViewModel: UserViewModel,
    onStartQuiz: () -> Unit,
    onLogout: () -> Unit,
    onAboutUs: () -> Unit
) {
    val userState by userViewModel.user.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        userState?.let { user ->
            UserProfileHeader(
                name = user.name,
                age = calculateAge(user.birthDate),
                onLogout = onLogout,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    Color.Transparent
                                ),
                                startY = 0f,
                                endY = 150f
                            )
                        )
                )

                val images = listOf(
                    R.drawable.atletismo,
                    R.drawable.clase,
                    R.drawable.corriendo,
                    R.drawable.gimnasio
                )
                val pagerState = rememberPagerState()
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    while (true) {
                        delay(3000)
                        val nextPage = (pagerState.currentPage + 1) % images.size
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                }

                HorizontalPager(
                    count = images.size,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    Image(
                        painter = painterResource(images[page]),
                        contentDescription = "Imagen $page",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    HorizontalPagerIndicator(
                        pagerState = pagerState,
                        modifier = Modifier.align(Alignment.Center),
                        activeColor = MaterialTheme.colorScheme.primary,
                        inactiveColor = MaterialTheme.colorScheme.surfaceVariant,
                        indicatorWidth = 12.dp,
                        indicatorHeight = 4.dp,
                        spacing = 6.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "¡Bienvenido!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Prepárate para descubrir tu nivel de actividad física",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(
                        elevation = AppTheme.Elevation.lg,
                        shape = MaterialTheme.shapes.large,
                        clip = true
                    ),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AppTheme.Elevation.md,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_quiz),
                        contentDescription = "Quiz",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Cuestionario de Actividad Física",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Descubre tu nivel actual y recibe recomendaciones personalizadas para mejorar tu salud y bienestar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = "Comenzar ahora",
                        onClick = onStartQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        icon = Icons.Default.PlayArrow
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SecondaryButton(
                text = "Conoce más sobre nosotros",
                onClick = onAboutUs,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                icon = Icons.Default.Info
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateAge(birthDateString: String): Int {
    return try {
        val birthDate = LocalDate.parse(birthDateString)
        val today = LocalDate.now()
        Period.between(birthDate, today).years
    } catch (e: Exception) {
        0
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

