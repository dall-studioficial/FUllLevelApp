package dall.full.level.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dall.full.level.app.repository.SensorRepository
import dall.full.level.app.ui.components.ClinometerCircle
import dall.full.level.app.viewmodel.ClinometerViewModel

/**
 * Pantalla principal del clinómetro que muestra toda la interfaz de usuario
 * Implementa el patrón MVVM usando el ViewModel para manejar la lógica
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinometerScreen(
    onInfoClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Crear el repositorio y ViewModel
    val sensorRepository = remember { SensorRepository(context) }
    val viewModel = remember { ClinometerViewModel(sensorRepository) }

    // Observar el estado desde el ViewModel
    val clinometerData by viewModel.clinometerState.collectAsStateWithLifecycle()
    val isError by viewModel.isError.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isRotatedReference by viewModel.isRotatedReference.collectAsStateWithLifecycle()
    val showPitch by viewModel.showPitch.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Clinómetro",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Ajustes"
                        )
                    }
                    IconButton(onClick = onInfoClick) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Info"
                        )
                    }
                }
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Indicador de estado movido aquí
                Card(
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (clinometerData.isCalibrated) "CALIBRADO" else "CALIBRANDO...",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { viewModel.toggleReferenceMode() },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isRotatedReference) "90° arriba" else "0° arriba",
                        fontSize = 12.sp
                    )
                }
                // Tabs para alternar Grados/Pitch
                val tabTitles = listOf("Grados", "Pitch (X/12)")
                val tabIcons = listOf(Icons.Outlined.Straighten, Icons.Filled.Roofing)
                val selectedTabIndex = if (showPitch) 1 else 0

                Spacer(modifier = Modifier.height(8.dp))
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = {
                                if (selectedTabIndex != index) {
                                    viewModel.toggleShowPitch()
                                }
                            },
                            text = { Text(text = title, fontSize = 14.sp) },
                            icon = { Icon(imageVector = tabIcons[index], contentDescription = title) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Manejo de errores
                if (isError) {
                    ErrorCard(
                        message = errorMessage,
                        onRetry = { viewModel.resetSensors() }
                    )
                } else {
                    // Componente principal del clinómetro
                    ClinometerCircle(
                        clinometerData = clinometerData,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        isRotatedReference = isRotatedReference,
                        showPitch = showPitch
                    )
                }
            }
        }
    )
}

/**
 * Componente que muestra información de error con opción de reintentar
 */
@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry
            ) {
                Text(
                    text = "Reintentar"
                )
            }
        }
    }
}