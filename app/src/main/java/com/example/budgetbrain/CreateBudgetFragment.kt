package com.example.budgetbrain

import ApiClient
import BudgetCreateRequest
import BudgetCreateResponse
import TokenManager
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.budgetbrain.databinding.FragmentCreateBudgetBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

        binding.createBudgetButton.setOnClickListener { createBudget() }

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

    private fun createBudget() {
        try {
            val name = binding.budgetNameEditText.text.toString().trim()
            val amount = binding.budgetAmountEditText.text.toString()

            if (name.isEmpty() || amount.isEmpty() || startDate == null || endDate == null) {
                throw IllegalArgumentException("All fields must be filled")
            }

            val budgetAmount = amount.toDoubleOrNull()
                ?: throw NumberFormatException("Invalid budget amount")

            val request = BudgetCreateRequest(
                name = name,
                startDate = startDate!!,
                endDate = endDate!!,
                budgetedAmount = budgetAmount
            )

            ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.budgetCreate(request).enqueue(object : Callback<BudgetCreateResponse> {
                override fun onResponse(
                    call: Call<BudgetCreateResponse>,
                    response: Response<BudgetCreateResponse>
                ) {
                    if (response.isSuccessful) {
                        val frag = BudgetDetailsFragment().apply {
                            arguments = (arguments ?: Bundle()).apply {
                                putString("budgetId", response.body()?.budget?._id)
                            }
                        }

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.nav_budget, frag)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Log.e("CreateBudgetError", "Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<BudgetCreateResponse>, t: Throwable) {
                    Log.e("CreateBudgetFailure", "Failed to create budget: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("CreateBudgetRequest", "Error creating request: ${e.message}")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
