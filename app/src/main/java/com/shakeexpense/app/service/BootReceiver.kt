package com.shakeexpense.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.shakeexpense.app.data.repository.SettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        CoroutineScope(Dispatchers.IO).launch {
            if (settingsRepository.shakeEnabled.first()) {
                ShakeDetectionService.start(context)
            }
        }
    }
}
