package dall.full.level.app.repository

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestor centralizado e inyectable de Anuncios Google AdMob (Intersticiales y Recompensados).
 * La carga de anuncios se realiza en el Hilo Principal de forma diferida (lazy) para no bloquear la pantalla de inicio.
 */
@Singleton
class AdManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // IDs de producción de Google AdMob
    private val interstitialAdUnitId = "ca-app-pub-1345719029437969/1198439041"
    private val rewardedAdUnitId = "ca-app-pub-1345719029437969/4259716653"

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    // Control de frecuencia para intersticiales (mínimo 3 minutos entre anuncios)
    private var lastInterstitialTime = 0L
    private val interstitialMinIntervalMs = 3 * 60 * 1000L // 3 minutos

    /**
     * Carga de forma diferida un Anuncio Intersticial en el hilo principal sin congelar el arranque.
     */
    fun loadInterstitialAd() {
        if (interstitialAd != null) return

        try {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(
                context,
                interstitialAdUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        interstitialAd = ad
                        Log.d("AdManager", "Anuncio Intersticial cargado con éxito.")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        interstitialAd = null
                        Log.e("AdManager", "Fallo al cargar Anuncio Intersticial: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("AdManager", "Error al solicitar Intersticial", e)
        }
    }

    /**
     * Muestra el Anuncio Intersticial respetando el límite de frecuencia (máximo 1 cada 3 minutos).
     */
    fun showInterstitialAdWithCap(activity: Activity?, onAdDismissed: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        val canShowAd = (currentTime - lastInterstitialTime) >= interstitialMinIntervalMs

        if (activity != null && canShowAd && interstitialAd != null) {
            lastInterstitialTime = currentTime
            val currentAd = interstitialAd
            interstitialAd = null

            currentAd?.show(activity)
            loadInterstitialAd()
            onAdDismissed()
        } else {
            onAdDismissed()
            if (interstitialAd == null) {
                loadInterstitialAd()
            }
        }
    }

    /**
     * Carga de forma diferida un Anuncio Bonificado (Rewarded) en el hilo principal.
     */
    fun loadRewardedAd() {
        if (rewardedAd != null) return

        try {
            val adRequest = AdRequest.Builder().build()
            RewardedAd.load(
                context,
                rewardedAdUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        rewardedAd = ad
                        Log.d("AdManager", "Anuncio Recompensado cargado con éxito.")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        rewardedAd = null
                        Log.e("AdManager", "Fallo al cargar Anuncio Recompensado: ${error.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("AdManager", "Error al solicitar Recompensado", e)
        }
    }

    /**
     * Muestra un Anuncio Bonificado y otorga la recompensa si el usuario ve el video completo.
     */
    fun showRewardedAd(activity: Activity?, onRewardGranted: () -> Unit) {
        if (activity != null && rewardedAd != null) {
            val currentAd = rewardedAd
            rewardedAd = null

            currentAd?.show(activity) { _ ->
                onRewardGranted()
            }
            loadRewardedAd()
        } else {
            Log.d("AdManager", "Anuncio Recompensado no disponible aún. Cargando...")
            loadRewardedAd()
        }
    }
}

/**
 * Extensión de utilidad para obtener la Activity desde cualquier Context en Compose.
 */
fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
