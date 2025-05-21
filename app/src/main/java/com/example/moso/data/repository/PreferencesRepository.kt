package com.example.moso.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.moso.data.model.AppPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crea el DataStore en el Contexto
private val Context.dataStore by preferencesDataStore("app_prefs")

class PreferencesRepository(context: Context) {
    private val ds = context.dataStore

    companion object {
        val DARK_MODE      = booleanPreferencesKey("darkMode")
        val NOTIFICATIONS  = booleanPreferencesKey("notificationsEnabled")
        val LANGUAGE       = stringPreferencesKey("language")
        val AUTO_PLAY      = booleanPreferencesKey("autoPlayCarousel")
        val ITEMS_PER_PAGE = intPreferencesKey("itemsPerPage")
    }

    // Flujo de preferencias
    val prefsFlow: Flow<AppPreferences> = ds.data.map { prefs ->
        AppPreferences(
            darkMode            = prefs[DARK_MODE] ?: false,
            notificationsEnabled= prefs[NOTIFICATIONS] ?: true,
            language            = prefs[LANGUAGE] ?: "es",
            autoPlayCarousel    = prefs[AUTO_PLAY] ?: true,
            itemsPerPage        = prefs[ITEMS_PER_PAGE] ?: 20,
            // Si tienes valores fijos para versión, URLs, etc., defínelos en AppPreferences
        )
    }

    // Métodos para actualizar cada preferencia
    suspend fun updateDarkMode(enabled: Boolean) {
        ds.edit { it[DARK_MODE] = enabled }
    }

    suspend fun updateNotifications(enabled: Boolean) {
        ds.edit { it[NOTIFICATIONS] = enabled }
    }

    suspend fun updateLanguage(lang: String) {
        ds.edit { it[LANGUAGE] = lang }
    }

    suspend fun updateAutoPlay(enabled: Boolean) {
        ds.edit { it[AUTO_PLAY] = enabled }
    }

    suspend fun updateItemsPerPage(count: Int) {
        ds.edit { it[ITEMS_PER_PAGE] = count }
    }
}



