package com.shakeexpense.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)

    @Query("SELECT * FROM expenses ORDER BY createdAt DESC")
    fun getAllFlow(): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses WHERE syncStatus IN ('PENDING', 'FAILED') ORDER BY createdAt ASC")
    suspend fun getPending(): List<ExpenseEntity>

    @Query("UPDATE expenses SET syncStatus = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("SELECT * FROM expenses WHERE createdAt LIKE :datePrefix || '%' ORDER BY createdAt DESC")
    fun getTodayFlow(datePrefix: String): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses WHERE id = :id")
    suspend fun delete(id: String)
}
