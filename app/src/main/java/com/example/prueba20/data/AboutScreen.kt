package com.example.prueba20.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prueba20.R
import com.example.prueba20.ui.theme.AppTheme
import com.example.prueba20.ui.theme.AppTopBar

/**
 * Pantalla "Sobre Nosotros" que muestra:
 * - Equipo de desarrollo (tarjetas horizontales).
 * - Equipo de investigación (con instituciones).
 * - Imagen de la facultad con gradiente.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val devTeam = listOf(
        TeamMember("Jorge Hernández", "Responsable del Proyecto", "Realizador del código y gestor de base de datos", R.drawable.icono),
        TeamMember("Alejandro Antúnez", "Desarrollador", "Creador del cuestionario y asistente en el código", R.drawable.icono),
        TeamMember("Marcos De San Eugenio", "Desarrollador", "Encargado de mejoras visuales y apoyo en desarrollo", R.drawable.icono),
        TeamMember("Roberto", "Ayudante de programación", "Colaboración en el desarrollo y pruebas del sistema", R.drawable.icono),
        TeamMember("Álvaro", "Ayudante de programación", "Apoyo en implementación y corrección de errores", R.drawable.icono)
    )

    val researchTeam = listOf(
        ResearchMember("José Carmelo Adsuar Sala", "Investigador Profesional", listOf("University of Lisbon", "University of Extremadura")),
        ResearchMember("Jorge Carlos Vivas", "Investigador Profesional", listOf("Universidad de Extremadura")),
        ResearchMember("María Mendoza Muñoz", "Investigadora", listOf("Universidad de Extremadura")),
        ResearchMember("Ricardo Hugo Gonzalo", "Investigador Principal", listOf("Universidad Federal do Ceará")),
        ResearchMember("Raquel Pastor Cisnero", "Personal en formación (FPU)", listOf("PHeSO")),
        ResearchMember("Adilson Passos da Costa Marques", "Investigador", listOf("University of Lisbon")),
        ResearchMember("Tiago D. Ribeiro", "PhD candidate", listOf("University of Lisbon")),
        ResearchMember("Sandy Dorian isla Alcoser", "Investigador", listOf("Universidad Nacional Mayor de San Marcos")),
        ResearchMember("Jose Antonio Romero Macarilla", "Investigador Asociado", listOf("Universidad de Extremadura")),
        ResearchMember("Jean Carlos Rosales García", "Asistente", listOf("Universidad del Atlántico")),
        ResearchMember("Jorge De Lázaro Coll Costa", "Vicerrector e Investigador", listOf("Universidad de Ciencias de la Cultura Física y Deporte Manuel Fajardo")),
        ResearchMember("Cristian Pérez Tapia", "Investigador", listOf("Universidad Santo Tomás")),
        ResearchMember("Natalia Triviño Amigo", "Profesora e Investigadora", listOf("Universidad Europea")),
        ResearchMember("Eddy Silva", "Investigador", listOf("Nicaragua")),
        ResearchMember("Maristela De Lima Ferreira", "Investigadora", listOf("E-8-77527")),
        ResearchMember("Milton Fernando Rosero Duque", "Investigador", listOf("Ecuador"))
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Sobre Nosotros",
                navigationIcon = Icons.Default.ArrowBack,
                navigationAction = onBack
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AppTheme.Spacing.lg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.campus),
                    contentDescription = "Facultad  de Ciencias del Deporte",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.7f)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(AppTheme.Spacing.md)
                ) {
                    Text(
                        "Facultad  de Ciencias del Deporte",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(AppTheme.Spacing.sm))
                    Text(
                        "Comprometidos con la excelencia académica e investigación multidisciplinar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Sección Equipo de Investigación
            TeamSection(
                title = "Equipo de Investigación",
                members = researchTeam,
                cardContent = { member ->
                    ResearchCardContent(member)
                }
            )

            // Sección Equipo de Desarrollo
            TeamSection(
                title = "Equipo de Desarrollo",
                members = devTeam,
                cardContent = { member ->
                    TeamCardContent(member)
                }
            )

            Spacer(modifier = Modifier.height(AppTheme.Spacing.xl))
        }
    }
}

@Composable
private fun <T> TeamSection(
    title: String,
    members: List<T>,
    cardContent: @Composable (T) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = AppTheme.Spacing.md)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.Spacing.md),
            contentPadding = PaddingValues(horizontal = AppTheme.Spacing.md),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(members) { member ->
                Card(
                    modifier = Modifier
                        .width(280.dp)
                        .height(320.dp),
                    shape = RoundedCornerShape(AppTheme.Spacing.md),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = AppTheme.Elevation.sm
                    )
                ) {
                    cardContent(member)
                }
            }
        }
    }
}

@Composable
private fun TeamCardContent(member: TeamMember) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppTheme.Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.Spacing.sm)
    ) {
        Image(
            painter = painterResource(member.imageRes),
            contentDescription = "Foto de ${member.name}",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = member.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = member.role,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = AppTheme.Spacing.sm),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        Text(
            text = member.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppTheme.Spacing.sm)
        )
    }
}

@Composable
private fun ResearchCardContent(member: ResearchMember) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(AppTheme.Spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Investigador",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Text(
            text = member.name,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = member.role,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(vertical = AppTheme.Spacing.sm),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.Spacing.xs)
        ) {
            member.institutions.forEach { institution ->
                Text(
                    text = institution,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = AppTheme.Spacing.sm)
                )
            }
        }
    }
}

data class TeamMember(
    val name: String,
    val role: String,
    val description: String,
    val imageRes: Int
)

data class ResearchMember(
    val name: String,
    val role: String,
    val institutions: List<String>
)

