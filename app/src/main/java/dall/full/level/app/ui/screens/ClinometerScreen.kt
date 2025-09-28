package dall.full.level.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
fun ClinometerScreen() {
    val context = LocalContext.current

    // Crear el repositorio y ViewModel
    val sensorRepository = remember { SensorRepository(context) }
    val viewModel = remember { ClinometerViewModel(sensorRepository) }

    // Observar el estado desde el ViewModel
    val clinometerData by viewModel.clinometerState.collectAsStateWithLifecycle()
    val isError by viewModel.isError.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A1A),
                        Color(0xFF2D2D2D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra superior con título y estado
            TopAppBar(
                title = {
                    Text(
                        "Clinómetro",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    // Indicador de estado
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (clinometerData.isCalibrated)
                                Color(0xFF4CAF50) else Color(0xFFFF9800)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = if (clinometerData.isCalibrated) "CALIBRADO" else "CALIBRANDO...",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

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
                        .fillMaxWidth()
                )
            }
        }
    }
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
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD32F2F)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White
                )
            ) {
                Text(
                    text = "Reintentar",
                    color = Color(0xFFD32F2F)
                )
            }
        }
    }
}
