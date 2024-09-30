package com.example.budgetbrain.models

import java.util.Date

data class BudgetItem(
    val _id: String,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val budgetedAmount: Double,
    val remainingAmount: Double,
    val categories: List<String>,
    val createdAt: Date
)
