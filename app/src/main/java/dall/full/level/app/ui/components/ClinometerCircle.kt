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
        // Círculo principal del clinómetro
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp) // Padding para que no toque los bordes
                .aspectRatio(1f) // Mantener proporción cuadrada
        ) {
            drawClinometerCircle(
                pitchAngle = animatedPitch, // Usamos Pitch como ángulo principal
                azimuthAngle = animatedAzimuth, // Para orientación de la aguja
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
    azimuthAngle: Float,
    mainAngle: Float,
    isLeveled: Boolean
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val radius = size.minDimension / 2 - 30.dp.toPx() // Un poco más de margen

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
        val isMainTick = i % 6 == 0 // Cada 90 grados (marcas principales)
        val isSecondaryTick = i % 2 == 0 // Cada 30 grados (marcas secundarias con números)

        val tickLength = when {
            isMainTick -> radius * 0.08f // Marcas principales proporcionales al radio
            isSecondaryTick -> radius * 0.05f // Marcas secundarias proporcionales
            else -> radius * 0.025f // Marcas pequeñas proporcionales
        }

        val tickWidth = when {
            isMainTick -> radius * 0.008f
            isSecondaryTick -> radius * 0.005f
            else -> radius * 0.003f
        }

        rotate(angle, Offset(centerX, centerY)) {
            drawLine(
                color = tickColor,
                start = Offset(centerX, centerY - radius),
                end = Offset(centerX, centerY - radius + tickLength),
                strokeWidth = tickWidth
            )
        }

        // Números en las marcas principales (cada 90°) y secundarias (cada 30°)
        if (isSecondaryTick) {
            val textRadius = radius - (radius * 0.12f) // Posición proporcional al radio
            val textX =
                centerX + cos(Math.toRadians((angle - 90).toDouble())).toFloat() * textRadius
            val textY =
                centerY + sin(Math.toRadians((angle - 90).toDouble())).toFloat() * textRadius

            // Tamaño de texto proporcional al radio
            val textSize = if (isMainTick) radius * 0.06f else radius * 0.045f
            val textColor =
                if (isMainTick) android.graphics.Color.BLACK else android.graphics.Color.GRAY

            // Para 0°, mostramos "0°", para otros ángulos el valor normal
            val displayText = if (angle == 0f) "0°" else "${angle.toInt()}°"

            drawContext.canvas.nativeCanvas.drawText(
                displayText,
                textX,
                textY + (radius * 0.02f), // Offset proporcional
                android.graphics.Paint().apply {
                    color = textColor
                    this.textSize = textSize
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = if (isMainTick) {
                        android.graphics.Typeface.DEFAULT_BOLD
                    } else {
                        android.graphics.Typeface.DEFAULT
                    }
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
        style = Stroke(width = radius * 0.005f) // Proporcional al radio
    )

    // Burbuja indicadora
    drawCircle(
        color = accentColor,
        radius = radius * 0.04f, // Proporcional al radio
        center = Offset(bubbleX, bubbleY)
    )

    // Punto central de referencia
    drawCircle(
        color = Color(0xFF424242),
        radius = radius * 0.015f, // Proporcional al radio
        center = Offset(centerX, centerY)
    )

    // Líneas de cruz para referencia - horizontal para rotación del teléfono
    drawLine(
        color = Color(0x50424242),
        start = Offset(centerX - radius * 0.8f, centerY),
        end = Offset(centerX + radius * 0.8f, centerY),
        strokeWidth = radius * 0.003f // Proporcional al radio
    )

    // AGUJA que apunta siempre hacia arriba en el mundo real (usando gravedad)
    val needleAngle = azimuthAngle // Usa orientación absoluta basada en gravedad
    val needleLength = radius * 0.75f
    val needleWidth = radius * 0.012f // Proporcional al radio

    // Calcular la posición de la punta de la aguja
    // La aguja apunta hacia "arriba" en el mundo real, sin importar cómo gires el teléfono
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
        radius = radius * 0.025f, // Proporcional al radio
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