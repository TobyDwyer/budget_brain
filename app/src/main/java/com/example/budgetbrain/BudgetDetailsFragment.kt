package com.example.budgetbrain

import ApiClient
import BudgetDetailResponse
import TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.databinding.FragmentBudgetDetailsBinding
import com.example.budgetbrain.models.BudgetDetails
import com.example.budgetbrain.models.SpendingCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class BudgetDetailsFragment : Fragment() {

    private var _binding: FragmentBudgetDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var budget: BudgetDetails
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

        val budgetId = requireArguments().getString("budgetId")
        if (budgetId != null) {
            ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.getBudget(budgetId).enqueue(object :
                Callback<BudgetDetailResponse> {
                override fun onResponse(
                    call: Call<BudgetDetailResponse>,
                    response: Response<BudgetDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        budget = response.body()!!.budget

                        binding.budgetNameEditText.setText(budget.name)
                        binding.budgetAmountEditText.setText(budget.budgetedAmount.toString())
                        binding.remainingAmountEditText.setText((budget.budgetedAmount - budget.categories.sumOf { it.amountSpent }).toString())
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        binding.startValueLbl.text = dateFormat.format(budget.startDate)
                        binding.endValueLbl.text = dateFormat.format(budget.endDate)
                        binding.spendingRecyclerView.adapter = SpendingAdapter(budget.categories)
                    } else {
                        Log.e("Failure", "Failed to get Budget List: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<BudgetDetailResponse>, t: Throwable) {
                    Log.e("Failure", "Failed to get Budget List: ${t.message}")
                }
            })
        }





        binding.spendingRecyclerView.layoutManager = LinearLayoutManager(requireContext())

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
        binding.remainingAmountEditText.isEnabled = isEnabled
        if(isEnabled) {
            binding.startDateBtn.visibility = View.VISIBLE
            binding.endDateBtn.visibility = View.VISIBLE
        }else{
            binding.startDateBtn.visibility = View.INVISIBLE
            binding.endDateBtn.visibility = View.INVISIBLE
        }

        // Optionally change the alpha for visual feedback
        val alpha = if (isEnabled) 1f else 0.5f
        binding.budgetNameTextView.alpha = alpha
        binding.budgetAmountTextView.alpha = alpha
        binding.remainingAmountTextView.alpha = alpha
        binding.startValueLbl.alpha = alpha
        binding.endValueLbl.alpha = alpha
    }

    private fun saveBudgetDetails() {
        // Implement your save logic here
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
