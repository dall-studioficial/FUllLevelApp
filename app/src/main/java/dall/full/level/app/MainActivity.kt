package dall.full.level.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dall.full.level.app.ui.screens.ClinometerScreen
import dall.full.level.app.ui.theme.FUllLevelTheme

/**
 * Activity principal que contiene la aplicación de clinómetro
 * Implementa el patrón MVVM delegando la lógica a la pantalla y ViewModel
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FUllLevelTheme {
                ClinometerScreen()
            }
        }
    }
}