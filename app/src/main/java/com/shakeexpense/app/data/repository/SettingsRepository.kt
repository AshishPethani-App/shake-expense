package com.shakeexpense.app.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val KEY_SHAKE_ENABLED = booleanPreferencesKey("shake_enabled")
        val KEY_SENSITIVITY = stringPreferencesKey("sensitivity")
        val KEY_APPS_SCRIPT_URL = stringPreferencesKey("apps_script_url")
        val KEY_SECRET_TOKEN = stringPreferencesKey("secret_token")
        val KEY_LAST_SYNCED = stringPreferencesKey("last_synced")
    }

    val shakeEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_SHAKE_ENABLED] ?: true }
    val sensitivity: Flow<String> = dataStore.data.map { it[KEY_SENSITIVITY] ?: "MEDIUM" }
    val appsScriptUrl: Flow<String> = dataStore.data.map { it[KEY_APPS_SCRIPT_URL] ?: "" }
    val lastSynced: Flow<String> = dataStore.data.map { it[KEY_LAST_SYNCED] ?: "Never" }

    suspend fun getAppsScriptUrl() = dataStore.data.first()[KEY_APPS_SCRIPT_URL] ?: ""
    suspend fun getSecretToken() = dataStore.data.first()[KEY_SECRET_TOKEN] ?: ""

    suspend fun setShakeEnabled(enabled: Boolean) = dataStore.edit { it[KEY_SHAKE_ENABLED] = enabled }
    suspend fun setSensitivity(s: String) = dataStore.edit { it[KEY_SENSITIVITY] = s }
    suspend fun setAppsScriptUrl(url: String) = dataStore.edit { it[KEY_APPS_SCRIPT_URL] = url }
    suspend fun setSecretToken(token: String) = dataStore.edit { it[KEY_SECRET_TOKEN] = token }
    suspend fun setLastSynced(time: String) = dataStore.edit { it[KEY_LAST_SYNCED] = time }
}
