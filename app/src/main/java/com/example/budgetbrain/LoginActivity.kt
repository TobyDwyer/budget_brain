package com.example.budgetbrain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.budgetbrain.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load LoginFragment in this activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_fragment_container, LoginFragment())
            .commit()
    }
}