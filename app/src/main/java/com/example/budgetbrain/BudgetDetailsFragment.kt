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
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetbrain.adapters.BudgetAdapter
import com.example.budgetbrain.data.BudgetRep
import com.example.budgetbrain.data.CategoryAmount
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.databinding.FragmentBudgetDetailsBinding
import com.example.budgetbrain.models.BudgetDetails
import com.example.budgetbrain.models.BudgetItem
import com.example.budgetbrain.models.SpendingCategory
import kotlinx.coroutines.launch
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

    private var budget: BudgetDetails? = null

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
            val apiService = ApiClient(TokenManager(requireContext()).getAccessToken()).apiService
            val budgetRep = BudgetRep(DatabaseProvider.getDatabase(requireContext()).budgetDao(),apiService)
            viewLifecycleOwner.lifecycleScope.launch {
                val b = budgetRep.getBudget(budgetId)
                val cat = budgetRep.getBudgetCategories(budgetId)
                if (b != null) {
                    budget = BudgetDetails(
                        _id = b._id,
                        createdAt = b.createdAt,
                        budgetedAmount = b.budgetedAmount,
                        categories = cat,
                        name = b.name,
                        startDate = b.startDate,
                        remainingAmount = b.remainingAmount,
                        endDate = b.endDate
                    )
                }else{
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_budget, BudgetListFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit()
                }

                updateUI()
            }
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

        }

        binding.backButton.setOnClickListener {
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

        val updatedBudget = BudgetItem(
            _id = budget!!._id,
            name = updatedName,
            budgetedAmount = budget!!.budgetedAmount,
            startDate = updatedStart!!,
            endDate = updatedEnd!!,
            createdAt = budget!!.createdAt,
            remainingAmount = budget!!.remainingAmount
        )
        val apiService = ApiClient(TokenManager(requireContext()).getAccessToken()).apiService
        val budgetRep = BudgetRep(DatabaseProvider.getDatabase(requireContext()).budgetDao(),apiService)
        viewLifecycleOwner.lifecycleScope.launch {
            budgetRep.updateBudget(
                requireContext(), updatedBudget,
                onSuccess = {
                    viewLifecycleOwner.lifecycleScope.launch {
                        val b = budgetRep.getBudget(it._id)
                        val cat = budgetRep.getBudgetCategories(it._id)
                        if (b != null) {
                            budget = BudgetDetails(
                                _id = b._id,
                                createdAt = b.createdAt,
                                budgetedAmount = b.budgetedAmount,
                                categories = cat,
                                name = b.name,
                                startDate = b.startDate,
                                remainingAmount = b.remainingAmount,
                                endDate = b.endDate
                            )
                            setupViewMode()
                            updateUI()
                        }
                    }
                },
                onFailure = {
                    Toast.makeText(
                        requireContext(),
                        "Failed to update budget: $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

    }

    private fun updateUI() {
        if(budget != null) {
            binding.budgetNameEditText.setText(budget!!.name)
            binding.budgetAmountEditText.setText(budget!!.budgetedAmount.toString())
            binding.remainingAmountEditText.setText((budget!!.budgetedAmount - budget!!.categories.sumOf { it.totalTransacted }).toString())
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            binding.startValueLbl.text = dateFormat.format(budget!!.startDate)
            binding.endValueLbl.text = dateFormat.format(budget!!.endDate)
            binding.spendingRecyclerView.adapter = SpendingAdapter(budget!!.categories)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
