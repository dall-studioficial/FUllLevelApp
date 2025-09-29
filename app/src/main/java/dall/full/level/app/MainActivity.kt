package dall.full.level.app

import android.os.Bundle
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dall.full.level.app.ui.theme.FUllLevelTheme
import dall.full.level.app.ui.navigation.AppNavHost
import dall.full.level.app.viewmodel.SettingsViewModel

/**
 * Activity principal que contiene la aplicación de clinómetro
 * Implementa el patrón MVVM delegando la lógica a la pantalla y ViewModel
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsVM: SettingsViewModel = viewModel(
                factory = androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory(application)
            )
            val darkTheme by settingsVM.darkTheme.collectAsState()
            val oledMode by settingsVM.oledMode.collectAsState()
            FUllLevelTheme(darkTheme = darkTheme, oledMode = oledMode) {
                val navController = rememberNavController()
                // Asegura iconos claros u oscuros en barra de status segun tema
                SideEffect {
                    val window = this@MainActivity.window
                    WindowCompat.getInsetsController(window, window.decorView)?.apply {
                        isAppearanceLightStatusBars = !darkTheme
                        isAppearanceLightNavigationBars = !darkTheme
                    }
                    window.statusBarColor = android.graphics.Color.TRANSPARENT
                    window.navigationBarColor = android.graphics.Color.TRANSPARENT
                }
                AppNavHost(navController, settingsVM)
            }
        }
    }
}