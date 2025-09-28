package dall.full.level.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dall.full.level.app.data.ClinometerData
import dall.full.level.app.R
import kotlin.math.*

/**
 * Componente circular del clinómetro que muestra la inclinación de forma visual
 */
@Composable
fun ClinometerCircle(
    clinometerData: ClinometerData,
    modifier: Modifier = Modifier
) {
    val animatedPitch by animateFloatAsState(
        targetValue = clinometerData.pitchAngle, // Usamos Pitch para rotación del teléfono
        animationSpec = tween(150),
        label = "pitch"
    )

    val animatedAzimuth by animateFloatAsState(
        targetValue = clinometerData.azimuthAngle, // Para compensar la orientación de la pantalla
        animationSpec = tween(150),
        label = "azimuth"
    )

    // Calcular el ángulo principal (absoluto para mostrar)
    val mainAngle = abs(animatedPitch)
    val isLeveled = mainAngle <= 2.0f

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Fondo: Transportador
        Image(
            painter = painterResource(id = R.drawable.dall_c),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        )

        // Aguja
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.minDimension / 2 - 30.dp.toPx()

            // --- Aguja (igual que antes, solo el dibujo de la aguja):
            val needleAngle =
                clinometerData.azimuthAngle // Usa orientación absoluta basada en gravedad
            val needleLength = radius * 0.75f
            val needleWidth = radius * 0.012f
            val needleRadians = Math.toRadians((needleAngle + 90).toDouble())
            val needleEndX = centerX + cos(needleRadians).toFloat() * needleLength
            val needleEndY = centerY + sin(needleRadians).toFloat() * needleLength
            // Línea de la aguja
            drawLine(
                color = if (isLeveled) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                start = Offset(centerX, centerY),
                end = Offset(needleEndX, needleEndY),
                strokeWidth = needleWidth,
                cap = StrokeCap.Round
            )
            // Círculo central para la base de la aguja
            drawCircle(
                color = if (isLeveled) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                radius = radius * 0.025f,
                center = Offset(centerX, centerY)
            )
        }

        // Indicador central rotado como antes
        Card(
            modifier = Modifier
                .graphicsLayer { rotationZ = animatedAzimuth },
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(16.dp)
                    .graphicsLayer {
                        rotationZ = 180f
                    }
            ) {
                Text(
                    text = "${mainAngle.toInt()}°",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLeveled) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                )
            }
        }
    }
}

/**
 * Componente que muestra un indicador de ángulo específico
 */
@Composable
private fun AngleIndicator(
    label: String,
    value: Float,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = color,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = "${value.toInt()}°",
            fontSize = 16.sp,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}