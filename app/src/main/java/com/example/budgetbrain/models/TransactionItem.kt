package com.example.budgetbrain.models

import java.util.Date

data class TransactionItem(
    val _id: String,
    val date: Date,
    val createdAt: Date,
    val amount: Double,
    val category: String,
    val budgetId: String,
    val notes: String
)