package com.example.budgetbrain

import ApiClient
import BudgetListResponse
import DashboardResponse
import TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.data.BudgetRep
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.databinding.ActivityMainBinding
import com.example.budgetbrain.databinding.FragmentHomeBinding
import com.example.budgetbrain.models.BudgetItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.dashboard().enqueue(object :
            Callback<DashboardResponse> {
            override fun onResponse(
                call: Call<DashboardResponse>,
                response: Response<DashboardResponse>
            ) {
                if (response.isSuccessful) {
                    binding.savingsGoalProgressBar.progress = response.body()!!.presentageSaved
                } else {
                    Log.e("Failure", "Failed to get Dashboard: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                Log.e("Failure", "Failed to get Dashboard: ${t.message}")
            }
        })

        val apiService = ApiClient(TokenManager(requireContext()).getAccessToken()).apiService
        val budgetRep = BudgetRep(DatabaseProvider.getDatabase(requireContext()).budgetDao(),apiService)
        viewLifecycleOwner.lifecycleScope.launch {
            val budgetList = budgetRep.getBudgets()
            setupRecyclerView(budgetList)
        }

        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.savingsGoalProgressBar.progress = 50 // Replace with actual value
    }

    private fun setupRecyclerView(budgetList: List<BudgetItem>) {
        binding.budgetRecyclerView.adapter = BudgetAdapter(
            budgetList = budgetList,
            onClick = {0}
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
