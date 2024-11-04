package com.example.budgetbrain

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.budgetbrain.data.CategoryAmount
import com.example.budgetbrain.databinding.ItemSpendingCategoryBinding
import com.example.budgetbrain.models.SpendingCategory

class SpendingAdapter(
    private val spendingList: List<CategoryAmount>
) : RecyclerView.Adapter<SpendingAdapter.SpendingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpendingViewHolder {
        val binding = ItemSpendingCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return SpendingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SpendingViewHolder, position: Int) {
        val category = spendingList[position]
        holder.bind(category)
    }

    override fun getItemCount(): Int = spendingList.size

    class SpendingViewHolder(private val binding: ItemSpendingCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryAmount) {
            binding.categoryNameTextView.text = category.categoryName
            binding.amountSpentTextView.text = "$${category.totalTransacted}"
        }
    }
}
