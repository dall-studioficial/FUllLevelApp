package dall.full.level.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dall.full.level.app.R
import dall.full.level.app.ui.components.BannerAdView
import dall.full.level.app.ui.components.ClinometerCircle
import dall.full.level.app.viewmodel.ClinometerViewModel
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale
import kotlin.math.abs

/**
 * Pantalla principal del clinómetro que muestra toda la interfaz de usuario con calibración y asistente de voz TTS.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinometerScreen(
    onInfoClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onCameraClick: () -> Unit = {},
    viewModel: ClinometerViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Observar estados del ViewModel
    val clinometerData by viewModel.clinometerState.collectAsStateWithLifecycle()
    val isError by viewModel.isError.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val isRotatedReference by viewModel.isRotatedReference.collectAsStateWithLifecycle()
    val showPitch by viewModel.showPitch.collectAsState()
    val isHeld by viewModel.isHeld.collectAsState()
    val isBeepEnabled by viewModel.isBeepEnabled.collectAsState()
    val pitchOffset by viewModel.pitchOffset.collectAsState()
    val rollOffset by viewModel.rollOffset.collectAsState()

    var showCalibrationDialog by remember { mutableStateOf(false) }
    val isCustomCalibrated = abs(pitchOffset) > 0.01f || abs(rollOffset) > 0.01f

    // --- Efectos Secundarios ---
    LaunchedEffect(Unit) {
        viewModel.preloadAds()
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ClinometerViewModel.UiEvent.LevelVibration -> {
                    // Manejado internamente
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                actions = {
                    // Botón Lectura por Voz
                    IconButton(onClick = { viewModel.speakCurrentMeasurement() }) {
                        Icon(Icons.Filled.RecordVoiceOver, contentDescription = "Leer Ángulo")
                    }
                    // Botón Silenciar / Activar Pitidos de Audio Proximidad
                    IconButton(onClick = { viewModel.toggleBeepEnabled() }) {
                        Icon(
                            imageVector = if (isBeepEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                            contentDescription = "Pitidos de Audio",
                            tint = if (isBeepEnabled) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    // Botón Calibrar
                    TextButton(
                        onClick = { showCalibrationDialog = true },
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.calibrate),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCustomCalibrated) MaterialTheme.colorScheme.primary else LocalContentColor.current
                        )
                    }
                    IconButton(onClick = onCameraClick) { Icon(Icons.Filled.CameraAlt, stringResource(R.string.camera)) }
                    IconButton(onClick = onSettingsClick) { Icon(Icons.Filled.Settings, stringResource(R.string.settings)) }
                    IconButton(onClick = onInfoClick) { Icon(Icons.Filled.Info, stringResource(R.string.info)) }
                }
            )
        },
        bottomBar = {
            // Banner de anuncios adaptativo
            BannerAdView()
        },
        content = { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Controles superiores
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCustomCalibrated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Text(
                                text = if (isCustomCalibrated) stringResource(R.string.calibrated_pro) else stringResource(R.string.calibrated),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isCustomCalibrated) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = { viewModel.toggleReferenceMode() },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            enabled = !isHeld
                        ) {
                            Text(text = if (isRotatedReference) stringResource(R.string.ref_90) else stringResource(R.string.ref_0), fontSize = 12.sp)
                        }
                    }

                    // Tabs
                    PrimaryTabRow(selectedTabIndex = if (showPitch) 1 else 0) {
                        Tab(
                            selected = !showPitch,
                            onClick = { if (showPitch) viewModel.toggleShowPitch() },
                            text = { Text(stringResource(R.string.degrees), fontSize = 14.sp) },
                            icon = { Icon(Icons.Outlined.Straighten, contentDescription = stringResource(R.string.degrees)) },
                            enabled = !isHeld
                        )
                        Tab(
                            selected = showPitch,
                            onClick = { if (!showPitch) viewModel.toggleShowPitch() },
                            text = { Text(stringResource(R.string.pitch), fontSize = 14.sp) },
                            icon = { Icon(Icons.Filled.Roofing, contentDescription = stringResource(R.string.pitch)) },
                            enabled = !isHeld
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

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
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.toggleHold() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(if (isHeld) Icons.Default.PlayArrow else Icons.Default.Pause, if (isHeld) stringResource(R.string.resume) else stringResource(R.string.freeze))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isHeld) stringResource(R.string.resume) else stringResource(R.string.freeze), fontSize = 16.sp)
                    }
                }

                // Diálogo de Calibración Semitransparente directamente sobre el Dial en tiempo real
                if (showCalibrationDialog) {
                    AlertDialog(
                        onDismissRequest = { showCalibrationDialog = false },
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(stringResource(R.string.calibration_mode), fontWeight = FontWeight.Bold)
                            }
                        },
                        text = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.calibration_step_1),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = stringResource(R.string.calibration_step_2),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (isCustomCalibrated) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = String.format(Locale.getDefault(), "Offset activo: Pitch %.2f° | Roll %.2f°", pitchOffset, rollOffset),
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(8.dp),
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {
                                    viewModel.calibrateCurrentZero()
                                    showCalibrationDialog = false
                                    Toast.makeText(context, context.getString(R.string.calibration_success_toast), Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text(stringResource(R.string.calibrate_zero))
                            }
                        },
                        dismissButton = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (isCustomCalibrated) {
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.resetCalibration()
                                            showCalibrationDialog = false
                                            Toast.makeText(context, context.getString(R.string.calibration_reset_toast), Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Text(stringResource(R.string.reset))
                                    }
                                }
                                TextButton(onClick = { showCalibrationDialog = false }) {
                                    Text(stringResource(R.string.cancel))
                                }
                            }
                        }
                    )
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
            Text(stringResource(R.string.error), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(message, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text(stringResource(R.string.retry)) }
        }
    }
}
