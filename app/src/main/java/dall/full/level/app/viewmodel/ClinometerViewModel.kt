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

    // Nuevo estado: indica si la referencia está girada (0°=vertical o 90°=vertical)
    private val _isRotatedReference = MutableStateFlow(false)
    val isRotatedReference: StateFlow<Boolean> = _isRotatedReference.asStateFlow()

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
                    val mappedPitch = mapAngleToTransportador(
                        clinometerData.pitchAngle,
                        _isRotatedReference.value
                    )
                    _clinometerState.value = clinometerData.copy(pitchAngle = mappedPitch)
                    _isError.value = false
                }
        }
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
     * Alterna el modo de referencia del transportador
     */
    fun toggleReferenceMode() {
        _isRotatedReference.value = !_isRotatedReference.value
        // Al cambiar el modo, actualizamos el valor mostrado
        applyReferenceMode()
    }

    /**
     * Mapea el ángulo para que siempre esté en el rango correcto de 0°-90° en el transportador
     */
    private fun mapAngleToTransportador(pitch: Float, rotated: Boolean): Float {
        val baseAngle = if (rotated) 90f - pitch else pitch
        val mapped = kotlin.math.abs(baseAngle % 180f)
        return if (mapped > 90f) 180f - mapped else mapped
    }

    // Ajusta el ángulo según el modo de referencia
    private fun applyReferenceMode() {
        val current = _clinometerState.value
        val mappedPitch = mapAngleToTransportador(current.pitchAngle, _isRotatedReference.value)
        _clinometerState.value = current.copy(pitchAngle = mappedPitch)
    }

}