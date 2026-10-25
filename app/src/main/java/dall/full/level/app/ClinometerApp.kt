package dall.full.level.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Clase Application necesaria para inicializar Hilt y el SDK de Google AdMob.
 */
@HiltAndroidApp
class ClinometerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Google Mobile Ads en segundo plano para no ralentizar el inicio
        CoroutineScope(Dispatchers.IO).launch {
            try {
                MobileAds.initialize(this@ClinometerApp) {}
            } catch (e: Exception) {
                // Capturar cualquier excepción de inicialización
            }
        }
    }
}
