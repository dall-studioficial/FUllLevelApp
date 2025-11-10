package dall.full.level.app.repository

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Gestor de feedback háptico para manejar las vibraciones de la app.
 *
 * Esta clase centraliza la lógica de vibración, facilitando su uso y mantenimiento,
 * y gestionando la compatibilidad con diferentes versiones de Android.
 */
class HapticFeedbackManager(context: Context) {

    private val vibrator: Vibrator

    init {
        // Obtiene el servicio de vibración del sistema dependiendo de la versión de Android.
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Realiza una vibración corta y estándar, ideal para clics de botón.
     */
    fun performClickVibration() {
        if (!vibrator.hasVibrator()) return

        // Usa VibrationEffect para APIs modernas, con fallback para versiones antiguas.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(5) // Vibración muy corta para clics.
        }
    }

    /**
     * Realiza una vibración un poco más larga, para eventos importantes como la nivelación.
     */
    fun performLevelVibration() {
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50) // Vibración un poco más notoria.
        }
    }
}