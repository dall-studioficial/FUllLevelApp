package dall.full.level.app.ui.theme

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import dall.full.level.app.R

/**
 * Enumeración de Paletas de Colores de la App (Material Theme Builder M3)
 */
enum class AppThemePalette(@StringRes val displayNameRes: Int, val primaryColor: Color) {
    GREEN(R.string.theme_green, Color(0xFF4C662B)),
    BLUE(R.string.theme_blue, Color(0xFF196584)),
    ORANGE(R.string.theme_orange, Color(0xFF8B5000)),
    PURPLE(R.string.theme_purple, Color(0xFF6B4EA2)),
    RED(R.string.theme_red, Color(0xFF904A47)),
    GOLD(R.string.theme_gold, Color(0xFF715C00)),
    DYNAMIC(R.string.theme_dynamic, Color(0xFF6750A4))
}

// --- 1. VERDE (BOSQUE) ---
val greenLightScheme = lightColorScheme(
    primary = Color(0xFF4C662B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFCDEDA3),
    onPrimaryContainer = Color(0xFF102000),
    secondary = Color(0xFF586249),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE7C8),
    onSecondaryContainer = Color(0xFF161E0B),
    tertiary = Color(0xFF386663),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFBCECE7),
    onTertiaryContainer = Color(0xFF00201E),
    background = Color(0xFFF9FAEF),
    onBackground = Color(0xFF1A1C16),
    surface = Color(0xFFF9FAEF),
    onSurface = Color(0xFF1A1C16),
    surfaceContainer = Color(0xFFEEEFE3),
    surfaceContainerHigh = Color(0xFFE8E9DE)
)

val greenDarkScheme = darkColorScheme(
    primary = Color(0xFFB1D18A),
    onPrimary = Color(0xFF1F3701),
    primaryContainer = Color(0xFF354E16),
    onPrimaryContainer = Color(0xFFCDEDA3),
    secondary = Color(0xFFBFCBAD),
    onSecondary = Color(0xFF2A331E),
    secondaryContainer = Color(0xFF404A33),
    onSecondaryContainer = Color(0xFFDCE7C8),
    tertiary = Color(0xFFA0D0CB),
    onTertiary = Color(0xFF003735),
    tertiaryContainer = Color(0xFF1F4E4B),
    onTertiaryContainer = Color(0xFFBCECE7),
    background = Color(0xFF12140E),
    onBackground = Color(0xFFE2E3D8),
    surface = Color(0xFF12140E),
    onSurface = Color(0xFFE2E3D8),
    surfaceContainer = Color(0xFF1E201A),
    surfaceContainerHigh = Color(0xFF282B24)
)

// --- 2. AZUL (OCÉANO) ---
val blueLightScheme = lightColorScheme(
    primary = Color(0xFF196584),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC1E8FF),
    onPrimaryContainer = Color(0xFF001E2A),
    secondary = Color(0xFF4E616D),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD1E5F4),
    onSecondaryContainer = Color(0xFF0A1E28),
    tertiary = Color(0xFF615A7D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE6DEFF),
    onTertiaryContainer = Color(0xFF1D1736),
    background = Color(0xFFF6FAFE),
    onBackground = Color(0xFF171C1F),
    surface = Color(0xFFF6FAFE),
    onSurface = Color(0xFF171C1F),
    surfaceContainer = Color(0xFFEAEEF3),
    surfaceContainerHigh = Color(0xFFE4E8ED)
)

val blueDarkScheme = darkColorScheme(
    primary = Color(0xFF8ECDFF),
    onPrimary = Color(0xFF003448),
    primaryContainer = Color(0xFF004C67),
    onPrimaryContainer = Color(0xFFC1E8FF),
    secondary = Color(0xFFB5C9D7),
    onSecondary = Color(0xFF20333E),
    secondaryContainer = Color(0xFF374955),
    onSecondaryContainer = Color(0xFFD1E5F4),
    tertiary = Color(0xFFCAC1E9),
    onTertiary = Color(0xFF322C4C),
    tertiaryContainer = Color(0xFF494264),
    onTertiaryContainer = Color(0xFFE6DEFF),
    background = Color(0xFF0F1417),
    onBackground = Color(0xFFE0E3E8),
    surface = Color(0xFF0F1417),
    onSurface = Color(0xFFE0E3E8),
    surfaceContainer = Color(0xFF1A1F23),
    surfaceContainerHigh = Color(0xFF252A2E)
)

