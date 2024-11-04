package com.example.budgetbrain

import ApiClient
import TokenManager
import TransactionListResponse
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.adapters.TransactionsAdapter
import com.example.budgetbrain.data.BudgetRep
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.data.TransactionRepo
import com.example.budgetbrain.databinding.FragmentTransactionsBinding
import com.example.budgetbrain.databinding.FragmentTransactionsListBinding
import com.example.budgetbrain.models.BudgetItem
import com.example.budgetbrain.models.TransactionItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionsListFragment : Fragment() {

    private var _binding: FragmentTransactionsListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsListBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val apiService = ApiClient(TokenManager(requireContext()).getAccessToken()).apiService
        val transactionRepo = TransactionRepo(DatabaseProvider.getDatabase(requireContext()).transactionDao(),apiService)
        viewLifecycleOwner.lifecycleScope.launch {
            val transactionList = transactionRepo.getTransactions()
            setupRecyclerView(transactionList)
        }
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        binding.addTransactionFab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_transaction, CreateTransactionFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    private fun setupRecyclerView(transactionList: List<TransactionItem>) {
        binding.transactionRecyclerView.adapter = TransactionsAdapter(transactionList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}