package com.example.budgetbrain.data

import ApiService
import BudgetWriteRequest
import android.content.Context
import android.util.Log
import com.example.budgetbrain.models.BudgetItem
import com.example.budgetbrain.models.Globals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class BudgetRep(
    private val budgetDao: BudgetDao,
    private val apiService: ApiService,
) {
    suspend fun getBudgets(): List<BudgetItem> {
        try {
            // Fetch the budgets from the API
            val response = apiService.budgets().awaitResponse()
            if (response.isSuccessful) {
                response.body()?.budgets?.map {
                    it.toEntity()
                }?.let { budgets ->
                    // Insert budgets into the database in a coroutine context
                    insertBudgetsIntoDatabase(budgets)
                }
            }
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Failed to sync budgets: ${e.message}")
        }

        // Retrieve all budgets with categories from the database
        return withContext(Dispatchers.IO) {
            budgetDao.getAllBudgets().map { it.toItem()}
        }
    }

    suspend fun getBudget(budgetId: String): BudgetItem? {
        try {
            // Fetch the budgets from the API
            val response = apiService.getBudget(budgetId).awaitResponse()
            if (response.isSuccessful) {
                insertBudgetIntoDatabase(response.body()!!.budget.toEntity())
            }

        } catch (e: Exception) {
            Log.e("BudgetRepository", "Failed to sync budgets: ${e.message}")
        }

        // Retrieve all budgets with categories from the database
        return withContext(Dispatchers.IO) {
            budgetDao.getBudgetById(budgetId)?.toItem()
        }
    }

    suspend fun getBudgetCategories(budgetId: String): List<CategoryAmount> {

        return withContext(Dispatchers.IO) {
            budgetDao.getCategoryAmountsForBudget(budgetId)
        }
    }

    suspend fun createBudget(
        context: Context,
        budget: BudgetItem,
        onSuccess: (BudgetItem) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (Globals.isOnline(context)) {
            try {
                val response = apiService.budgetWrite(BudgetWriteRequest(budget)).awaitResponse()
                if (response.isSuccessful) {
                    val budgetRes = response.body()?.budget
                    if (budgetRes != null) {
                        updateBudgetIntoDatabase(budgetRes.toEntity())
                    }
                        onSuccess(budgetRes!!)
                } else {
                    onFailure("Response not successful: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to sync budgets: ${e.message}")
                onFailure("Exception occurred: ${e.message ?: "Unknown error"}")
            }
        } else {
            try {
                updateBudgetIntoDatabase(budget.toEntity(false))
                onSuccess(budget)
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to save budget locally: ${e.message}")
                onFailure("Failed to save budget locally: ${e.message ?: "Unknown error"}")
            }
        }
    }
    suspend fun updateBudget(
        context: Context,
        budget: BudgetItem,
        onSuccess: (BudgetItem) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (Globals.isOnline(context)) {
            try {
                val response = apiService.budgetWrite(BudgetWriteRequest(budget)).awaitResponse()
                if (response.isSuccessful) {
                    val budgetRes = response.body()?.budget
                    if (budgetRes != null) {
                        insertBudgetIntoDatabase(budgetRes.toEntity())
                    }
                        onSuccess(budgetRes!!)
                } else {
                    onFailure("Response not successful: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to sync budgets: ${e.message}")
                onFailure("Exception occurred: ${e.message ?: "Unknown error"}")
            }
        } else {
            // Offline mode: Save locally as unsynced
            try {
                insertBudgetIntoDatabase(budget.toEntity(false))
                onSuccess(budget) // Return the local budget
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to save budget locally: ${e.message}")
                onFailure("Failed to save budget locally: ${e.message ?: "Unknown error"}")
            }
        }
    }

    suspend fun syncUnsynced() {
        val unsyncedBudgets = budgetDao.getUnsyncedBudgets()
        unsyncedBudgets.forEach { budget ->
            val budgetEntity = budget
            try {
                val response = apiService.budgetWrite(
                    BudgetWriteRequest(budgetEntity.toItem())
                ).awaitResponse()

                if (response.isSuccessful) {
                    budgetDao.updateBudget(budgetEntity.copy(isSynced = true))
                }
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to sync budget ${budgetEntity.id}: ${e.message}")
            }
        }
    }

    private suspend fun insertBudgetsIntoDatabase(budgets: List<BudgetEntity>) {
        withContext(Dispatchers.IO) {
            budgets.forEach { budget ->
                budgetDao.insertBudget(budget) // This should be a suspend function
            }
        }
    }
    private suspend fun insertBudgetIntoDatabase(budget: BudgetEntity) {
        withContext(Dispatchers.IO) {
            budgetDao.insertBudget(budget) // This should be a suspend function
        }
    }
    private suspend fun updateBudgetIntoDatabase(budget: BudgetEntity) {
        withContext(Dispatchers.IO) {
            budgetDao.updateBudget(budget) // This should be a suspend function
        }
    }
}


fun BudgetItem.toEntity(isSynced: Boolean = true): BudgetEntity {
    return BudgetEntity(
        id = _id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        budgetedAmount = budgetedAmount,
        remainingAmount = remainingAmount,
        createdAt = createdAt,
        userId = "",  // Set user ID as appropriate
        isSynced = isSynced,
    )
}

fun BudgetEntity.toItem(): BudgetItem {
    return BudgetItem(
        _id = id,
        name = name,
        startDate = startDate,
        endDate = endDate,
        budgetedAmount = budgetedAmount,
        remainingAmount = remainingAmount,
//        categories = categoriesList, // Set categories from joined data
        createdAt = createdAt,
    )
}