// --- 3. NARANJA (FUEGO) ---
val orangeLightScheme = lightColorScheme(
    primary = Color(0xFF8B5000),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDCC1),
    onPrimaryContainer = Color(0xFF2C1600),
    secondary = Color(0xFF715A45),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFCDEC2),
    onSecondaryContainer = Color(0xFF281808),
    tertiary = Color(0xFF576339),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDBE9B3),
    onTertiaryContainer = Color(0xFF151E00),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF211A13),
    surface = Color(0xFFFFF8F5),
    onSurface = Color(0xFF211A13),
    surfaceContainer = Color(0xFFF7ECDF),
    surfaceContainerHigh = Color(0xFFF1E6D9)
)

val orangeDarkScheme = darkColorScheme(
    primary = Color(0xFFFFB878),
    onPrimary = Color(0xFF4A2800),
    primaryContainer = Color(0xFF6A3B00),
    onPrimaryContainer = Color(0xFFFFDCC1),
    secondary = Color(0xFFDEC2A7),
    onSecondary = Color(0xFF3F2D1B),
    secondaryContainer = Color(0xFF574330),
    onSecondaryContainer = Color(0xFFFCDEC2),
    tertiary = Color(0xFFBFCD99),
    onTertiary = Color(0xFF2A3410),
    tertiaryContainer = Color(0xFF404B24),
    onTertiaryContainer = Color(0xFFDBE9B3),
    background = Color(0xFF18120B),
    onBackground = Color(0xFFF0E0D2),
    surface = Color(0xFF18120B),
    onSurface = Color(0xFFF0E0D2),
    surfaceContainer = Color(0xFF231B13),
    surfaceContainerHigh = Color(0xFF2E251C)
)

// --- 4. PÚRPURA (IMPERIAL) ---
val purpleLightScheme = lightColorScheme(
    primary = Color(0xFF6B4EA2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFECDCFF),
    onPrimaryContainer = Color(0xFF24005A),
    secondary = Color(0xFF635B70),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE9DEF8),
    onSecondaryContainer = Color(0xFF1F182B),
    tertiary = Color(0xFF7E525D),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFD9E1),
    onTertiaryContainer = Color(0xFF31101B),
    background = Color(0xFFFDF7FF),
    onBackground = Color(0xFF1D1B20),
    surface = Color(0xFFFDF7FF),
    onSurface = Color(0xFF1D1B20),
    surfaceContainer = Color(0xFFF2ECF4),
    surfaceContainerHigh = Color(0xFFECE6EE)
)

val purpleDarkScheme = darkColorScheme(
    primary = Color(0xFFD5BBFF),
    onPrimary = Color(0xFF3B1A70),
    primaryContainer = Color(0xFF523488),
    onPrimaryContainer = Color(0xFFECDCFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF342D40),
    secondaryContainer = Color(0xFF4B4358),
    onSecondaryContainer = Color(0xFFE9DEF8),
    tertiary = Color(0xFFF0B7C5),
    onTertiary = Color(0xFF4A2530),
    tertiaryContainer = Color(0xFF643B46),
    onTertiaryContainer = Color(0xFFFFD9E1),
    background = Color(0xFF151218),
    onBackground = Color(0xFFE7E0E8),
    surface = Color(0xFF151218),
    onSurface = Color(0xFFE7E0E8),
    surfaceContainer = Color(0xFF211E24),
    surfaceContainerHigh = Color(0xFF2B282F)
)

// --- 5. ROJO (RUBÍ) ---
val redLightScheme = lightColorScheme(
    primary = Color(0xFF904A47),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDAD8),
    onPrimaryContainer = Color(0xFF3B080A),
    secondary = Color(0xFF775654),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFDAD8),
    onSecondaryContainer = Color(0xFF2C1514),
    tertiary = Color(0xFF735B2E),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDEA8),
    onTertiaryContainer = Color(0xFF261900),
    background = Color(0xFFFFF8F7),
    onBackground = Color(0xFF231919),
    surface = Color(0xFFFFF8F7),
    onSurface = Color(0xFF231919),
    surfaceContainer = Color(0xFFF9EAE8),
    surfaceContainerHigh = Color(0xFFF3E4E2)
)

