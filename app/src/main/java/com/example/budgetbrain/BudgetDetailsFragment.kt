package com.example.budgetbrain

import ApiClient
import BudgetDetailResponse
import TokenManager
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.databinding.FragmentBudgetDetailsBinding
import com.example.budgetbrain.models.BudgetDetails
import com.example.budgetbrain.models.SpendingCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BudgetDetailsFragment : Fragment() {

    private var _binding: FragmentBudgetDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var budget: BudgetDetails
    private val spendingList = mutableListOf<SpendingCategory>() // Example spending list

    private var isEditing = false // Track whether we are in Edit Mode
    private var isStartDatePicker = false

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
        binding.backButton1.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_budget, BudgetListFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
        }

        binding.startDateBtn.setOnClickListener {
            isStartDatePicker = true
            showDatePicker { selectedDate ->
                updateDateUI(selectedDate)
            }
        }

        binding.endDateBtn.setOnClickListener {
            isStartDatePicker = false
            showDatePicker { selectedDate ->
                updateDateUI(selectedDate)
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

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(), { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, monthOfYear, dayOfMonth)
                }.time
                onDateSelected(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateDateUI(selectedDate: Date) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if (isStartDatePicker) {
            binding.startValueLbl.text = dateFormat.format(selectedDate)
        } else {
            binding.endValueLbl.text = dateFormat.format(selectedDate)
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
        binding.remainingAmountEditText.isEnabled = false
        binding.budgetNameEditText.isEnabled = isEnabled
        binding.budgetAmountEditText.isEnabled = isEnabled
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
        // Get updated values from the EditText fields
        val updatedName = binding.budgetNameEditText.text.toString().trim()
        val updatedAmount = binding.budgetAmountEditText.text.toString().toDoubleOrNull()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val updatedStart = dateFormat.parse(binding.startValueLbl.text.toString())
        val updatedEnd = dateFormat.parse(binding.endValueLbl.text.toString())

        if (updatedName.isEmpty() || updatedAmount == null) {
            Log.e("SaveBudget", "Name or Amount cannot be empty")
            return
        }

        val updatedBudget = BudgetDetails(
            _id = budget._id,
            name = updatedName,
            budgetedAmount = budget.budgetedAmount,
            startDate = updatedStart!!,
            endDate = updatedEnd!!,
            categories = budget.categories,
            createdAt = budget.createdAt,
            remainingAmount = budget.remainingAmount
        )
        // Make the API call to update the budget
        ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.updateBudget(budget._id, updatedBudget).enqueue(object : Callback<BudgetDetailResponse> {
            override fun onResponse(call: Call<BudgetDetailResponse>, response: Response<BudgetDetailResponse>) {
                if (response.isSuccessful) {
                    // Successfully updated the budget
                    budget = response.body()!!.budget
                    setupViewMode() // Go back to view mode
                    updateUI() // Update UI with new budget details
                } else {
                    Log.e("UpdateBudgetError", "Error updating budget: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BudgetDetailResponse>, t: Throwable) {
                Log.e("UpdateBudgetFailure", "Failed to update budget: ${t.message}")
            }
        })
    }

    private fun updateUI() {
        binding.budgetNameEditText.setText(budget.name)
        binding.budgetAmountEditText.setText(budget.budgetedAmount.toString())
        binding.remainingAmountEditText.setText((budget.budgetedAmount - budget.categories.sumOf { it.amountSpent }).toString())
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.startValueLbl.text = dateFormat.format(budget.startDate)
        binding.endValueLbl.text = dateFormat.format(budget.endDate)
        binding.spendingRecyclerView.adapter = SpendingAdapter(budget.categories)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
