package fr.celld.smartalarm.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Extension pour créer le DataStore
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * Gestion des préférences de l'application
 */
class AppPreferences(private val context: Context) {

    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val DEFAULT_RINGTONE = stringPreferencesKey("default_ringtone")
        private val DEFAULT_VIBRATE = booleanPreferencesKey("default_vibrate")
        private val DEFAULT_SNOOZE_DURATION = stringPreferencesKey("default_snooze_duration")
    }

    /**
     * Notifications activées
     */
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATIONS_ENABLED] ?: true
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    /**
     * Mode de thème (light, dark, system)
     */
    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[THEME_MODE] ?: "system"
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    /**
     * Sonnerie par défaut
     */
    val defaultRingtone: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_RINGTONE] ?: ""
    }

    suspend fun setDefaultRingtone(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_RINGTONE] = uri
        }
    }

    /**
     * Vibration par défaut
     */
    val defaultVibrate: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_VIBRATE] ?: true
    }

    suspend fun setDefaultVibrate(vibrate: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_VIBRATE] = vibrate
        }
    }

    /**
     * Durée de snooze par défaut
     */
    val defaultSnoozeDuration: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[DEFAULT_SNOOZE_DURATION] ?: "5"
    }

    suspend fun setDefaultSnoozeDuration(duration: String) {
        context.dataStore.edit { preferences ->
            preferences[DEFAULT_SNOOZE_DURATION] = duration
        }
    }
}
