package com.example.budgetbrain

import ApiClient
import BudgetWriteRequest
import BudgetWriteResponse
import TokenManager
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.budgetbrain.data.BudgetRep
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.databinding.FragmentCreateBudgetBinding
import com.example.budgetbrain.models.BudgetItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreateBudgetFragment : Fragment() {

    private var _binding: FragmentCreateBudgetBinding? = null
    private val binding get() = _binding!!

    private var startDate: Date? = null
    private var endDate: Date? = null
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createBudgetButton.setOnClickListener { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createBudget()
        }
        }

        binding.startDateBtn.setOnClickListener {
            showDatePicker { selectedDate ->
                startDate = selectedDate
                binding.startValueLbl.text = formatDate(selectedDate)
            }
        }

        binding.endDateBtn.setOnClickListener {
            showDatePicker { selectedDate ->
                endDate = selectedDate
                binding.endValueLbl.text = formatDate(selectedDate)
            }
        }

        binding.backButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_budget, BudgetListFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()

        }
    }

    private fun showDatePicker(onDateSelected: (Date) -> Unit) {
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

    private fun formatDate(date: Date): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBudget() {
        try {
            val name = binding.budgetNameEditText.text.toString().trim()
            val amount = binding.budgetAmountEditText.text.toString()

            if (name.isEmpty() || amount.isEmpty() || startDate == null || endDate == null) {
                throw IllegalArgumentException("All fields must be filled")
            }

            val budgetAmount = amount.toDoubleOrNull()
                ?: throw NumberFormatException("Invalid budget amount")

            val budget =
                BudgetItem(
                    name = name,
                    startDate = startDate!!,
                    endDate = endDate!!,
                    budgetedAmount = budgetAmount,
                    createdAt = null,
                    remainingAmount = 0.0,
                    _id = UUID.randomUUID().toString()
                )


            val appService = ApiClient(TokenManager(requireContext()).getAccessToken()).apiService
            val budgetRep = BudgetRep(DatabaseProvider.getDatabase(requireContext()).budgetDao(), apiService= appService)

            lifecycleScope.launch {
                budgetRep.createBudget(
                    context = requireContext(), // Pass in the context
                    budget = budget,
                    onSuccess = {
                        val frag = BudgetDetailsFragment().apply {
                            arguments = (arguments ?: Bundle()).apply {
                                putString("budgetId", it._id)
                            }
                        }

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.nav_budget, frag)
                            .addToBackStack(null)
                            .commit()
                    },
                    onFailure = {
                        Log.e("CreateBudgetRequest", "Something went wrong")
                    }
                )
            }


        } catch (e: Exception) {
            Log.e("CreateBudgetRequest", "Error creating request: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
