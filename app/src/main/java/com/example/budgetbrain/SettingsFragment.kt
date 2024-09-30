package com.example.budgetbrain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.budgetbrain.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var isEditing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.editSettingsButton.setOnClickListener {
            toggleEditMode()
        }

        binding.saveSettingsButton.setOnClickListener {
            // Handle saving settings logic
            toggleEditMode() // Optionally toggle back to non-edit mode after saving
        }

        // Initialize the edit texts to be non-editable
        setEditTextEnabled(false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
