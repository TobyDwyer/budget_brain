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
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.budgetbrain.data.AppDatabase
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.databinding.ActivityMainBinding
import com.example.budgetbrain.models.Globals
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging
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

        // Initialize notification channel
        NotificationHelper.createNotificationChannel(this)

        // Retrieve and save FCM token
        val appService = ApiClient(TokenManager(this).getAccessToken()).apiService
        lifecycleScope.launch{
            BudgetRep(DatabaseProvider.getDatabase(this@MainActivity).budgetDao(), apiService= appService).syncUnsynced()
            TransactionRepo(DatabaseProvider.getDatabase(this@MainActivity).transactionDao(), apiService= appService).syncUnsynced()
        }


        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM token
            val fcmToken = task.result
            Log.d("FCM", "FCM Token: $fcmToken")

            // Save FCM token to shared preferences or send it to your backend
            TokenManager(this).saveAccessToken(fcmToken)
        }

        // Check if user is logged in by validating the access token
        if (TokenManager(this).getAccessToken() != null) {
            ApiClient(TokenManager(this).getAccessToken()).apiService.user().enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        Globals.SessionUser = response.body()?.user
                        // Show login notification
                        NotificationHelper.showNotification(this@MainActivity, "Login Successful", "Welcome back to BudgetBrain!")
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
        } else {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
        }

        // Setup profile icon click listener
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

        // Setup bottom navigation
        val bottomNavigationView: BottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)
    }

    public fun loginWithToken(){
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
                Log.e("LoginWithToken", "Failed to fetch user details: ${t.message}")
                TokenManager(this@MainActivity).removeAccessToken()
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))

            }
        })
    }


}
