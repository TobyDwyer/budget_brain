package com.example.budgetbrain.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbrain.databinding.ItemBudgetBinding
import com.example.budgetbrain.models.BudgetItem

class BudgetAdapter(private val budgetList: List<BudgetItem>, private val onClick: (String) -> Int) :
    RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder>() {

    // ViewHolder class using binding
    class BudgetViewHolder(val binding: ItemBudgetBinding ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        // Inflate the binding layout
        val binding = ItemBudgetBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        binding.root
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        // Bind data to the views
        val budget = budgetList[position]
        holder.binding.root.setOnClickListener{onClick(budget._id)}
        holder.binding.budgetAmountTextView.text = budget.budgetedAmount.toString()
        holder.binding.budgetNameTextView.text = budget.name
        holder.binding.budgetRemainingTextView.text = budget.remainingAmount.toString()
    }

    override fun getItemCount() = budgetList.size
}
