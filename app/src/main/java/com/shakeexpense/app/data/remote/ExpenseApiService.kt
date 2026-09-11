package com.shakeexpense.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

data class ExpenseRequest(
    val expenseId: String,
    val amount: Double,
    val description: String,
    val currency: String,
    val clientTimestamp: String,
    val token: String
)

data class ExpenseResponse(
    val success: Boolean,
    val expenseId: String?,
    val message: String?
)

interface ExpenseApiService {
    @POST("/")
    suspend fun saveExpense(@Body request: ExpenseRequest): ExpenseResponse
}
