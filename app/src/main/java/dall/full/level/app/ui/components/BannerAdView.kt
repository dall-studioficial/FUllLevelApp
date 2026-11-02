package dall.full.level.app.ui.components

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.delay

// ID de AdMob de Producción para Banner (bann)
private const val BANNER_AD_UNIT_ID = "ca-app-pub-1345719029437969/2511520713"

/**
 * Componente Compose reutilizable para mostrar Banners Adaptativos de AdMob.
 * Utiliza view.post para encolar la carga del anuncio de forma asíncrona en el MessageQueue del Handler.
 * Esto evita bloqueos del hilo principal (ANR) durante los eventos de toque e inicio.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adUnitId: String = BANNER_AD_UNIT_ID
) {
    var showAd by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Esperar 1 segundo tras la renderización inicial para no saturar el hilo principal
        delay(1000)
        showAd = true
    }

    if (showAd) {
        AndroidView(
            modifier = modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .height(50.dp),
            factory = { context ->
                AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    this.adUnitId = adUnitId
                    // Cargar el anuncio encolado en el Handler para no bloquear eventos de toque (InputDispatching)
                    post {
                        try {
                            loadAd(AdRequest.Builder().build())
                        } catch (e: Exception) {
                            Log.e("BannerAdView", "Error al solicitar banner AdMob", e)
                        }
                    }
                }
            }
        )
    }
}
