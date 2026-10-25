package dall.full.level.app.data

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

/**
 * Enumeración de Idiomas Disponibles en la Aplicación (estilo Google Maps).
 */
enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val flag: String
) {
    AUTO("", "System Default", "🌐"),
    SPANISH("es", "Español", "🇪🇸"),
    ENGLISH("en", "English", "🇺🇸"),
    KOREAN("ko", "한국어", "🇰🇷"),
    CHINESE("zh", "简体中文", "🇨🇳"),
    RUSSIAN("ru", "Русский", "🇷🇺"),
    HINDI("hi", "हिन्दी", "🇮🇳");

    companion object {
        fun fromCode(code: String): AppLanguage {
            val languageCode = Locale.forLanguageTag(code).language
            return entries.find { it.code.equals(languageCode, ignoreCase = true) } ?: AUTO
        }

        fun fromApplicationLocales(): AppLanguage {
            val locales = AppCompatDelegate.getApplicationLocales()
            for (index in 0 until locales.size()) {
                val locale = locales[index] ?: continue
                entries.firstOrNull {
                    it != AUTO && it.code.equals(locale.language, ignoreCase = true)
                }?.let { return it }
            }
            return AUTO
        }

        fun applyLanguage(language: AppLanguage) {
            val localeList = if (language.code.isEmpty()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(language.code)
            }
            if (AppCompatDelegate.getApplicationLocales() != localeList) {
                AppCompatDelegate.setApplicationLocales(localeList)
            }
            if (language.code.isNotEmpty()) {
                val locale = Locale.forLanguageTag(language.code)
                Locale.setDefault(locale)
            }
        }
    }
}
