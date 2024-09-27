package com.example.budgetbrain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.TransactionsAdapter
import com.example.budgetbrain.databinding.FragmentTransactionsBinding
import com.example.budgetbrain.models.TransactionItem

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Example transaction data
        val transactionList = listOf(
            TransactionItem("15 Sep 2024", "$120.50", "Groceries", "Monthly Expenses", "Bought weekly groceries"),
            TransactionItem("10 Sep 2024", "$45.00", "Transport", "Monthly Expenses", "Fuel for the car"),
            TransactionItem("05 Sep 2024", "$150.00", "Entertainment", "Leisure", "Concert tickets")
        )

        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.transactionRecyclerView.adapter = TransactionsAdapter(transactionList)

        binding.addTransactionFab.setOnClickListener {
            // Navigate to AddTransactionFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
