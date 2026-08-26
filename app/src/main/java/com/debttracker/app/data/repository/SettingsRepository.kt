package com.debttracker.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.debttracker.app.model.AppLanguage
import com.debttracker.app.model.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val settings: Flow<AppSettings> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences ->
            AppSettings(
                language = AppLanguage.fromCode(preferences[Keys.LANGUAGE]),
                darkModeOverride = preferences[Keys.DARK_MODE]
            )
        }

    val language: Flow<AppLanguage> = settings.map { it.language }

    suspend fun setLanguage(language: AppLanguage) {
        dataStore.edit { preferences ->
            preferences[Keys.LANGUAGE] = language.code
        }
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DARK_MODE] = enabled
        }
    }
}
