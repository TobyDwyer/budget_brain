package com.example.budgetbrain

import ApiClient
import BudgetListResponse
import TokenManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.databinding.FragmentBudgetListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetListFragment : Fragment() {
    private var _binding: FragmentBudgetListBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBudgetListBinding.inflate(inflater, container, false)
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
                    binding.budgetListRecyclerView.adapter = BudgetAdapter(
                        budgetList = response.body()?.budgets?: emptyList(),
                        onClick = {
                            val frag = BudgetDetailsFragment()
                            frag.arguments = arguments?: Bundle()
                            frag.requireArguments().putString("budgetId", it)

                            parentFragmentManager.beginTransaction()
                                .replace(R.id.nav_budget, frag)
                                .addToBackStack(null)
                                .commit()
                        }
                    )
                } else {
                    Log.e("Failure", "Failed to get Budget List: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BudgetListResponse>, t: Throwable) {
                Log.e("Failure", "Failed to get Budget List: ${t.message}")
            }
        })

        binding.budgetListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.addBudgetFab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_budget, CreateBudgetFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}