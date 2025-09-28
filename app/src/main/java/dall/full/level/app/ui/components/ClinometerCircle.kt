package dall.full.level.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dall.full.level.app.R
import dall.full.level.app.data.ClinometerData
import kotlin.math.abs

/**
 * Componente circular del clinómetro que muestra la inclinación de forma visual
 */
@Composable
fun ClinometerCircle(
    clinometerData: ClinometerData,
    modifier: Modifier = Modifier,
    isRotatedReference: Boolean = false
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
                .graphicsLayer {
                    rotationZ = if (isRotatedReference) 90f else 0f
                }
        )

        // Aguja personalizada como imagen
        Image(
            painter = painterResource(id = R.drawable.dall_a),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = +animatedAzimuth
                }
        )

        // Indicador central rotado como antes
        Card(
            modifier = Modifier
                .graphicsLayer { rotationZ = +animatedAzimuth },
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.9f)
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(start = 35.dp, top = 10.dp, end = 35.dp, bottom = 10.dp)
                    .graphicsLayer { rotationZ = 180f }
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