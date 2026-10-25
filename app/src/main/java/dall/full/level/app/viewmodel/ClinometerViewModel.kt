package dall.full.level.app.viewmodel

import android.app.Activity
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dall.full.level.app.data.ClinometerData
import dall.full.level.app.repository.AdManager
import dall.full.level.app.repository.AudioFeedbackManager
import dall.full.level.app.repository.HapticFeedbackManager
import dall.full.level.app.repository.SensorRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.tan

/**
 * ViewModel para el clinómetro que maneja la lógica de presentación, calibración, audio asistido y anuncios PRO.
 */
@HiltViewModel
class ClinometerViewModel @Inject constructor(
    private val application: Application,
    private val dataStore: DataStore<Preferences>,
    private val sensorRepository: SensorRepository,
    private val hapticManager: HapticFeedbackManager,
    private val audioManager: AudioFeedbackManager,
    private val adManager: AdManager
) : ViewModel() {

    private val pitchOffsetKey = floatPreferencesKey("pitch_offset")
    private val rollOffsetKey = floatPreferencesKey("roll_offset")
    private val beepEnabledKey = booleanPreferencesKey("beep_enabled")
    private val ttsEnabledKey = booleanPreferencesKey("tts_enabled")

    // Offsets de calibración guardados
    private val _pitchOffset = MutableStateFlow(0f)
    val pitchOffset: StateFlow<Float> = _pitchOffset.asStateFlow()

    private val _rollOffset = MutableStateFlow(0f)
    val rollOffset: StateFlow<Float> = _rollOffset.asStateFlow()

    // Configuración independiente de audio y voz
    private val _isBeepEnabled = MutableStateFlow(true)
    val isBeepEnabled: StateFlow<Boolean> = _isBeepEnabled.asStateFlow()

    private val _isTtsEnabled = MutableStateFlow(true)
    val isTtsEnabled: StateFlow<Boolean> = _isTtsEnabled.asStateFlow()

    // Datos crudos del sensor
    private val _rawClinometerData = MutableStateFlow(ClinometerData())

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
        loadCalibrationAndAudioPreferences()
        startSensorMonitoring()
    }

    private fun loadCalibrationAndAudioPreferences() {
        viewModelScope.launch {
            dataStore.data
                .map { prefs ->
                    Tuple4(
                        prefs[pitchOffsetKey] ?: 0f,
                        prefs[rollOffsetKey] ?: 0f,
                        prefs[beepEnabledKey] ?: true,
                        prefs[ttsEnabledKey] ?: true
                    )
                }
                .collect { tuple ->
                    _pitchOffset.value = tuple.v1
                    _rollOffset.value = tuple.v2
                    _isBeepEnabled.value = tuple.v3
                    _isTtsEnabled.value = tuple.v4
                }
        }
    }

    private data class Tuple4<A, B, C, D>(
        val v1: A, val v2: B, val v3: C, val v4: D
    )

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
                .collect { rawData ->
                    _rawClinometerData.value = rawData
                    if (!_isHeld.value) {
                        // Aplicar calibración de offset
                        val calibratedPitch = rawData.pitchAngle - _pitchOffset.value
                        val calibratedRoll = rawData.rollAngle - _rollOffset.value

                        val mappedAngle = mapAngleToTransportador(
                            calibratedPitch,
                            _isRotatedReference.value
                        )

                        // Lógica de vibración
                        checkAndTriggerHapticFeedback(mappedAngle)

                        // Pitidos de proximidad tipo radar
                        audioManager.playProximityBeep(mappedAngle, _isBeepEnabled.value)

                        val pitchValue =
                            if (_showPitch.value) mapAngleToPitch(mappedAngle) else mappedAngle

                        _clinometerState.value = rawData.copy(
                            pitchAngle = pitchValue,
                            rollAngle = calibratedRoll,
                            azimuthAngle = rawData.azimuthAngle - _pitchOffset.value
                        )
                        _isError.value = false
                    }
                }
        }
    }

    /**
     * Pre-carga de anuncios diferida en el hilo principal tras 2 segundos para evitar bloquear eventos de toque (ANR).
     */
    fun preloadAds() {
        viewModelScope.launch(Dispatchers.Main) {
            delay(2000) // Esperar 2 segundos después del arranque completo
            adManager.loadInterstitialAd()
            adManager.loadRewardedAd()
        }
    }

    /**
     * Muestra un Anuncio Intersticial respetando la frecuencia de AdMob.
     */
    fun showInterstitialAd(activity: Activity?, onDismissed: () -> Unit = {}) {
        adManager.showInterstitialAdWithCap(activity, onDismissed)
    }

    /**
     * Muestra un Anuncio Recompensado y ejecuta la recompensa si el usuario ve el video completo.
     */
    fun showRewardedAd(activity: Activity?, onRewardGranted: () -> Unit) {
        adManager.showRewardedAd(activity, onRewardGranted)
    }

    fun toggleBeepEnabled() {
        val newValue = !_isBeepEnabled.value
        _isBeepEnabled.value = newValue
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[beepEnabledKey] = newValue
            }
        }
    }

    fun toggleTtsEnabled() {
        val newValue = !_isTtsEnabled.value
        _isTtsEnabled.value = newValue
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ttsEnabledKey] = newValue
            }
        }
        if (!newValue) {
            audioManager.stop()
        }
    }

    /**
     * Lee la medición actual en voz alta usando Text-To-Speech.
     */
    fun speakCurrentMeasurement() {
        val current = _clinometerState.value
        val angle = if (_showPitch.value) current.pitchAngle.roundToInt().toFloat() else current.pitchAngle
        audioManager.speakAngle(angle, _showPitch.value, true)
    }

    fun calibrateCurrentZero() {
        val rawData = _rawClinometerData.value
        val newPitchOffset = rawData.pitchAngle
        val newRollOffset = rawData.rollAngle

        _pitchOffset.value = newPitchOffset
        _rollOffset.value = newRollOffset

        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[pitchOffsetKey] = newPitchOffset
                prefs[rollOffsetKey] = newRollOffset
            }
        }
        hapticManager.performClickVibration()
    }

    fun resetCalibration() {
        _pitchOffset.value = 0f
        _rollOffset.value = 0f

        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[pitchOffsetKey] = 0f
                prefs[rollOffsetKey] = 0f
            }
        }
        hapticManager.performClickVibration()
    }

    private suspend fun checkAndTriggerHapticFeedback(angle: Float) {
        val isCurrentlyLeveled = abs(angle) <= 0.5f
        val isCurrentlyVertical = abs(angle) >= 89.5f

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
        val willBeHeld = !_isHeld.value
        _isHeld.value = willBeHeld
        hapticManager.performClickVibration()

        if (willBeHeld && _isTtsEnabled.value) {
            speakCurrentMeasurement()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
