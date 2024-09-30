package com.example.budgetbrain.models

data class TransactionItem(
    val _id: String,
    val date: String,
    val amount: String,
    val category: String,
    val budget: String,
    val notes: String
)