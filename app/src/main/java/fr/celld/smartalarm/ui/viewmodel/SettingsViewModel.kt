package fr.celld.smartalarm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.celld.smartalarm.data.preferences.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel pour les paramètres de l'application
 */
class SettingsViewModel(
    private val preferences: AppPreferences
) : ViewModel() {

    val notificationsEnabled: StateFlow<Boolean> = preferences.notificationsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val themeMode: StateFlow<String> = preferences.themeMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "system"
        )

    val defaultRingtone: StateFlow<String> = preferences.defaultRingtone
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val defaultVibrate: StateFlow<Boolean> = preferences.defaultVibrate
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setNotificationsEnabled(enabled)
        }
    }

    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferences.setThemeMode(mode)
        }
    }

    fun setDefaultRingtone(uri: String) {
        viewModelScope.launch {
            preferences.setDefaultRingtone(uri)
        }
    }

    fun setDefaultVibrate(vibrate: Boolean) {
        viewModelScope.launch {
            preferences.setDefaultVibrate(vibrate)
        }
    }
}
