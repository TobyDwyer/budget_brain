package com.example.budgetbrain

import ApiClient
import LoginResponse
import TokenManager
import UserResponse
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.room.Room
import com.example.budgetbrain.data.AppDatabase
import com.example.budgetbrain.data.BudgetRep
import com.example.budgetbrain.data.DatabaseProvider
import com.example.budgetbrain.data.TransactionRepo
import com.example.budgetbrain.databinding.ActivityMainBinding
import com.example.budgetbrain.models.BiometricPromptManager
import com.example.budgetbrain.models.Globals
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.KeyStore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var  enrollLauncher : ActivityResultLauncher<Intent>
    private val promtManager by lazy {
        BiometricPromptManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enrollLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            println("Activity Result $result")
        }
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
//            TokenManager(this).saveAccessToken(fcmToken)
        }
        if(TokenManager(this).getAccessToken() != null){
            lifecycleScope.launch {
                // Show the biometric prompt
                promtManager.showBiometricPompt(
                    title = "Biometric Login",
                    description = "Please authenticate using biometrics"
                )

                // Wait for biometric result
                val biometricResult = promtManager.getBiometricResult()
                when (biometricResult) {
                    is BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
                        Log.d("BiometricAuth", "Authentication succeeded")
                        if(Globals.isOnline(this@MainActivity)){
                            if (TokenManager(this@MainActivity).getAccessToken() != null) {
                                loginWithToken()
                            } else {
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            }
                        }
                    }
                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                        Log.e("BiometricAuth", "Authentication failed: ${biometricResult.error}")
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric authentication failed. Please try again.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
                        Log.e("BiometricAuth", "Biometric authentication failed.")
                        Toast.makeText(
                            this@MainActivity,
                            "Authentication failed. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                        if (Build.VERSION.SDK_INT >= 30) {
                            val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                putExtra(
                                    Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                    BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                )
                            }
                            enrollLauncher.launch(enrollIntent)
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                "Biometric authentication is not set up. Please enable it in settings.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
                        Log.e("BiometricAuth", "Biometric feature is unavailable.")
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric feature is unavailable on this device.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
                        Log.e("BiometricAuth", "Biometric hardware is unavailable.")
                        Toast.makeText(
                            this@MainActivity,
                            "Biometric hardware is currently unavailable. Please try again later.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
        else{
            TokenManager(this@MainActivity).removeAccessToken()
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

    private fun loginWithToken(){
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
    }


}
