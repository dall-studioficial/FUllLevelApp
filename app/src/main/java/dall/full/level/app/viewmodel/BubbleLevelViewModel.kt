package dall.full.level.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dall.full.level.app.repository.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BubbleLevelViewModel(private val sensorRepository: SensorRepository) : ViewModel() {

    private val _bubbleLevelState = MutableStateFlow(BubbleLevelData())
    val bubbleLevelState: StateFlow<BubbleLevelData> = _bubbleLevelState.asStateFlow()

    // Almacenará el primer valor de 'pitch' como el punto de referencia "cero"
    private var pitchOffset: Float? = null

    init {
        viewModelScope.launch {
            sensorRepository.getClinometerData().collect { clinometerData ->
                // La primera vez que recibimos un dato del sensor, lo usamos como nuestro "cero"
                if (pitchOffset == null) {
                    pitchOffset = clinometerData.pitchAngle
                }

                // Restamos el offset para que la inclinación inicial en horizontal sea 0
                val calibratedPitch = clinometerData.pitchAngle - (pitchOffset ?: 0f)

                _bubbleLevelState.value = BubbleLevelData(
                    pitch = calibratedPitch,
                    roll = clinometerData.rollAngle // Mantenemos el roll por si acaso
                )
            }
        }
    }
}

data class BubbleLevelData(
    val pitch: Float = 0f,
    val roll: Float = 0f
)
