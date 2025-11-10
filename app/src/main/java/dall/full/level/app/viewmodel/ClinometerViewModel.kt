package dall.full.level.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dall.full.level.app.data.ClinometerData
import dall.full.level.app.repository.HapticFeedbackManager
import dall.full.level.app.repository.SensorRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.tan

/**
 * ViewModel para el clinómetro que maneja la lógica de presentación.
 */
class ClinometerViewModel(
    private val sensorRepository: SensorRepository,
    private val hapticManager: HapticFeedbackManager
) : ViewModel() {

    // --- Estados de la Interfaz de Usuario ---
    private val _clinometerState = MutableStateFlow(ClinometerData())
    val clinometerState: StateFlow<ClinometerData> = _clinometerState.asStateFlow()

    private val _isError = MutableStateFlow(false)
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _isRotatedReference = MutableStateFlow(false)
    val isRotatedReference: StateFlow<Boolean> = _isRotatedReference.asStateFlow()

    private val _showPitch = MutableStateFlow(false)
    val showPitch: StateFlow<Boolean> = _showPitch.asStateFlow()

    private val _isHeld = MutableStateFlow(false)
    val isHeld: StateFlow<Boolean> = _isHeld.asStateFlow()

    // --- Eventos para la Interfaz de Usuario (como vibraciones) ---
    sealed class UiEvent {
        object LevelVibration : UiEvent()
    }
    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    // Variables para controlar la vibración de nivelación
    private var wasLeveled = false
    private var wasVertical = false

    init {
        startSensorMonitoring()
    }

    private fun startSensorMonitoring() {
        if (!sensorRepository.areSensorsAvailable()) {
            _isError.value = true
            _errorMessage.value = "Los sensores necesarios no están disponibles en este dispositivo."
            return
        }

        viewModelScope.launch {
            sensorRepository.getClinometerData()
                .catch { exception ->
                    _isError.value = true
                    _errorMessage.value = "Error al leer sensores: ${exception.message}"
                }
                .collect { clinometerData ->
                    if (!_isHeld.value) {
                        val mappedAngle = mapAngleToTransportador(
                            clinometerData.pitchAngle,
                            _isRotatedReference.value
                        )

                        // Lógica de vibración
                        checkAndTriggerHapticFeedback(mappedAngle)

                        val pitchValue =
                            if (_showPitch.value) mapAngleToPitch(mappedAngle) else mappedAngle
                        _clinometerState.value = clinometerData.copy(pitchAngle = pitchValue)
                        _isError.value = false
                    }
                }
        }
    }

    private suspend fun checkAndTriggerHapticFeedback(angle: Float) {
        val isCurrentlyLeveled = angle <= 0.5f
        val isCurrentlyVertical = angle >= 89.5f

        // Vibra solo al cruzar el umbral para evitar vibraciones constantes.
        if (isCurrentlyLeveled && !wasLeveled) {
            _uiEvent.emit(UiEvent.LevelVibration)
        }
        if (isCurrentlyVertical && !wasVertical) {
            _uiEvent.emit(UiEvent.LevelVibration)
        }
        wasLeveled = isCurrentlyLeveled
        wasVertical = isCurrentlyVertical
    }

    fun resetSensors() {
        _isError.value = false
        _errorMessage.value = ""
        _isHeld.value = false
        startSensorMonitoring()
    }

    fun toggleReferenceMode() {
        if (_isHeld.value) return
        _isRotatedReference.value = !_isRotatedReference.value
        applyReferenceMode()
    }

    private fun mapAngleToTransportador(pitch: Float, rotated: Boolean): Float {
        val baseAngle = if (rotated) 90f - pitch else pitch
        val mapped = abs(baseAngle % 180f)
        return if (mapped > 90f) 180f - mapped else mapped
    }

    private fun applyReferenceMode() {
        val current = _clinometerState.value
        val mappedPitch = mapAngleToTransportador(current.pitchAngle, _isRotatedReference.value)
        val pitchValue = if (_showPitch.value) mapAngleToPitch(mappedPitch) else mappedPitch
        _clinometerState.value = current.copy(pitchAngle = pitchValue)
    }

    fun toggleShowPitch() {
        if (_isHeld.value) return
        _showPitch.value = !_showPitch.value
        applyReferenceMode()
    }

    private fun mapAngleToPitch(angle: Float): Float {
        if (angle >= 89.5f) return 999f
        val pitch = (12 * tan(Math.toRadians(angle.toDouble()))).toFloat()
        return if (pitch.isFinite()) pitch else 999f
    }

    fun toggleHold() {
        _isHeld.value = !_isHeld.value
        // Realiza una vibración de clic al presionar el botón
        hapticManager.performClickVibration()
    }
}