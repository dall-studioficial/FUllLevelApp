package dall.full.level.app.repository

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dall.full.level.app.data.ClinometerData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Repositorio para manejar los sensores del dispositivo
 * Proporciona datos de inclinación mediante Flow para reactividad
 */
class SensorRepository(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private var accelerometerValues = FloatArray(3)
    private var magnetometerValues = FloatArray(3)
    private var lastAccelerometerUpdate = 0L
    private var lastMagnetometerUpdate = 0L

    // Filtro de paso bajo para suavizar las lecturas
    private var lastFilteredPitch = 0f
    private val FILTER_ALPHA = 0.8f // Factor de suavizado (0.0 - 1.0)

    /**
     * Aplica un filtro de paso bajo para suavizar las lecturas del sensor
     */
    private fun applyLowPassFilter(newValue: Float): Float {
        lastFilteredPitch = FILTER_ALPHA * lastFilteredPitch + (1 - FILTER_ALPHA) * newValue
        return lastFilteredPitch
    }

    /**
     * Calibra el ángulo para mayor compatibilidad con otras apps
     */
    private fun calibrateAngle(rawAngle: Float): Float {
        // Aplicar corrección de offset si es necesario
        // Basado en pruebas con otras aplicaciones de clinómetro
        return rawAngle
    }

    /**
     * Obtiene un Flow continuo con los datos del clinómetro
     */
    fun getClinometerData(): Flow<ClinometerData> = callbackFlow {

        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        accelerometerValues = event.values.clone()
                        lastAccelerometerUpdate = System.currentTimeMillis()
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        magnetometerValues = event.values.clone()
                        lastMagnetometerUpdate = System.currentTimeMillis()
                    }
                }

                // Calcular orientación solo si tenemos datos recientes de ambos sensores
                if (lastAccelerometerUpdate != 0L && lastMagnetometerUpdate != 0L) {
                    val rotationMatrix = FloatArray(9)
                    val orientationAngles = FloatArray(3)

                    val success = SensorManager.getRotationMatrix(
                        rotationMatrix, null,
                        accelerometerValues, magnetometerValues
                    )

                    if (success) {
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)

                        // Método para obtener orientación absoluta hacia "arriba" (gravedad)
                        // Calculamos hacia dónde apunta "arriba" del teléfono en el mundo real
                        val x = accelerometerValues[0]
                        val y = accelerometerValues[1]
                        val z = accelerometerValues[2]

                        // Para rotación del teléfono (como ver video horizontal)
                        // Calculamos el Roll usando atan2
                        val rollRadians = atan2(x.toDouble(), y.toDouble())
                        var rollDegrees = Math.toDegrees(rollRadians).toFloat()

                        // Ajustar para que vertical = 0°, horizontal = ±90°
                        rollDegrees = when {
                            rollDegrees > 90 -> rollDegrees - 180
                            rollDegrees < -90 -> rollDegrees + 180
                            else -> rollDegrees
                        }

                        // ORIENTACIÓN ABSOLUTA: Calcular hacia dónde apunta "arriba" del teléfono
                        // usando el vector de gravedad para determinar la orientación real
                        val upDirectionRadians = atan2(-x.toDouble(), -y.toDouble())
                        val upDirectionDegrees = Math.toDegrees(upDirectionRadians).toFloat()

                        // Aplicar filtro de suavizado
                        val smoothedRoll = applyLowPassFilter(rollDegrees)
                        val calibratedRoll = calibrateAngle(smoothedRoll)

                        // Obtener azimut del cálculo original para la orientación absoluta
                        val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                        val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()

                        val clinometerData = ClinometerData(
                            pitchAngle = calibratedRoll, // Roll como ángulo principal
                            rollAngle = roll,
                            azimuthAngle = upDirectionDegrees, // Orientación absoluta hacia arriba
                            isCalibrated = true
                        )

                        trySend(clinometerData)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Manejar cambios de precisión si es necesario
            }
        }

        // Registrar los listeners de sensores
        accelerometer?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        }

        awaitClose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    /**
     * Verifica si los sensores necesarios están disponibles
     */
    fun areSensorsAvailable(): Boolean {
        return accelerometer != null && magnetometer != null
    }
}