package dall.full.level.app.repository

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * Gestor de Audio y Asistente de Voz TTS para la medición sin manos.
 * Utiliza inicialización diferida (lazy) para que el arranque de la app sea instantáneo.
 */
@Singleton
class AudioFeedbackManager @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var toneGenerator: ToneGenerator? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsReady = false
    private var isSpeaking = false

    private var lastBeepTime = 0L

    private fun ensureTtsInitialized() {
        if (textToSpeech == null) {
            try {
                textToSpeech = TextToSpeech(context, this)
            } catch (e: Exception) {
                Log.e("AudioFeedbackManager", "Error al instanciar TextToSpeech", e)
            }
        }
    }

    private fun initToneGenerator() {
        try {
            toneGenerator?.release()
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
        } catch (e: Exception) {
            Log.e("AudioFeedbackManager", "Error al inicializar ToneGenerator", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            setTtsLanguage(currentAppLocale())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                textToSpeech?.setAudioAttributes(audioAttributes)
            }

            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeaking = true
                }

                override fun onDone(utteranceId: String?) {
                    isSpeaking = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    isSpeaking = false
                }
            })

            isTtsReady = true
            Log.d("AudioFeedbackManager", "TextToSpeech inicializado con éxito.")
        } else {
            Log.e("AudioFeedbackManager", "Fallo al inicializar TextToSpeech: status=$status")
        }
    }

    /**
     * Emite un pitido de proximidad al acercarse al cero (0.0°).
     * No interfiere mientras la voz TTS esté hablando.
     */
    fun playProximityBeep(angle: Float, isBeepEnabled: Boolean) {
        if (!isBeepEnabled || isSpeaking) return

        val absAngle = abs(angle)
        if (absAngle > 10.0f) return

        val currentTime = System.currentTimeMillis()
        val beepInterval = (80 + (absAngle * 42)).toLong()

        if (currentTime - lastBeepTime >= beepInterval) {
            lastBeepTime = currentTime
            try {
                if (toneGenerator == null) {
                    initToneGenerator()
                }
                if (absAngle <= 0.5f) {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 100)
                } else {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 40)
                }
            } catch (e: Exception) {
                Log.e("AudioFeedbackManager", "Recreando ToneGenerator tras fallo", e)
                initToneGenerator()
            }
        }
    }

    /**
     * Lee en voz alta la medición actual usando Text-To-Speech.
     */
    fun speakAngle(angle: Float, isPitch: Boolean, isTtsEnabled: Boolean) {
        if (!isTtsEnabled) return

        ensureTtsInitialized()

        if (!isTtsReady || textToSpeech == null) return

        val locale = currentAppLocale()
        setTtsLanguage(locale)
        val cleanSpeech = buildMeasurementSpeech(angle, isPitch, locale)

        try {
            isSpeaking = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, null, "angle_readout")
            } else {
                @Suppress("DEPRECATION")
                textToSpeech?.speak(cleanSpeech, TextToSpeech.QUEUE_FLUSH, null)
            }
        } catch (e: Exception) {
            Log.e("AudioFeedbackManager", "Error al reproducir TTS", e)
            isSpeaking = false
        }
    }

    private fun currentAppLocale(): Locale {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (!appLocales.isEmpty) {
            appLocales[0]?.let { return it }
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0] ?: Locale.getDefault()
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale ?: Locale.getDefault()
        }
    }

    private fun setTtsLanguage(locale: Locale) {
        @Suppress("DEPRECATION")
        val result = textToSpeech?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w("AudioFeedbackManager", "TTS no tiene una voz disponible para ${locale.toLanguageTag()}")
        }
    }

    private fun buildMeasurementSpeech(angle: Float, isPitch: Boolean, locale: Locale): String {
        val language = locale.language
        val isInfinite = isPitch && angle >= 999f
        val value = if (isInfinite) "" else String.format(locale, "%.1f", angle)

        return when (language) {
            "en" -> when {
                isInfinite -> "Infinite incline"
                isPitch -> "$value over 12"
                else -> "$value degrees"
            }
            "ko" -> when {
                isInfinite -> "무한 기울기"
                isPitch -> "12 중 $value"
                else -> "$value 도"
            }
            "zh" -> when {
                isInfinite -> "无限倾斜"
                isPitch -> "12分之$value"
                else -> "$value 度"
            }
            "ru" -> when {
                isInfinite -> "Бесконечный наклон"
                isPitch -> "$value из 12"
                else -> "$value градусов"
            }
            "hi" -> when {
                isInfinite -> "अनंत झुकाव"
                isPitch -> "12 में से $value"
                else -> "$value डिग्री"
            }
            else -> when {
                isInfinite -> "Inclinación infinita"
                isPitch -> "$value sobre 12"
                else -> "$value grados"
            }
        }
    }

    fun stop() {
        isSpeaking = false
        textToSpeech?.stop()
    }

    fun release() {
        try {
            isSpeaking = false
            toneGenerator?.release()
            toneGenerator = null
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
        } catch (e: Exception) {
            Log.e("AudioFeedbackManager", "Error al liberar recursos de audio", e)
        }
    }
}
