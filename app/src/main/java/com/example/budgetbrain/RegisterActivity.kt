package com.example.budgetbrain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbrain.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load RegisterFragment in this activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.register_fragment_container, RegisterFragment())
            .commit()
    }
}
