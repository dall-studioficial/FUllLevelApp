package dall.full.level.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dall.full.level.app.data.ClinometerData
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
        targetValue = clinometerData.pitchAngle, // Usamos Pitch para inclinación vertical
        animationSpec = tween(150),
        label = "pitch"
    )

    // Calcular el ángulo principal (absoluto para mostrar)
    val mainAngle = abs(animatedPitch)
    val isLeveled = mainAngle <= 2.0f

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Círculo principal del clinómetro
        Canvas(
            modifier = Modifier
                .size(320.dp)
                .clip(CircleShape)
        ) {
            drawClinometerCircle(
                pitchAngle = animatedPitch, // Usamos Pitch como ángulo principal
                mainAngle = mainAngle,
                isLeveled = isLeveled
            )
        }

        // Indicador central con el valor del ángulo
        Card(
            modifier = Modifier.offset(y = (-20).dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "${mainAngle.toInt()}°",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLeveled) Color(0xFF2E7D32) else Color(0xFFD32F2F) // Verde oscuro o rojo oscuro
                )

                Text(
                    text = if (isLeveled) "NIVELADO" else "INCLINADO",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isLeveled) Color(0xFF2E7D32) else Color(0xFFE65100), // Verde oscuro o naranja oscuro
                    textAlign = TextAlign.Center
                )
            }
        }

        // Indicadores de valores secundarios
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AngleIndicator(
                    label = "Pitch",
                    value = animatedPitch,
                    color = Color(0xFFFF5722)
                )
            }
        }
    }
}

/**
 * Dibuja el círculo principal del clinómetro con todas las marcas y indicadores
 */
private fun DrawScope.drawClinometerCircle(
    pitchAngle: Float,
    mainAngle: Float,
    isLeveled: Boolean
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = size.minDimension / 2 - 20.dp.toPx()

    // Colores
    val backgroundColor = Color(0xFFE8E8E8)
    val borderColor = Color(0xFF424242)
    val accentColor = if (isLeveled) Color(0xFF4CAF50) else Color(0xFFFF5722)
    val tickColor = Color(0xFF757575)

    // Fondo del círculo
    drawCircle(
        color = backgroundColor,
        radius = radius,
        center = Offset(centerX, centerY)
    )

    // Borde exterior
    drawCircle(
        color = borderColor,
        radius = radius,
        center = Offset(centerX, centerY),
        style = Stroke(width = 4.dp.toPx())
    )

    // Marcas de grados (cada 15 grados)
    for (i in 0 until 24) {
        val angle = i * 15f
        val isMainTick = i % 6 == 0 // Cada 90 grados
        val tickLength = if (isMainTick) 20.dp.toPx() else 10.dp.toPx()
        val tickWidth = if (isMainTick) 3.dp.toPx() else 1.dp.toPx()

        rotate(angle, Offset(centerX, centerY)) {
            drawLine(
                color = tickColor,
                start = Offset(centerX, centerY - radius),
                end = Offset(centerX, centerY - radius + tickLength),
                strokeWidth = tickWidth
            )
        }

        // Números en las marcas principales
        if (isMainTick && angle != 0f) {
            val textRadius = radius - 35.dp.toPx()
            val textX =
                centerX + cos(Math.toRadians((angle - 90).toDouble())).toFloat() * textRadius
            val textY =
                centerY + sin(Math.toRadians((angle - 90).toDouble())).toFloat() * textRadius

            drawContext.canvas.nativeCanvas.drawText(
                "${angle.toInt()}°",
                textX,
                textY + 5.dp.toPx(),
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 12.sp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }

    // Nivel de burbuja (indicador de rotación)
    val bubbleRadius = radius * 0.7f
    val bubbleX = centerX + (pitchAngle / 45f) * bubbleRadius * 0.8f
    val bubbleY = centerY

    // Círculo de referencia para la burbuja
    drawCircle(
        color = Color(0x30000000),
        radius = bubbleRadius,
        center = Offset(centerX, centerY),
        style = Stroke(width = 2.dp.toPx())
    )

    // Burbuja indicadora
    drawCircle(
        color = accentColor,
        radius = 12.dp.toPx(),
        center = Offset(bubbleX, bubbleY)
    )

    // Punto central de referencia
    drawCircle(
        color = Color(0xFF424242),
        radius = 4.dp.toPx(),
        center = Offset(centerX, centerY)
    )

    // Líneas de cruz para referencia - horizontal para rotación del teléfono
    drawLine(
        color = Color(0x50424242),
        start = Offset(centerX - radius * 0.8f, centerY),
        end = Offset(centerX + radius * 0.8f, centerY),
        strokeWidth = 1.dp.toPx()
    )

    // AGUJA que apunta al ángulo actual
    val needleAngle = pitchAngle // El ángulo de la aguja basado en Pitch
    val needleLength = radius * 0.75f
    val needleWidth = 4.dp.toPx()

    // Calcular la posición de la punta de la aguja
    // Convertir ángulo a radianes y ajustar para que 0° esté arriba
    val needleRadians = Math.toRadians((needleAngle + 90).toDouble())
    val needleEndX = centerX + cos(needleRadians).toFloat() * needleLength
    val needleEndY = centerY + sin(needleRadians).toFloat() * needleLength

    // Dibujar la aguja
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
        radius = 8.dp.toPx(),
        center = Offset(centerX, centerY)
    )
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