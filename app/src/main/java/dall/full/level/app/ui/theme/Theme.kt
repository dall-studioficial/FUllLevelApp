package dall.full.level.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun FUllLevelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    oledMode: Boolean = false,
    palette: AppThemePalette = AppThemePalette.GREEN,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorSchemeForPalette(
        palette = palette,
        darkTheme = darkTheme,
        oledMode = oledMode
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
