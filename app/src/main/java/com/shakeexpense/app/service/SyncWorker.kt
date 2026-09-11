package com.shakeexpense.app.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.shakeexpense.app.data.repository.ExpenseRepository
import com.shakeexpense.app.data.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val expenseRepository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            expenseRepository.syncPending()
            val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM, hh:mm a"))
            settingsRepository.setLastSynced(now)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "expense_sync", ExistingPeriodicWorkPolicy.KEEP, request
            )
        }
    }
}
