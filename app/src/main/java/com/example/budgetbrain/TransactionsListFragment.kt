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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.adapters.TransactionsAdapter
import com.example.budgetbrain.databinding.FragmentTransactionsBinding
import com.example.budgetbrain.databinding.FragmentTransactionsListBinding
import com.example.budgetbrain.models.TransactionItem
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

        // Initialize the transaction list
        val transactionList = emptyList<TransactionItem>()
        ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.transactions().enqueue(object :
            Callback<TransactionListResponse> {
            override fun onResponse(
                call: Call<TransactionListResponse>,
                response: Response<TransactionListResponse>
            ) {
                if (response.isSuccessful) {
                    binding.transactionRecyclerView.adapter = TransactionsAdapter(
                        transactionList = response.body()?.transactions?: emptyList()
                    )
                } else {
                    Log.e("Failure", "Failed to get Transaction List: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<TransactionListResponse>, t: Throwable) {
                Log.e("Failure", "Failed to get Transaction List: ${t.message}")
            }
        })

        // Setup RecyclerView
        binding.transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = TransactionsAdapter(transactionList)
        binding.transactionRecyclerView.adapter = adapter

        binding.addTransactionFab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_transaction, CreateTransactionFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}