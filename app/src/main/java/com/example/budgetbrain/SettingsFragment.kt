package com.example.budgetbrain

import ApiClient
import TokenManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.budgetbrain.databinding.FragmentSettingsBinding
import com.example.budgetbrain.models.SessionUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var isEditing = false
    private lateinit var user: SessionUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUserSettings()

        binding.editSettingsButton.setOnClickListener {
            toggleEditMode()
        }

        // Save button to save the settings
        binding.saveSettingsButton.setOnClickListener {
            saveUserSettings()
        }

        // Initialize the edit texts to be non-editable
        setEditTextEnabled(false)

        // Close button to exit the fragment
        binding.closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun fetchUserSettings() {
        // Make API call to get the user settings
        val token = TokenManager(requireContext()).getAccessToken()
        ApiClient(token).apiService.getUserDetails().enqueue(object : Callback<SessionUser> {
            override fun onResponse(call: Call<SessionUser>, response: Response<SessionUser>) {
                if (response.isSuccessful) {
                    // Populate the user data into the form fields
                    user = response.body()!!
                    binding.firstNameEditText.setText(user.firstName)
                    binding.lastNameEditText.setText(user.lastName)
                    binding.emailEditText.setText(user.email)
                    binding.phoneEditText.setText(user.phoneNumber)
                    binding.savingsGoalEditText.setText(user.savingsGoal?.toString() ?: "")
                } else {
                    Log.e("Settings", "Failed to fetch user details: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SessionUser>, t: Throwable) {
                Log.e("Settings", "Error fetching user details: ${t.message}")
            }
        })
    }

    private fun toggleEditMode() {
        isEditing = !isEditing
        setEditTextEnabled(isEditing)
        binding.editSettingsButton.text = if (isEditing) "Cancel" else "Edit"
    }

    private fun setEditTextEnabled(enabled: Boolean) {
        val editTexts = listOf(
            binding.firstNameEditText,
            binding.lastNameEditText,
            binding.emailEditText,
            binding.phoneEditText,
            binding.savingsGoalEditText
        )
        for (editText in editTexts) {
            editText.isEnabled = enabled
        }
    }

    private fun saveUserSettings() {
        // Prepare updated user data from input fields
        user.firstName = binding.firstNameEditText.text.toString()
        user.lastName = binding.lastNameEditText.text.toString()
        user.email = binding.emailEditText.text.toString()
        user.phoneNumber = binding.phoneEditText.text.toString()
        user.savingsGoal = binding.savingsGoalEditText.text.toString().toIntOrNull()

        // Make API call to save the updated settings
        val token = TokenManager(requireContext()).getAccessToken()
        ApiClient(token).apiService.updateUserDetails(user).enqueue(object : Callback<SessionUser> {
            override fun onResponse(call: Call<SessionUser>, response: Response<SessionUser>) {
                if (response.isSuccessful) {
                    Log.d("Settings", "User details updated successfully")
                    toggleEditMode()  // Optionally toggle back to view mode after saving
                } else {
                    Log.e("Settings", "Failed to update user details: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SessionUser>, t: Throwable) {
                Log.e("Settings", "Error updating user details: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
