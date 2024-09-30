package com.example.budgetbrain

import ApiClient
import LoginResponse
import RegisterRequest
import RegisterResponse
import TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.fragment.findNavController
import com.example.budgetbrain.databinding.FragmentRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registerButton.setOnClickListener {
            try {
                val firstName = binding.firstNameEditText.text.toString().trim()
                val lastName = binding.lastNameEditText.text.toString().trim()
                val email = binding.emailEditText.text.toString().trim()
                val phoneNumber = binding.phoneEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString()
                val languagePreference = "en";
                val savingsGoalString = binding.savingsGoalEditText.text.toString().trim()

                // Basic validation
                if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    phoneNumber.isEmpty() || password.isEmpty() || languagePreference.isEmpty() ||
                    savingsGoalString.isEmpty()) {
                    throw IllegalArgumentException("All fields must be filled")
                }

                // Email validation
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    throw IllegalArgumentException("Invalid email format")
                }

                // Phone number validation (basic example, adjust as needed)
                if (!phoneNumber.matches(Regex("^[+]?[0-9]{10,13}$"))) {
                    throw IllegalArgumentException("Invalid phone number format")
                }

                // Password strength check (example: at least 8 characters)
                if (password.length < 8) {
                    throw IllegalArgumentException("Password must be at least 8 characters long")
                }

                // Safe conversion to Double
                val savingsGoal = savingsGoalString.toDoubleOrNull()
                    ?: throw NumberFormatException("Invalid savings goal amount")

                val request = RegisterRequest(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber,
                    password = password,
                    languagePreference = languagePreference,
                    savingsGoal = savingsGoal
                )

                 ApiClient(null).apiService.register(request).enqueue(object : Callback<RegisterResponse> {
                     override fun onResponse(
                         call: Call<RegisterResponse>,
                         response: Response<RegisterResponse>
                     ) {
                         if (response.isSuccessful) {
                             TokenManager(requireContext()).saveAccessToken(response.body()!!.token)
                             parentFragmentManager.beginTransaction()
                                 .replace(R.id.nav_host_fragment,HomeFragment())
                                 .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                 .commit();
                         } else {
                             Log.e("RegisterError", "Error code: ${response.code()}")
                         }
                     }

                     override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                         Log.e("RegisterFailure", "Failed to register: ${t.message}")
                     }
                 })
            } catch (e: Exception) {
                Log.e("RegisterRequest", "Error creating request: ${e.message}")
            }
        }


        binding.loginLink.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment,LoginFragment())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
