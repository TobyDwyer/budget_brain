package com.example.budgetbrain

import ApiClient
import BudgetListResponse
import TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.replace
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.databinding.FragmentBudgetBinding
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
//        childFragmentManager.beginTransaction().replace(R.id.nav_budget, BudgetListFragment()).commit()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
