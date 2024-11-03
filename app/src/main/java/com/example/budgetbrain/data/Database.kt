package com.example.budgetbrain.data

import ApiService
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.budgetbrain.models.BudgetItem
import com.example.budgetbrain.models.TransactionItem

@Database(
    entities = [UserEntity::class, BudgetEntity::class, BudgetCategoryCrossRef::class, CategoryEntity::class, TransactionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun budgetDao(): BudgetDao
    abstract fun transactionDao(): TransactionDao
}