package com.shakeexpense.app.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shakeexpense.app.data.repository.ExpenseRepository
import com.shakeexpense.app.domain.model.Expense
import com.shakeexpense.app.domain.model.SyncStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class ExpenseUiState(
    val amount: String = "",
    val description: String = "",
    val amountError: String? = null,
    val descriptionError: String? = null,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val repository: ExpenseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    fun onAmountChange(v: String) { _uiState.update { it.copy(amount = v, amountError = null) } }
    fun onDescriptionChange(v: String) { _uiState.update { it.copy(description = v, descriptionError = null) } }

    fun saveExpense() {
        val state = _uiState.value
        val amount = state.amount.trim().toDoubleOrNull()
        val description = state.description.trim()

        var valid = true
        if (amount == null || amount <= 0) {
            _uiState.update { it.copy(amountError = "Please enter a valid amount.") }
            valid = false
        }
        if (description.isBlank()) {
            _uiState.update { it.copy(descriptionError = "Please enter what this expense was for.") }
            valid = false
        }
        if (description.length > 200) {
            _uiState.update { it.copy(descriptionError = "Description too long (max 200 chars).") }
            valid = false
        }
        if (!valid) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val now = LocalDateTime.now()
                val ts = now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                val expenseId = "EXP-" + now.format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) +
                    "-" + UUID.randomUUID().toString().take(5).uppercase()
                val expense = Expense(
                    id = expenseId,
                    amount = amount!!,
                    description = description,
                    currency = "INR",
                    createdAt = ts,
                    syncStatus = SyncStatus.PENDING
                )
                repository.saveExpense(expense)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false,
                    errorMessage = "Unable to save right now. Please try again.") }
            }
        }
    }

    fun reset() { _uiState.value = ExpenseUiState() }
}
