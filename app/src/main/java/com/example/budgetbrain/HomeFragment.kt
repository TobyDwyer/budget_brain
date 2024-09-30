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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.databinding.ActivityMainBinding
import com.example.budgetbrain.databinding.FragmentHomeBinding
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

        ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.budgets().enqueue(object :
            Callback<BudgetListResponse> {
            override fun onResponse(
                call: Call<BudgetListResponse>,
                response: Response<BudgetListResponse>
            ) {
                if (response.isSuccessful) {
                    binding.budgetRecyclerView.adapter = BudgetAdapter(
                        budgetList = response.body()?.budgets?: emptyList(),
                        onClick = { 0 }
                    )
                } else {
                    Log.e("Failure", "Failed to get Budget List: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BudgetListResponse>, t: Throwable) {
                Log.e("Failure", "Failed to get Budget List: ${t.message}")
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView
        binding.budgetRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        // You can set an adapter here to handle data

        // Initialize BarChart (requires a library like MPAndroidChart)
        // Set data for the BarChart here

        // Update progress bar based on savings goal
        binding.savingsGoalProgressBar.progress = 50 // Replace with actual value
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
