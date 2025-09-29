package dall.full.level.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private const val SETTINGS_PREFS = "settings_prefs"
private val Context.dataStore by preferencesDataStore(name = SETTINGS_PREFS)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val oledModeKey = booleanPreferencesKey("oled_mode")
    private val followSystemThemeKey = booleanPreferencesKey("follow_system_theme")

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme: StateFlow<Boolean> = _darkTheme.asStateFlow()

    private val _oledMode = MutableStateFlow(false)
    val oledMode: StateFlow<Boolean> = _oledMode.asStateFlow()

    private val _followSystemTheme = MutableStateFlow(false)
    val followSystemTheme: StateFlow<Boolean> = _followSystemTheme.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                loadPreferences()
            } catch (_: Exception) {
            }
        }
    }

    private suspend fun loadPreferences() {
        getApplication<Application>().dataStore.data
            .map { prefs: Preferences ->
                Triple(
                    prefs[darkThemeKey] ?: false,
                    prefs[oledModeKey] ?: false,
                    prefs[followSystemThemeKey] ?: false
                )
            }
            .collect { triple: Triple<Boolean, Boolean, Boolean> ->
                _darkTheme.value = triple.first
                _oledMode.value = triple.second
                _followSystemTheme.value = triple.third
            }
    }

    enum class ThemeMode { AUTO, LIGHT, DARK }

    fun setThemeMode(mode: ThemeMode) {
        val followSystemTheme = mode == ThemeMode.AUTO
        val darkTheme = mode == ThemeMode.DARK
        _followSystemTheme.value = followSystemTheme
        _darkTheme.value = darkTheme
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[followSystemThemeKey] = followSystemTheme
                prefs[darkThemeKey] = darkTheme
            }
        }
    }

    fun setOledMode(enabled: Boolean) {
        _oledMode.value = enabled
        viewModelScope.launch {
            getApplication<Application>().dataStore.edit { prefs ->
                prefs[oledModeKey] = enabled
            }
        }
    }
}
