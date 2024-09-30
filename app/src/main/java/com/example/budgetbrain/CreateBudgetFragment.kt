package com.example.budgetbrain

import ApiClient
import BudgetCreateRequest
import BudgetCreateResponse
import TokenManager
import android.app.DatePickerDialog
import android.content.Intent
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

        binding.createBudgetButton.setOnClickListener{createBudget()}
        binding.startDateBtn.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    startDate = selectedDate.time
                    binding.startValueLbl.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
        binding.endDateBtn.setOnClickListener{
            val datePickerDialog = DatePickerDialog(
                requireContext(), { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    endDate = selectedDate.time
                    binding.endValueLbl.text = formattedDate
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun createBudget(){
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
                startDate = Date(),
                endDate = Date(),
                budgetedAmount = budgetAmount
            )

            ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.budgetCreate(request).enqueue(object : Callback<BudgetCreateResponse> {
                override fun onResponse(
                    call: Call<BudgetCreateResponse>,
                    response: Response<BudgetCreateResponse>
                ) {
                    if (response.isSuccessful) {

                        val frag = BudgetDetailsFragment()
                        frag.arguments = arguments?: Bundle()
                        frag.requireArguments().putString("budgetId", response.body()!!.budget._id)

                        parentFragmentManager.beginTransaction()
                            .replace(R.id.nav_budget, frag)
                            .addToBackStack(null)
                            .commit()
                    } else {
                        Log.e("LoginError", "Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<BudgetCreateResponse>, t: Throwable) {
                    Log.e("LoginFailure", "Failed to login: ${t.message}")
                }
            })
        } catch (e: Exception) {
            Log.e("LoginRequest", "Error creating request: ${e.message}")
        }
    }

}
