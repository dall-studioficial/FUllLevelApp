package dall.full.level.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dall.full.level.app.repository.HapticFeedbackManager
import dall.full.level.app.repository.SensorRepository
import dall.full.level.app.ui.components.ClinometerCircle
import dall.full.level.app.viewmodel.ClinometerViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Pantalla principal del clinómetro que muestra toda la interfaz de usuario.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinometerScreen(
    onInfoClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Crear instancias de las dependencias
    val hapticManager = remember { HapticFeedbackManager(context) }
    val viewModel = remember {
        ClinometerViewModel(SensorRepository(context), hapticManager)
    }

    // Observar estados del ViewModel
    val clinometerData by viewModel.clinometerState.collectAsStateWithLifecycle()
    val isError by viewModel.isError.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isRotatedReference by viewModel.isRotatedReference.collectAsStateWithLifecycle()
    val showPitch by viewModel.showPitch.collectAsState()
    val isHeld by viewModel.isHeld.collectAsState()

    // --- Efectos Secundarios ---
    // Escucha eventos de una sola vez del ViewModel, como las vibraciones.
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ClinometerViewModel.UiEvent.LevelVibration -> {
                    hapticManager.performLevelVibration()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clinómetro", fontSize = 24.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Filled.Settings, "Ajustes") }
                    IconButton(onClick = onInfoClick) { Icon(Icons.Filled.Info, "Info") }
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
                // Controles superiores
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Card(shape = RoundedCornerShape(12.dp)) {
                        Text(
                            text = if (clinometerData.isCalibrated) "CALIBRADO" else "CALIBRANDO...",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Button(
                        onClick = { viewModel.toggleReferenceMode() },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        enabled = !isHeld
                    ) {
                        Text(text = if (isRotatedReference) "Ref: 90°" else "Ref: 0°", fontSize = 12.sp)
                    }
                }

                // Tabs
                PrimaryTabRow(selectedTabIndex = if (showPitch) 1 else 0) {
                    Tab(
                        selected = !showPitch,
                        onClick = { if (showPitch) viewModel.toggleShowPitch() },
                        text = { Text("Grados", fontSize = 14.sp) },
                        icon = { Icon(Icons.Outlined.Straighten, contentDescription = "Grados") },
                        enabled = !isHeld
                    )
                    Tab(
                        selected = showPitch,
                        onClick = { if (!showPitch) viewModel.toggleShowPitch() },
                        text = { Text("Pitch (X/12)", fontSize = 14.sp) },
                        icon = { Icon(Icons.Filled.Roofing, contentDescription = "Pitch") },
                        enabled = !isHeld
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Círculo o Error
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (isError) {
                        ErrorCard(errorMessage) { viewModel.resetSensors() }
                    } else {
                        ClinometerCircle(clinometerData, Modifier.fillMaxWidth(), isRotatedReference, showPitch)
                    }
                }

                // Botón Congelar/Reanudar
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.toggleHold() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(if (isHeld) Icons.Default.PlayArrow else Icons.Default.Pause, if (isHeld) "Reanudar" else "Congelar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isHeld) "Reanudar" else "Congelar", fontSize = 16.sp)
                }
            }
        }
    )
}

@Composable
private fun ErrorCard(message: String, onRetry: () -> Unit) {
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
            Text("Error", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text("Reintentar") }
        }
    }
}