package com.example.budgetbrain

import ApiClient
import LoginResponse
import TokenManager
import UserResponse
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.budgetbrain.databinding.ActivityMainBinding
import com.example.budgetbrain.models.Globals
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if(TokenManager(this@MainActivity).getAccessToken() != null){
            ApiClient(TokenManager(this@MainActivity).getAccessToken()).apiService.user().enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        Globals.SessionUser = response.body()?.user
                    } else {
                        TokenManager(this@MainActivity).removeAccessToken()
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    TokenManager(this@MainActivity).removeAccessToken()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))

                }
            })
        }else{
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        val profileIcon: ImageView = binding.profileIcon
        profileIcon.setOnClickListener {
            try {
                navController.navigate(R.id.action_global_settingsFragment)
            } catch (e: Exception) {
                Log.e("NavigationError", "Failed to navigate to settings fragment", e)
            } finally {
                profileIcon.isEnabled = true
            }
        }

        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }
}
