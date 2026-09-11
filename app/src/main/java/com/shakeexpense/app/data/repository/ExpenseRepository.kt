package com.shakeexpense.app.data.repository

import android.util.Log
import com.shakeexpense.app.data.local.ExpenseDao
import com.shakeexpense.app.data.local.toEntity
import com.shakeexpense.app.data.remote.ExpenseApiService
import com.shakeexpense.app.data.remote.ExpenseRequest
import com.shakeexpense.app.domain.model.Expense
import com.shakeexpense.app.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository @Inject constructor(
    private val dao: ExpenseDao,
    private val api: ExpenseApiService,
    private val settingsRepository: SettingsRepository
) {
    fun getAllExpenses(): Flow<List<Expense>> =
        dao.getAllFlow().map { list -> list.map { it.toDomain() } }

    fun getTodayExpenses(): Flow<List<Expense>> {
        val prefix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        return dao.getTodayFlow(prefix).map { list -> list.map { it.toDomain() } }
    }

    suspend fun saveExpense(expense: Expense) {
        dao.insert(expense.toEntity())
        trySyncSingle(expense)
    }

    private suspend fun trySyncSingle(expense: Expense) {
        if (settingsRepository.getAppsScriptUrl().isBlank()) return
        try {
            dao.updateStatus(expense.id, SyncStatus.SYNCING.name)
            val response = api.saveExpense(
                ExpenseRequest(
                    expenseId = expense.id,
                    amount = expense.amount,
                    description = expense.description,
                    currency = expense.currency,
                    clientTimestamp = expense.createdAt,
                    token = settingsRepository.getSecretToken()
                )
            )
            if (response.success) {
                dao.updateStatus(expense.id, SyncStatus.SYNCED.name)
            } else {
                dao.updateStatus(expense.id, SyncStatus.FAILED.name)
            }
        } catch (e: Exception) {
            Log.e("ExpenseRepo", "Sync failed", e)
            dao.updateStatus(expense.id, SyncStatus.FAILED.name)
        }
    }

    suspend fun syncPending() {
        val pending = dao.getPending()
        pending.forEach { entity -> trySyncSingle(entity.toDomain()) }
    }
}
