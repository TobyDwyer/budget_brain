package com.example.budgetbrain.data

import ApiService
import TransactionWriteRequest
import android.content.Context
import android.util.Log
import com.example.budgetbrain.models.Globals
import com.example.budgetbrain.models.TransactionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class TransactionRepo(
    private val transactionDao: TransactionDao,
    private val apiService: ApiService,
) {
    suspend fun getTransactions(): List<TransactionItem> {
        try {
            // Fetch the budgets from the API
            val response = apiService.transactions().awaitResponse()
            if (response.isSuccessful) {
                response.body()?.transactions?.map {
                    it.toEntity()
                }?.let { budgets ->
                    // Insert budgets into the database in a coroutine context
                    insertTransactionsIntoDatabase(budgets)
                }
            }
        } catch (e: Exception) {
            Log.e("BudgetRepository", "Failed to sync budgets: ${e.message}")
        }

        // Retrieve all budgets with categories from the database
        return withContext(Dispatchers.IO) {
            transactionDao.getAllTransactions().map { it.toItem()}
        }
    }

    suspend fun createTransaction(
        context: Context,
        transaction: TransactionItem,
        onSuccess: (TransactionItem) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (Globals.isOnline(context)) {
            // Online mode: Try to sync with API
            try {
                val response = apiService.transactionWrite(TransactionWriteRequest(transaction)).awaitResponse()
                if (response.isSuccessful) {
                    val trxRes = response.body()?.transaction
                    if (trxRes != null) {
                        insertTransactionIntoDatabase(trxRes.toEntity())
                    }
                        onSuccess(trxRes!!)
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
                insertTransactionIntoDatabase(transaction.toEntity(false))
                onSuccess(transaction) // Return the local budget
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to save budget locally: ${e.message}")
                onFailure("Failed to save budget locally: ${e.message ?: "Unknown error"}")
            }
        }
    }

    suspend fun syncUnsynced() {
        val unsyncedTransactions = transactionDao.getUnsyncedTransactions()
        unsyncedTransactions.forEach { txn ->
            val transactionEntity = txn
            try {
                val response = apiService.transactionWrite(
                    TransactionWriteRequest(transactionEntity.toItem())
                ).awaitResponse()

                if (response.isSuccessful) {
                    transactionDao.updateTransaction(transactionEntity.copy(isSynced = true))
                }
            } catch (e: Exception) {
                Log.e("BudgetRepository", "Failed to sync budget ${transactionEntity.id}: ${e.message}")
            }
        }
    }

    private suspend fun insertTransactionsIntoDatabase(transactions: List<TransactionEntity>) {
        withContext(Dispatchers.IO) {
            transactions.forEach { txn ->
                transactionDao.insertTransaction(txn) // This should be a suspend function
            }
        }
    }
    private suspend fun insertTransactionIntoDatabase(txn: TransactionEntity) {
        withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(txn) // This should be a suspend function

        }
    }
}


fun TransactionItem.toEntity(isSynced: Boolean = true): TransactionEntity {
    return TransactionEntity(
        id = _id,
        notes = notes,
        date = date,
        createdAt = createdAt,
        userId = "",
        isSynced = isSynced,
        amount = amount,
        category = category,
        budgetId = budget
    )
}

fun TransactionEntity.toItem(): TransactionItem {
    return TransactionItem(
        _id = id,
        date = date,
        createdAt = createdAt,
        amount = amount,
        category = category,
        budget = budgetId,
        notes = notes ?: ""
    )
}