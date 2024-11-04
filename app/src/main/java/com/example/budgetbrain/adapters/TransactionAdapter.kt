package com.example.budgetbrain.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbrain.databinding.TransactionItemBinding
import com.example.budgetbrain.models.TransactionItem
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

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
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(transaction.date)
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        val formattedAmount = currencyFormat.format(transaction.amount)

        holder.binding.transactionDate.text = formattedDate
        holder.binding.transactionAmount.text = formattedAmount
        holder.binding.transactionCategory.text = transaction.category
        holder.binding.linkedBudget.text = transaction._id
        holder.binding.transactionNotes.text = transaction.notes
    }

    override fun getItemCount() = transactionList.size
}
