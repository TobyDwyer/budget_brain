package com.example.budgetbrain

import ApiClient
import BudgetListResponse
import TokenManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.adapters.TransactionsAdapter
import com.example.budgetbrain.databinding.FragmentBudgetBinding
import com.example.budgetbrain.models.Globals
import com.example.budgetbrain.models.TransactionItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetFragment : Fragment() {

    private var _binding: FragmentBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.budgets().enqueue(object :
            Callback<BudgetListResponse> {
            override fun onResponse(
                call: Call<BudgetListResponse>,
                response: Response<BudgetListResponse>
            ) {
                if (response.isSuccessful) {
                    binding.budgetListRecyclerView.adapter = BudgetAdapter(response.body()?.budgets?: emptyList())
                } else {
                    Log.e("Failure", "Failed to get Budget List: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BudgetListResponse>, t: Throwable) {
                Log.e("Failure", "Failed to get Budget List: ${t.message}")
            }
        })

        binding.budgetListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Set adapter for RecyclerView

        binding.addBudgetFab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, CreateBudgetFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        loadBudgets()
    }

    private fun loadBudgets() {

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