val redDarkScheme = darkColorScheme(
    primary = Color(0xFFFFB3B0),
    onPrimary = Color(0xFF571D1D),
    primaryContainer = Color(0xFF733331),
    onPrimaryContainer = Color(0xFFFFDAD8),
    secondary = Color(0xFFE7BDBA),
    onSecondary = Color(0xFF442928),
    secondaryContainer = Color(0xFF5D3F3D),
    onSecondaryContainer = Color(0xFFFFDAD8),
    tertiary = Color(0xFFE2C28C),
    onTertiary = Color(0xFF402D04),
    tertiaryContainer = Color(0xFF594319),
    onTertiaryContainer = Color(0xFFFFDEA8),
    background = Color(0xFF1A1111),
    onBackground = Color(0xFFF1DFDD),
    surface = Color(0xFF1A1111),
    onSurface = Color(0xFFF1DFDD),
    surfaceContainer = Color(0xFF261D1D),
    surfaceContainerHigh = Color(0xFF312727)
)

// --- 6. DORADO (ÁMBAR) ---
val goldLightScheme = lightColorScheme(
    primary = Color(0xFF715C00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFE07E),
    onPrimaryContainer = Color(0xFF231B00),
    secondary = Color(0xFF675E40),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEFE2BC),
    onSecondaryContainer = Color(0xFF221B04),
    tertiary = Color(0xFF46664B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFC8ECE8),
    onTertiaryContainer = Color(0xFF01210D),
    background = Color(0xFFFFF9EE),
    onBackground = Color(0xFF1E1B13),
    surface = Color(0xFFFFF9EE),
    onSurface = Color(0xFF1E1B13),
    surfaceContainer = Color(0xFFF6ECE0),
    surfaceContainerHigh = Color(0xFFF0E6DA)
)

val goldDarkScheme = darkColorScheme(
    primary = Color(0xFFE5C34A),
    onPrimary = Color(0xFF3B2F00),
    primaryContainer = Color(0xFF554500),
    onPrimaryContainer = Color(0xFFFFE07E),
    secondary = Color(0xFFD2C6A1),
    onSecondary = Color(0xFF373016),
    secondaryContainer = Color(0xFF4E462A),
    onSecondaryContainer = Color(0xFFEFE2BC),
    tertiary = Color(0xFFACD0AF),
    onTertiary = Color(0xFF17371F),
    tertiaryContainer = Color(0xFF2E4E34),
    onTertiaryContainer = Color(0xFFC8ECE8),
    background = Color(0xFF16130C),
    onBackground = Color(0xFFEBE2D5),
    surface = Color(0xFF16130C),
    onSurface = Color(0xFFEBE2D5),
    surfaceContainer = Color(0xFF211D16),
    surfaceContainerHigh = Color(0xFF2B2820)
)

/**
 * Obtiene el ColorScheme M3 correcto según la paleta, tema oscuro y modo OLED.
 */
@Composable
fun getColorSchemeForPalette(
    palette: AppThemePalette,
    darkTheme: Boolean,
    oledMode: Boolean
): ColorScheme {
    val context = LocalContext.current

    val baseScheme = if (darkTheme) {
        when (palette) {
            AppThemePalette.GREEN -> greenDarkScheme
            AppThemePalette.BLUE -> blueDarkScheme
            AppThemePalette.ORANGE -> orangeDarkScheme
            AppThemePalette.PURPLE -> purpleDarkScheme
            AppThemePalette.RED -> redDarkScheme
            AppThemePalette.GOLD -> goldDarkScheme
            AppThemePalette.DYNAMIC -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dynamicDarkColorScheme(context)
                } else {
                    greenDarkScheme
                }
            }
        }
    } else {
        when (palette) {
            AppThemePalette.GREEN -> greenLightScheme
            AppThemePalette.BLUE -> blueLightScheme
            AppThemePalette.ORANGE -> orangeLightScheme
            AppThemePalette.PURPLE -> purpleLightScheme
            AppThemePalette.RED -> redLightScheme
            AppThemePalette.GOLD -> goldLightScheme
            AppThemePalette.DYNAMIC -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dynamicLightColorScheme(context)
                } else {
                    greenLightScheme
                }
            }
        }
    }

    // Sobrescribir colores a Negro Puro (#000000) si el modo OLED está activo en Tema Oscuro
    return if (darkTheme && oledMode) {
        baseScheme.copy(
            background = Color.Black,
            surface = Color.Black,
            surfaceDim = Color.Black,
            surfaceContainerLowest = Color.Black,
            surfaceContainerLow = Color(0xFF050505),
            surfaceContainer = Color(0xFF0A0A0A),
            surfaceContainerHigh = Color(0xFF121212),
            surfaceContainerHighest = Color(0xFF181818)
        )
    } else {
        baseScheme
    }
}
