package com.shakeexpense.app.domain.model

data class Expense(
    val id: String,
    val amount: Double,
    val description: String,
    val currency: String = "INR",
    val createdAt: String,
    val syncStatus: SyncStatus = SyncStatus.PENDING
)

enum class SyncStatus { PENDING, SYNCING, SYNCED, FAILED }
