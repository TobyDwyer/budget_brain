package com.example.budgetbrain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.databinding.FragmentBudgetDetailsBinding
import com.example.budgetbrain.models.SpendingCategory

class BudgetDetailsFragment : Fragment() {

    private var _binding: FragmentBudgetDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var spendingAdapter: SpendingAdapter
    private val spendingList = mutableListOf<SpendingCategory>() // Example spending list

    private var isEditing = false // Track whether we are in Edit Mode

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBudgetDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up RecyclerView with adapter
        spendingAdapter = SpendingAdapter(spendingList)
        binding.spendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.spendingRecyclerView.adapter = spendingAdapter

        setupViewMode()

        binding.editBudgetButton.setOnClickListener {
            if (isEditing) {
                saveBudgetDetails()
                setupViewMode()
            } else {
                setupEditMode()
            }
        }

        binding.deleteBudgetButton.setOnClickListener {
            // Handle delete logic here
        }

        binding.backButton.setOnClickListener {
            // Revert to View Mode without saving changes
            setupViewMode()
        }
    }

    private fun setupEditMode() {
        isEditing = true
        binding.editBudgetButton.text = "Save"
        binding.deleteBudgetButton.visibility = View.GONE
        binding.backButton.visibility = View.VISIBLE

        enableFields(true)
    }

    private fun setupViewMode() {
        isEditing = false
        binding.editBudgetButton.text = "Edit"
        binding.deleteBudgetButton.visibility = View.VISIBLE
        binding.backButton.visibility = View.GONE

        enableFields(false)
    }

    private fun enableFields(isEnabled: Boolean) {
        // Make EditText fields editable or not
        binding.budgetNameEditText.isEnabled = isEnabled
        binding.budgetAmountEditText.isEnabled = isEnabled
        binding.totalAmountEditText.isEnabled = isEnabled
        binding.remainingAmountEditText.isEnabled = isEnabled
        binding.startDateEditText.isEnabled = isEnabled
        binding.endDateEditText.isEnabled = isEnabled

        // Optionally change the alpha for visual feedback
        val alpha = if (isEnabled) 1f else 0.5f
        binding.budgetNameTextView.alpha = alpha
        binding.budgetAmountTextView.alpha = alpha
        binding.totalAmountTextView.alpha = alpha
        binding.remainingAmountTextView.alpha = alpha
        binding.startDateTextView.alpha = alpha
        binding.endDateTextView.alpha = alpha
    }

    private fun saveBudgetDetails() {
        // Implement your save logic here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
