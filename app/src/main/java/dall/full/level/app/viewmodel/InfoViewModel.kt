package dall.full.level.app.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel para la pantalla de información de la App
 * Sigue el patrón MVVM permitiendo expansión futura
 */
@HiltViewModel
class InfoViewModel @Inject constructor() : ViewModel() {
    // Aquí puedes agregar lógica de UI/info, como cargar versión, links, etc.
}
