package com.example.budgetbrain.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbrain.R
import com.example.budgetbrain.models.TransactionItem

class TransactionsAdapter(private val transactionList: List<TransactionItem>) :
    RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.transactionDateEditText)
        val amount: TextView = itemView.findViewById(R.id.transactionAmountEditText)
        val category: TextView = itemView.findViewById(R.id.transactionCategoryEditText)
        val budget: TextView = itemView.findViewById(R.id.transactionBudget)
        val notes: TextView = itemView.findViewById(R.id.transactionNotesEditText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactionList[position]
        holder.date.text = transaction.date
        holder.amount.text = transaction.amount
        holder.category.text = transaction.category
        holder.budget.text = transaction.budget
        holder.notes.text = transaction.notes
    }

    override fun getItemCount() = transactionList.size
}
