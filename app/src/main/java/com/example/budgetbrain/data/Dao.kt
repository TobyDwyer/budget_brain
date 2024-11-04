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

    @Query("SELECT b.id, b.budgeted_amount, b.user_id, b.created_at, b.end_date, b.is_synced, b.name, b.start_date, (b.budgeted_amount - COALESCE(SUM(t.amount), 0)) AS remaining_amount " +
            "FROM budgets b LEFT JOIN transactions t ON b.id = t.budget_id " +
            "GROUP BY b.id")
    suspend fun getAllBudgets(): List<BudgetEntity>

    @Query("SELECT b.id, b.budgeted_amount, b.user_id, b.created_at, b.end_date, b.is_synced, b.name, b.start_date, (b.budgeted_amount - COALESCE(SUM(t.amount), 0)) AS remaining_amount " +
            "FROM budgets b " +
            "LEFT JOIN transactions t ON b.id = t.budget_id " +
            "WHERE b.id = :budgetId " +
            "GROUP BY b.id")
    suspend fun getBudgetById(budgetId: String): BudgetEntity?

    @Query("""
        SELECT t.category AS categoryName, COALESCE(SUM(t.amount), 0) AS totalTransacted
        FROM transactions t 
        WHERE t.budget_id = :budgetId
        GROUP BY t.category
    """)
    suspend fun getCategoryAmountsForBudget(budgetId: String): List<CategoryAmount>


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