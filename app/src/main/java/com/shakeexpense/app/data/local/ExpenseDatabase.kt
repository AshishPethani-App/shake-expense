package com.shakeexpense.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ExpenseEntity::class], version = 1, exportSchema = false)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun dao(): ExpenseDao
}
