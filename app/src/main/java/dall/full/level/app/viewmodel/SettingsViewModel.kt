package dall.full.level.app.viewmodel

import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dall.full.level.app.data.AppLanguage
import dall.full.level.app.ui.theme.AppThemePalette
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val oledModeKey = booleanPreferencesKey("oled_mode")
    private val followSystemThemeKey = booleanPreferencesKey("follow_system_theme")
    private val pitchOffsetKey = floatPreferencesKey("pitch_offset")
    private val rollOffsetKey = floatPreferencesKey("roll_offset")
    private val hapticEnabledKey = booleanPreferencesKey("haptic_enabled")
    private val beepEnabledKey = booleanPreferencesKey("beep_enabled")
    private val ttsEnabledKey = booleanPreferencesKey("tts_enabled")
    private val themePaletteKey = stringPreferencesKey("theme_palette")
    private val languageKey = stringPreferencesKey("language_code")

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _oledMode = MutableStateFlow(false)
    val oledMode: StateFlow<Boolean> = _oledMode.asStateFlow()

    private val _followSystemTheme = MutableStateFlow(false)
    val followSystemTheme: StateFlow<Boolean> = _followSystemTheme.asStateFlow()

    private val _themePalette = MutableStateFlow(AppThemePalette.GREEN)
    val themePalette: StateFlow<AppThemePalette> = _themePalette.asStateFlow()

    private val _appLanguage = MutableStateFlow(AppLanguage.fromApplicationLocales())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _pitchOffset = MutableStateFlow(0f)
    val pitchOffset: StateFlow<Float> = _pitchOffset.asStateFlow()

    private val _rollOffset = MutableStateFlow(0f)
    val rollOffset: StateFlow<Float> = _rollOffset.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(true)
    val hapticEnabled: StateFlow<Boolean> = _hapticEnabled.asStateFlow()

    private val _beepEnabled = MutableStateFlow(true)
    val beepEnabled: StateFlow<Boolean> = _beepEnabled.asStateFlow()

    private val _ttsEnabled = MutableStateFlow(true)
    val ttsEnabled: StateFlow<Boolean> = _ttsEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                loadPreferences()
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun loadPreferences() {
        dataStore.data
            .map { prefs: Preferences ->
                val savedPaletteName = prefs[themePaletteKey] ?: AppThemePalette.GREEN.name
                val palette = try {
                    AppThemePalette.valueOf(savedPaletteName)
                } catch (_: Exception) {
                    AppThemePalette.GREEN
                }

                val language = AppLanguage.fromCode(prefs[languageKey] ?: "")

                Tuple10(
                    prefs[darkThemeKey] ?: false,
                    prefs[oledModeKey] ?: false,
                    prefs[followSystemThemeKey] ?: false,
                    prefs[pitchOffsetKey] ?: 0f,
                    prefs[rollOffsetKey] ?: 0f,
                    prefs[hapticEnabledKey] ?: true,
                    prefs[beepEnabledKey] ?: true,
                    prefs[ttsEnabledKey] ?: true,
                    palette,
                    language
                )
            }
            .collect { tuple ->
                _darkTheme.value = tuple.v1
                _oledMode.value = tuple.v2
                _followSystemTheme.value = tuple.v3
                _pitchOffset.value = tuple.v4
                _rollOffset.value = tuple.v5
                _hapticEnabled.value = tuple.v6
                _beepEnabled.value = tuple.v7
                _ttsEnabled.value = tuple.v8
                _themePalette.value = tuple.v9
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                    _appLanguage.value = tuple.v10
                    AppLanguage.applyLanguage(tuple.v10)
                }
            }
    }

    private data class Tuple10<A, B, C, D, E, F, G, H, I, J>(
        val v1: A, val v2: B, val v3: C, val v4: D, val v5: E, val v6: F, val v7: G, val v8: H, val v9: I, val v10: J
    )

    enum class ThemeMode { AUTO, LIGHT, DARK }

    fun setThemeMode(mode: ThemeMode) {
        val followSystemTheme = mode == ThemeMode.AUTO
        val darkTheme = mode == ThemeMode.DARK
        _followSystemTheme.value = followSystemTheme
        _darkTheme.value = darkTheme
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[followSystemThemeKey] = followSystemTheme
                prefs[darkThemeKey] = darkTheme
            }
        }
    }

    fun setOledMode(enabled: Boolean) {
        _oledMode.value = enabled
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[oledModeKey] = enabled
            }
        }
    }

    fun setThemePalette(palette: AppThemePalette) {
        _themePalette.value = palette
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[themePaletteKey] = palette.name
            }
        }
    }

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[languageKey] = language.code
            }
        }
        AppLanguage.applyLanguage(language)
    }

    fun syncAppLanguageWithSystem() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val language = AppLanguage.fromApplicationLocales()
        _appLanguage.value = language
        viewModelScope.launch {
            dataStore.edit { prefs ->
                if (prefs[languageKey] != language.code) {
                    prefs[languageKey] = language.code
                }
            }
        }
    }

    fun saveCalibration(pitchOffset: Float, rollOffset: Float) {
        _pitchOffset.value = pitchOffset
        _rollOffset.value = rollOffset
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[pitchOffsetKey] = pitchOffset
                prefs[rollOffsetKey] = rollOffset
            }
        }
    }

    fun resetCalibration() {
        _pitchOffset.value = 0f
        _rollOffset.value = 0f
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[pitchOffsetKey] = 0f
                prefs[rollOffsetKey] = 0f
            }
        }
    }

    fun setHapticEnabled(enabled: Boolean) {
        _hapticEnabled.value = enabled
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[hapticEnabledKey] = enabled
            }
        }
    }

    fun setBeepEnabled(enabled: Boolean) {
        _beepEnabled.value = enabled
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[beepEnabledKey] = enabled
            }
        }
    }

    fun setTtsEnabled(enabled: Boolean) {
        _ttsEnabled.value = enabled
        viewModelScope.launch {
            dataStore.edit { prefs ->
                prefs[ttsEnabledKey] = enabled
            }
        }
    }
}
