package com.example.budgetbrain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.databinding.ActivityMainBinding
import com.example.budgetbrain.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
