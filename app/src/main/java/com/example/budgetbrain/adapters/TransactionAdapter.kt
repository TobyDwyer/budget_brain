package com.example.budgetbrain.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbrain.databinding.TransactionItemBinding
import com.example.budgetbrain.models.TransactionItem

class TransactionsAdapter(private val transactionList: List<TransactionItem>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    // ViewHolder class using binding
    class TransactionViewHolder(val binding: TransactionItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        // Inflate the binding layout
        val binding = TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        // Bind data to the views
        val transaction = transactionList[position]
        holder.binding.transactionDate.text = transaction.date
        holder.binding.transactionAmount.text = transaction.amount
        holder.binding.transactionCategory.text = transaction.category
        holder.binding.linkedBudget.text = transaction.budget
        holder.binding.transactionNotes.text = transaction.notes
    }

    override fun getItemCount() = transactionList.size
}
