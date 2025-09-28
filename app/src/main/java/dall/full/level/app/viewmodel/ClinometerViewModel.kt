package dall.full.level.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dall.full.level.app.data.ClinometerData
import dall.full.level.app.repository.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * ViewModel para el clinómetro que maneja la lógica de presentación
 * Sigue el patrón MVVM separando la lógica de negocio de la UI
 */
class ClinometerViewModel(
    private val sensorRepository: SensorRepository
) : ViewModel() {

    private val _clinometerState = MutableStateFlow(ClinometerData())
    val clinometerState: StateFlow<ClinometerData> = _clinometerState.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    // Offset de calibración manual
    private val _calibrationOffset = MutableStateFlow(0f)
    val calibrationOffset: StateFlow<Float> = _calibrationOffset.asStateFlow()

    init {
        startSensorMonitoring()
    }

    /**
     * Inicia el monitoreo de sensores
     */
    private fun startSensorMonitoring() {
        if (!sensorRepository.areSensorsAvailable()) {
            _isError.value = true
            _errorMessage.value = "Los sensores necesarios no están disponibles en este dispositivo"
            return
        }

        viewModelScope.launch {
            sensorRepository.getClinometerData()
                .catch { exception ->
                    _isError.value = true
                    _errorMessage.value = "Error al leer sensores: ${exception.message}"
                }
                .collect { clinometerData ->
                    _clinometerState.value = clinometerData
                    _isError.value = false
                }
        }
    }

    /**
     * Ajusta el offset de calibración
     */
    fun adjustCalibration(offset: Float) {
        _calibrationOffset.value = offset
    }

    /**
     * Resetea la calibración a 0
     */
    fun resetCalibration() {
        _calibrationOffset.value = 0f
    }

    /**
     * Reinicia el monitoreo de sensores
     */
    fun resetSensors() {
        _isError.value = false
        _errorMessage.value = ""
        startSensorMonitoring()
    }

    /**
     * Obtiene el ángulo principal para mostrar (Roll para rotación del teléfono)
     */
    fun getMainAngle(): Float {
        val state = _clinometerState.value
        val calibratedAngle = state.pitchAngle + _calibrationOffset.value
        // Para nivel vertical, usamos principalmente Pitch
        return kotlin.math.abs(calibratedAngle)
    }

    /**
     * Obtiene el ángulo con signo para la aguja (positivo/negativo) - rotación del teléfono
     */
    fun getSignedAngle(): Float {
        val state = _clinometerState.value
        return state.pitchAngle + _calibrationOffset.value
    }

    /**
     * Determina si el dispositivo está nivelado (dentro de un margen de tolerancia).
     * Usa el ángulo calibrado (Roll + offset), no el valor absoluto.
     */
    fun isLeveled(tolerance: Float = 2.0f): Boolean {
        val angle = getSignedAngle()
        return angle >= -tolerance && angle <= tolerance
    }
}