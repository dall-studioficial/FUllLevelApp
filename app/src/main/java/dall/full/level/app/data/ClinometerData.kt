package dall.full.level.app.data

/**
 * Modelo de datos que representa la información del clinómetro
 * Contiene los ángulos de inclinación en diferentes ejes
 */
data class ClinometerData(
    val pitchAngle: Float = 0f,    // Ángulo de inclinación hacia adelante/atrás (en grados)
    val rollAngle: Float = 0f,     // Ángulo de inclinación hacia izquierda/derecha (en grados)
    val azimuthAngle: Float = 0f,  // Ángulo de orientación magnética (en grados)
    val isCalibrated: Boolean = false // Si el sensor está calibrado
)