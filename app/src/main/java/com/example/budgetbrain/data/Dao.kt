package com.example.budgetbrain.data

import androidx.room.*
import com.example.budgetbrain.models.BudgetItem
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getUser(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)
}

@Dao
interface BudgetDao {
    @Transaction
    @Query("SELECT * FROM budgets")
    suspend fun getAllBudgetsWithCategories(): List<BudgetWithCategories>

    @Query("SELECT * FROM budgets")
    suspend fun getAllBudgets(): List<BudgetEntity>

    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    suspend fun getBudgetById(budgetId: String): BudgetEntity?

    @Transaction
    @Query("SELECT * FROM budgets WHERE id = :budgetId")
    suspend fun getBudgetWithCategoriesById(budgetId: String): BudgetWithCategories?


    @Query("SELECT * FROM budgets WHERE is_synced = 0")
    suspend fun getUnsyncedBudgets(): List<BudgetEntity>

    @Transaction
    @Query("SELECT * FROM budgets  WHERE is_synced = 0")
    suspend fun getUnsyncedBudgetsWithCategories(): List<BudgetWithCategories>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetEntity)

    @Update
    suspend fun updateBudget(budget: BudgetEntity)
}



@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions")
    fun getAllTransactions(): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE budget_id = :budgetId")
    fun getTransactionsByBudget(budgetId: String): List<TransactionEntity>

    @Query("SELECT * FROM transactions WHERE is_synced = 0")
    suspend fun getUnsyncedTransactions(): List<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)
}