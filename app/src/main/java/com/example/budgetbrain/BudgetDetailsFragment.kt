package com.example.budgetbrain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgetbrain.databinding.FragmentBudgetDetailsBinding

class BudgetDetailsFragment : Fragment() {

    private var _binding: FragmentBudgetDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editBudgetButton.setOnClickListener {
            // Navigate to EditBudgetFragment or toggle edit mode
        }

        binding.deleteBudgetButton.setOnClickListener {
            // Handle delete logic here
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
