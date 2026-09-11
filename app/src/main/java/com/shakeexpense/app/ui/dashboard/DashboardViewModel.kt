package com.shakeexpense.app.ui.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakeexpense.app.data.repository.ExpenseRepository
import com.shakeexpense.app.data.repository.SettingsRepository
import com.shakeexpense.app.domain.model.Expense
import com.shakeexpense.app.service.ShakeDetectionService
import com.shakeexpense.app.service.SyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardState(
    val todayTotal: Double = 0.0,
    val todayCount: Int = 0,
    val recentExpenses: List<Expense> = emptyList(),
    val shakeEnabled: Boolean = true
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val expenseRepository: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val state: StateFlow<DashboardState> = combine(
        expenseRepository.getTodayExpenses(),
        expenseRepository.getAllExpenses(),
        settingsRepository.shakeEnabled
    ) { today, all, shakeEnabled ->
        DashboardState(
            todayTotal = today.sumOf { it.amount },
            todayCount = today.size,
            recentExpenses = all.take(10),
            shakeEnabled = shakeEnabled
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardState())

    fun toggleShake(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setShakeEnabled(enabled)
            if (enabled) ShakeDetectionService.start(context)
            else ShakeDetectionService.stop(context)
        }
    }

    fun syncNow() { SyncWorker.schedule(context) }
}
