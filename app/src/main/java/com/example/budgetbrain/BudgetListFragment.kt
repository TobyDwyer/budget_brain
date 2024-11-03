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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.data.BudgetRep
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.data.toItem
import com.example.budgetbrain.databinding.FragmentBudgetListBinding
import com.example.budgetbrain.models.BudgetItem
import kotlinx.coroutines.launch
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

        val apiService = ApiClient(TokenManager(requireContext()).getAccessToken()).apiService
        val budgetRep = BudgetRep(DatabaseProvider.getDatabase(requireContext()).budgetDao(),apiService)
        viewLifecycleOwner.lifecycleScope.launch {
            val budgetList = budgetRep.getBudgets()
            setupRecyclerView(budgetList)
        }

        binding.budgetListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.addBudgetFab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_budget, CreateBudgetFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }
    }

    private fun setupRecyclerView(budgetList: List<BudgetItem>) {
        binding.budgetListRecyclerView.adapter = BudgetAdapter(
            budgetList = budgetList,
            onClick = { budgetId ->
                val frag = BudgetDetailsFragment()
                frag.arguments = arguments ?: Bundle()
                frag.requireArguments().putString("budgetId", budgetId)

                parentFragmentManager.beginTransaction()
                    .replace(R.id.nav_budget, frag)
                    .addToBackStack(null)
                    .commit()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}