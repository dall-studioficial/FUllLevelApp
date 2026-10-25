package dall.full.level.app

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dall.full.level.app.ui.theme.FUllLevelTheme
import dall.full.level.app.ui.navigation.AppNavHost
import dall.full.level.app.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal que contiene la aplicación de clinómetro.
 * Hereda de AppCompatActivity para dar soporte nativo a AppCompatDelegate e in-app language picker en Android 13+.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val settingsVM: SettingsViewModel by viewModels()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Bloquear la rotación de la pantalla a vertical
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
        enableEdgeToEdge()
        setContent {
            val followSystemTheme by settingsVM.followSystemTheme.collectAsState()
            val darkThemeByUser by settingsVM.darkTheme.collectAsState()
            val oledMode by settingsVM.oledMode.collectAsState()
            val palette by settingsVM.themePalette.collectAsState()

            // Aplico lógica según prefiero automático/manual
            val darkTheme = if (followSystemTheme) isSystemInDarkTheme() else darkThemeByUser
            FUllLevelTheme(
                darkTheme = darkTheme,
                oledMode = oledMode,
                palette = palette
            ) {
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

    override fun onResume() {
        super.onResume()
        settingsVM.syncAppLanguageWithSystem()
    }
}
