package com.example.budgetbrain

import ApiClient
import BudgetListResponse
import TokenManager
import TransactionWriteResponse
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.example.budgetbrain.adapters.BudgetSpinAdapter
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.data.TransactionRepo
import com.example.budgetbrain.databinding.FragmentCreateTransactionBinding
import com.example.budgetbrain.models.BudgetItem
import com.example.budgetbrain.models.TransactionItem
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CreateTransactionFragment : Fragment() {

    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    private var date: Date? = null
    val categories = arrayOf("Food", "Savings", "Leisure", "Essentials", "Other")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        binding.transactionCategorySpn.adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories)

        ApiClient(TokenManager(requireContext()).getAccessToken()).apiService.budgets().enqueue(object :
            Callback<BudgetListResponse> {
            override fun onResponse(
                call: Call<BudgetListResponse>,
                response: Response<BudgetListResponse>
            ) {
                if (response.isSuccessful) {
                    val adapter = BudgetSpinAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        response.body()!!.budgets
                    )
                    binding.transactionBudgetSpn.adapter = adapter
                } else {
                    Log.e("Failure", "Failed to get Budget List: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<BudgetListResponse>, t: Throwable) {
                Log.e("Failure", "Failed to get Budget List: ${t.message}")
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dateBtn.setOnClickListener {
            showDatePicker { selectedDate ->
                date = selectedDate
                binding.dateValueLbl.text = formatDate(selectedDate)
            }
        }


        binding.saveTransactionButton.setOnClickListener {
            createTxn()
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

    private fun createTxn() {
        try {
            val amount = binding.transactionAmountEditText.text.toString()
            val category = binding.transactionCategorySpn.selectedItem.toString().trim()
            val notes = binding.transactionNotesEditText.text.toString()
            val budget = binding.transactionBudgetSpn.selectedItem


            if (category.isEmpty() || amount.isEmpty() || notes.isEmpty() ||date == null) {
                throw IllegalArgumentException("All fields must be filled")
            }

            val dAmount = amount.toDoubleOrNull()
                ?: throw NumberFormatException("Invalid budget amount")

            val txn = TransactionItem(

                date = date ?: Date(),
                amount = dAmount,
                notes = notes,
                budget = (budget as BudgetItem)._id,
                category = category,
                createdAt = Date(),
                _id = UUID.randomUUID().toString()
            )

            val apiService = ApiClient(TokenManager(requireContext()).getAccessToken()).apiService
            val txnRepo = TransactionRepo(DatabaseProvider.getDatabase(requireContext()).transactionDao(),apiService)
            lifecycleScope.launch {
                txnRepo.createTransaction(
                    requireContext(), txn,
                    onSuccess = {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.nav_transaction, TransactionsListFragment())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit()
                    },
                    onFailure = TODO()
                )
            }
//                .transactionWrite(request).enqueue(object :
//                Callback<TransactionWriteResponse> {
//                override fun onResponse(
//                    call: Call<TransactionWriteResponse>,
//                    response: Response<TransactionWriteResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        parentFragmentManager.beginTransaction()
//                            .replace(R.id.nav_transaction, TransactionsListFragment())
//                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
//                            .commit()
//                    } else {
//                        Log.e("CreateTransactionError", "Error code: ${response.code()}")
//                    }
//                }
//
//                override fun onFailure(call: Call<TransactionWriteResponse>, t: Throwable) {
//                    Log.e("CreateTransactionFailure", "Failed to create transaction: ${t.message}")
//                }
//            })

        } catch (e: Exception) {
            Log.e("CreateTransactionRequest", "Error creating request: ${e.message}")
        }
    }

}