package com.example.budgetbrain

import ApiClient
import LoginRequest
import LoginResponse
import TokenManager
import android.content.Context
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import com.example.budgetbrain.databinding.ActivityLoginBinding
import com.example.budgetbrain.models.Globals
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        if(Globals.biometricsAvailable) {
//            val sharedPreferences = getSharedPreferences("biometric_prefs", Context.MODE_PRIVATE)
//            val encryptedEmail = sharedPreferences.getString("encrypted_email", null)
//            val encryptedPassword = sharedPreferences.getString("encrypted_password", null)
//
//            if (encryptedEmail != null && encryptedPassword != null) {
//                // Biometric authentication
//                BiometricUtils.showBiometricPrompt(
//                    "Biometric Login",
//                    "Authenticate to login",
//                    "Please authenticate using biometrics",
//                    this,
//                    object : BiometricAuthListener {
//                        override fun onBiometricAuthenticateError(error: Int, errMsg: String) {
//                            Log.e("BiometricAuthError", errMsg)
//                        }
//
//                        override fun onBiometricAuthenticateSuccess(result: BiometricPrompt.AuthenticationResult) {
//                            val email = BiometricUtils.decryptData(
//                                Base64.decode(
//                                    encryptedEmail,
//                                    Base64.DEFAULT
//                                ), result.cryptoObject!!.cipher!!
//                            )
//                            val password = BiometricUtils.decryptData(
//                                Base64.decode(
//                                    encryptedPassword,
//                                    Base64.DEFAULT
//                                ), result.cryptoObject!!.cipher!!
//                            )
//
//                            val request = LoginRequest(
//                                email = email,
//                                password = password,
//                            )
//                            ApiClient(null).apiService.login(request).enqueue(object :
//                                Callback<LoginResponse> {
//                                override fun onResponse(
//                                    call: Call<LoginResponse>,
//                                    response: Response<LoginResponse>
//                                ) {
//                                    if (response.isSuccessful) {
//                                        TokenManager(this@LoginActivity).saveAccessToken(response.body()!!.token)
//                                    } else {
//                                        Log.e("LoginError", "Error code: ${response.code()}")
//                                    }
//                                }
//
//                                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
//                                    Log.e("LoginFailure", "Failed to login: ${t.message}")
//                                }
//                            })
//                        }
//                    },
//                )
//            }
//        }

        // Load LoginFragment in this activity
        supportFragmentManager.beginTransaction()
            .replace(R.id.login_fragment_container, LoginFragment())
            .commit()
    }
}