package com.shakeexpense.app.ui.settings
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakeexpense.app.data.repository.SettingsRepository
import com.shakeexpense.app.service.ShakeDetectionService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val shakeEnabled: Boolean = true,
    val sensitivity: String = "MEDIUM",
    val appsScriptUrl: String = "",
    val lastSynced: String = "Never"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val state: StateFlow<SettingsState> = combine(
        settingsRepository.shakeEnabled,
        settingsRepository.sensitivity,
        settingsRepository.appsScriptUrl,
        settingsRepository.lastSynced
    ) { enabled, sens, url, synced ->
        SettingsState(shakeEnabled = enabled, sensitivity = sens, appsScriptUrl = url, lastSynced = synced)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

    fun setShakeEnabled(v: Boolean) = viewModelScope.launch {
        settingsRepository.setShakeEnabled(v)
        if (v) ShakeDetectionService.start(context) else ShakeDetectionService.stop(context)
    }
    fun setSensitivity(v: String) = viewModelScope.launch { settingsRepository.setSensitivity(v) }
    fun setAppsScriptUrl(v: String) = viewModelScope.launch { settingsRepository.setAppsScriptUrl(v) }
    fun setSecretToken(v: String) = viewModelScope.launch { settingsRepository.setSecretToken(v) }
}
