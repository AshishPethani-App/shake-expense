package com.shakeexpense.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shakeexpense.app.domain.model.Expense
import com.shakeexpense.app.domain.model.SyncStatus

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val description: String,
    val currency: String,
    val createdAt: String,
    val syncStatus: String
) {
    fun toDomain() = Expense(id, amount, description, currency, createdAt,
        SyncStatus.valueOf(syncStatus))
}

fun Expense.toEntity() = ExpenseEntity(id, amount, description, currency, createdAt,
    syncStatus.name)
