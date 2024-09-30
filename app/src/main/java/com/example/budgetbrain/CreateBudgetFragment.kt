package com.example.budgetbrain

import ApiClient
import BudgetCreateRequest
import BudgetCreateResponse
import TokenManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgetbrain.databinding.FragmentCreateBudgetBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date

class CreateBudgetFragment : Fragment() {

    private var _binding: FragmentCreateBudgetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateBudgetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createBudgetButton.setOnClickListener {
            try {
                val name = binding.budgetNameEditText.text.toString().trim()
                val startDate = binding.startDateEditText.text.toString()
                val endDate = binding.endDateEditText.text.toString()
                val amount = binding.budgetAmountEditText.text.toString()

                if (name.isEmpty() || amount.isEmpty()) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
