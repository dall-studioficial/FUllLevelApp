package dall.full.level.app.ui.components

import android.graphics.Paint
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.geometry.Offset
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

internal fun normalizeNeedleAngle(angle: Float): Float =
    ((angle % 360f) + 360f) % 360f

/**
 * Dibuja la cara graduada del clinómetro con los colores del tema actual y superpone la aguja.
 *
 * Las medidas geométricas se expresan como proporciones del radio, no en dp:
 * así el dial conserva sus márgenes y proporciones al cambiar de tamaño.
 */
@Composable
fun ClinometerDial(
    // Ángulo de rotación de la aguja, recibido desde la lectura del sensor.
    needleAngle: Float,
    // El modificador permite al dial ocupar el espacio que determine su pantalla contenedora.
    modifier: Modifier = Modifier,
    // Gira únicamente la escala para mostrar la referencia horizontal o vertical.
    referenceAngle: Float = 0f,
    // El mini dial de la guía oculta los números para que sigan siendo legibles a tamaño pequeño.
    showLabels: Boolean = true
) {
    // Los colores proceden de la paleta elegida por el usuario (clara, oscura o personalizada).
    val colors = MaterialTheme.colorScheme

    // BoxWithConstraints permite usar un cuadrado incluso si el espacio disponible es rectangular.
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        // El dial usa el menor lado disponible, por lo que queda centrado y no se deforma.
        val dialSize = minOf(maxWidth, maxHeight)
        Box(modifier = Modifier.size(dialSize), contentAlignment = Alignment.Center) {
            // La cara, sus marcas y sus números giran juntos al cambiar la referencia.
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationZ = referenceAngle }
            ) {
                // Deja un margen exterior del 1 % del radio entre el borde del Canvas y el dial.
                val radius = min(size.width, size.height) * 0.49f
                // Centro geométrico desde el que se calculan círculos, marcas y etiquetas.
                val center = Offset(size.width / 2f, size.height / 2f)
                // Contorno y marcas usan el color legible del tema; la escala principal usa el acento.
                val outline = colors.onSurface.copy(alpha = 0.72f)
                val tickColor = colors.onSurface.copy(alpha = 0.88f)
                val accent = colors.primary

                // Fondo circular con degradado: centro claro y borde teñido con el color del tema.
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.surface,
                            colors.surface,
                            colors.surfaceContainerHigh,
                            colors.primaryContainer
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )
                // Borde exterior; 0.985 deja un pequeño margen para que el trazo no se recorte.
                drawCircle(
                    color = accent,
                    radius = radius * 0.985f,
                    center = center,
                    style = Stroke(width = radius * 0.025f)
                )
                // Segundo aro de acento colocado ligeramente hacia dentro del borde.
                drawCircle(
                    color = accent.copy(alpha = 0.85f),
                    radius = radius * 0.955f,
                    center = center,
                    style = Stroke(width = radius * 0.012f)
                )
                // Aro interior que separa visualmente la escala de grados de la escala porcentual.
                drawCircle(
                    color = outline.copy(alpha = 0.42f),
                    radius = radius * 0.49f,
                    center = center,
                    style = Stroke(width = radius * 0.006f)
                )

                // Una marca por cada grado alrededor del círculo completo (0° a 359°).
                for (degree in 0 until 360) {
                    // Android mide el ángulo desde el eje X; restar 90° coloca el cero arriba.
                    val angle = Math.toRadians((degree - 90).toDouble())
                    // Marca larga cada 45° y marca mediana cada 5°; el resto son marcas cortas.
                    val isMajor = degree % 45 == 0
                    val isMedium = degree % 5 == 0
                    // Longitud de la marca radial, proporcional al radio para adaptarse a cualquier tamaño.
                    val tickLength = when {
                        isMajor -> radius * 0.12f
                        isMedium -> radius * 0.075f
                        else -> radius * 0.035f
                    }
                    // Todas las marcas exteriores comienzan al 92 % del radio, dejando margen al borde.
                    val startRadius = radius * 0.92f
                    // El extremo interior se obtiene restando la longitud elegida para esa marca.
                    val endRadius = startRadius - tickLength
                    // Dibuja la marca desde el aro exterior hacia el centro usando coordenadas polares.
                    drawLine(
                        color = if (isMajor) accent else tickColor,
                        start = Offset(
                            center.x + cos(angle).toFloat() * startRadius,
                            center.y + sin(angle).toFloat() * startRadius
                        ),
                        end = Offset(
                            center.x + cos(angle).toFloat() * endRadius,
                            center.y + sin(angle).toFloat() * endRadius
                        ),
                        // Las marcas principales son más gruesas; las de 5° son intermedias.
                        strokeWidth = when {
                            isMajor -> radius * 0.012f
                            isMedium -> radius * 0.006f
                            else -> radius * 0.003f
                        }
                    )

                    // La escala interna tiene una marca cada 5° para dar una segunda referencia visual.
                    if (degree % 5 == 0) {
                        // Esta escala comienza al 52 % del radio, justo después del aro que la delimita.
                        val innerStart = radius * 0.52f
                        // Las marcas internas de 45° son más largas que las marcas intermedias.
                        val innerLength = if (degree % 45 == 0) radius * 0.065f else radius * 0.028f
                        // Traza la marca interna desde el borde de la escala hacia el centro.
                        drawLine(
                            color = if (degree % 45 == 0) accent.copy(alpha = 0.8f) else tickColor.copy(alpha = 0.75f),
                            start = Offset(
                                center.x + cos(angle).toFloat() * innerStart,
                                center.y + sin(angle).toFloat() * innerStart
                            ),
                            end = Offset(
                                center.x + cos(angle).toFloat() * (innerStart - innerLength),
                                center.y + sin(angle).toFloat() * (innerStart - innerLength)
                            ),
                            // Las marcas destacadas de la escala interna también reciben mayor grosor.
                            strokeWidth = if (degree % 45 == 0) radius * 0.008f else radius * 0.004f
                        )
                    }
                }

                // El mini dial omite textos porque no tendrían tamaño suficiente para leerse.
                if (showLabels) {
                    // Canvas nativo de Android para renderizar etiquetas de texto centradas.
                    val nativeCanvas = drawContext.canvas.nativeCanvas
                    // Estilo de las etiquetas angulares: color de texto y tamaño proporcional al dial.
                    val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = colors.onSurface.toArgb()
                        textAlign = Paint.Align.CENTER
                        textSize = radius * 0.09f
                        typeface = android.graphics.Typeface.create(
                            android.graphics.Typeface.DEFAULT,
                            android.graphics.Typeface.BOLD
                        )
                    }
                    // Estilo de las etiquetas Pitch: color primario y tamaño algo menor.
                    val pitchPaint = Paint(labelPaint).apply {
                        color = accent.toArgb()
                        textSize = radius * 0.065f
                    }
                    // Etiquetas de grados colocadas cada 45° alrededor del dial.
                    val majorLabels = listOf("0°", "45°", "90°", "45°", "0°", "45°", "90°", "45°")
                    // Referencias aproximadas de pendiente alineadas con los ocho puntos principales.
                    val pitchLabels = listOf("0%", "100%", "∞", "100%", "0%", "100%", "∞", "100%")

                    // Se dibujan ocho etiquetas, una cada 45°, comenzando arriba y siguiendo en sentido horario.
                    for (index in 0 until 8) {
                        // Conversión de la posición de cada etiqueta a un ángulo; -90° sitúa la primera arriba.
                        val angle = Math.toRadians((index * 45 - 90).toDouble())
                        // Las etiquetas de grados se colocan al 73 % del radio, entre las dos escalas.
                        val labelRadius = radius * 0.73f
                        val x = center.x + cos(angle).toFloat() * labelRadius
                        val y = center.y + sin(angle).toFloat() * labelRadius
                        // El ajuste con ascent/descent centra verticalmente el texto en su coordenada.
                        nativeCanvas.drawText(
                            majorLabels[index],
                            x,
                            y - (labelPaint.ascent() + labelPaint.descent()) / 2f,
                            labelPaint
                        )

                        // Las etiquetas de porcentaje siguen la escala interior, al 40,5 % del radio.
                        val pitchRadius = radius * 0.405f
                        val pitchX = center.x + cos(angle).toFloat() * pitchRadius
                        val pitchY = center.y + sin(angle).toFloat() * pitchRadius
                        nativeCanvas.drawText(
                            pitchLabels[index],
                            pitchX,
                            pitchY - (pitchPaint.ascent() + pitchPaint.descent()) / 2f,
                            pitchPaint
                        )
                    }
                }
            }

            // Aguja vectorial superpuesta; su giro es independiente de la rotación de la escala.
            Canvas(modifier = Modifier.fillMaxSize()) {
                // El pivote queda en el centro y la punta llega al 84 % del radio del dial.
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = min(size.width, size.height) * 0.49f
                // Mantiene la vuelta completa para que la punta siga el sentido real del giro.
                val normalizedAngle = normalizeNeedleAngle(needleAngle)

                rotate(degrees = normalizedAngle, pivot = center) {
                    // Punta superior y alas anchas para que el extremo que señala se distinga con facilidad.
                    val pointerPath = Path().apply {
                        moveTo(center.x, center.y - radius * 0.84f)
                        lineTo(center.x + radius * 0.105f, center.y - radius * 0.29f)
                        lineTo(center.x + radius * 0.035f, center.y - radius * 0.20f)
                        lineTo(center.x + radius * 0.035f, center.y + radius * 0.16f)
                        lineTo(center.x - radius * 0.035f, center.y + radius * 0.16f)
                        lineTo(center.x - radius * 0.035f, center.y - radius * 0.20f)
                        lineTo(center.x - radius * 0.105f, center.y - radius * 0.29f)
                        close()
                    }
                    // Cola inferior con la mitad de la longitud de la punta principal.
                    val tailPath = Path().apply {
                        moveTo(center.x, center.y + radius * 0.42f)
                        lineTo(center.x + radius * 0.055f, center.y + radius * 0.22f)
                        lineTo(center.x + radius * 0.035f, center.y + radius * 0.16f)
                        lineTo(center.x - radius * 0.035f, center.y + radius * 0.16f)
                        lineTo(center.x - radius * 0.055f, center.y + radius * 0.22f)
                        close()
                    }
                    // El contorno claro mantiene separada la aguja de marcas y números bajo ella.
                    drawPath(
                        path = pointerPath,
                        color = colors.surface,
                        style = Stroke(width = radius * 0.018f)
                    )
                    drawPath(
                        path = tailPath,
                        color = colors.surface,
                        style = Stroke(width = radius * 0.018f)
                    )
                    // La punta usa el color principal del tema; la cola usa el secundario.
                    drawPath(path = pointerPath, color = colors.primary)
                    drawPath(path = tailPath, color = colors.tertiary)

                    // Reflejo estrecho en la punta para dar profundidad sin cambiar la geometría de lectura.
                    val highlightPath = Path().apply {
                        moveTo(center.x, center.y - radius * 0.78f)
                        lineTo(center.x + radius * 0.018f, center.y - radius * 0.31f)
                        lineTo(center.x, center.y - radius * 0.22f)
                        close()
                    }
                    drawPath(path = highlightPath, color = colors.onPrimary.copy(alpha = 0.28f))

                    // El pivote cubre la unión de las dos mitades y marca el eje de rotación.
                    drawCircle(
                        color = colors.surface,
                        radius = radius * 0.045f,
                        center = center
                    )
                    drawCircle(
                        color = colors.primary,
                        radius = radius * 0.027f,
                        center = center
                    )
                }
            }
        }
    }
}
