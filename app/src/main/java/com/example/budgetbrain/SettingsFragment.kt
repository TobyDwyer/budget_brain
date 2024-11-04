package com.example.budgetbrain

import ApiClient
import TokenManager
import UserResponse
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
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

        binding.saveSettingsButton.setOnClickListener {
            saveUserSettings()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }

        // Initialize the edit texts to be non-editable
        setEditTextEnabled(false)

        binding.closeButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.deleteAccount.setOnClickListener{DeleteAccount()}
    }

    private fun fetchUserSettings() {
        // Fetch user details via API
        val token = TokenManager(requireContext()).getAccessToken()
        ApiClient(token).apiService.user().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    Log.d("Settings", "User details fetched successfully")
                    user = response.body()?.user!!

                    // Update UI with user details
                    binding.firstNameEditText.setText(user.firstName)
                    binding.lastNameEditText.setText(user.lastName)
                    binding.emailEditText.setText(user.email)
                    binding.phoneEditText.setText(user.phoneNumber)
                    binding.savingsGoalEditText.setText(user.savingsGoal?.toString() ?: "")
                } else {
                    Log.e("Settings", "Failed to fetch user details: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Log.e("Settings", "Error fetching user details: ${t.message}")
            }
        })
    }

    private fun toggleEditMode() {
        isEditing = !isEditing
        setEditTextEnabled(isEditing)

        if (isEditing){
            binding.editSettingsButton.text = "Cancel"
            binding.logoutButton.visibility = View.GONE
            binding.deleteAccount.visibility = View.GONE
            binding.saveSettingsButton.visibility = View.VISIBLE
        }else{
            binding.editSettingsButton.text = "Edit"
            binding.logoutButton.visibility = View.VISIBLE
            binding.deleteAccount.visibility = View.VISIBLE
            binding.saveSettingsButton.visibility = View.GONE
        }

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
    private fun DeleteAccount(){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("http://ec2-13-53-175-17.eu-north-1.compute.amazonaws.com/login.html")
        requireContext().startActivity(intent)
    }

    private fun logout() {
        // Clear the token and navigate to login screen
        TokenManager(requireContext()).removeAccessToken()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        Log.d("Settings", "User logged out successfully")
        findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
