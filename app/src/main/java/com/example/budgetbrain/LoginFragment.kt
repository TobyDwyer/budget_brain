package com.example.budgetbrain

import ApiClient
import LoginRequest
import LoginResponse
import RegisterRequest
import RegisterResponse
import TokenManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.budgetbrain.databinding.FragmentLoginBinding
import com.example.budgetbrain.models.BiometricPromptManager
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var enrollLauncher: ActivityResultLauncher<Intent>
    private val promtManager by lazy {
        BiometricPromptManager(requireActivity() as AppCompatActivity)
    }
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enrollLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                println("Activity Result $result")
            }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Clear any previous login session to force account chooser
        binding.googleSignInButton.setOnClickListener {
            googleSignInClient.signOut().addOnCompleteListener {
                Log.d("GoogleSignIn", "Signing out to ensure account selection prompt")
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        binding.loginButton.setOnClickListener {
            try {
                val email = binding.emailEditText.text.toString().trim()
                val password = binding.passwordEditText.text.toString()

                if (email.isEmpty() || password.isEmpty()) {
                    throw IllegalArgumentException("All fields must be filled")
                }
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    throw IllegalArgumentException("Invalid email format")
                }

                val request = LoginRequest(email = email, password = password)

                ApiClient(null).apiService.login(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {

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
                                        TokenManager(requireContext()).saveAccessToken(response.body()!!.token)
                                        startActivity(
                                            Intent(
                                                requireContext(),
                                                MainActivity::class.java
                                            )
                                        )
                                    }

                                    is BiometricPromptManager.BiometricResult.AuthenticationError -> {
                                        // Handle failed biometric authentication
                                        Log.e(
                                            "BiometricAuth",
                                            "Authentication failed: ${biometricResult.error}"
                                        )
                                        // Optionally, prompt the user to retry or provide an alternative login method
                                    }

                                    BiometricPromptManager.BiometricResult.AuthenticationFailed -> TODO()
                                    BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
                                        if (Build.VERSION.SDK_INT >= 30) {
                                            val enrollIntent =
                                                Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                                                    putExtra(
                                                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                                                        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                                                    )
                                                }
                                            enrollLauncher.launch(enrollIntent)
                                        }
                                    }

                                    BiometricPromptManager.BiometricResult.FeatureUnavailable -> TODO()
                                    BiometricPromptManager.BiometricResult.HardwareUnavailable -> TODO()
                                }
                            }

                            TokenManager(requireContext()).saveAccessToken(response.body()!!.token)
                            startActivity(Intent(requireContext(), MainActivity::class.java))
                        } else {
                            Log.e("LoginError", "Error code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("LoginFailure", "Failed to login: ${t.message}")
                    }
                })
            } catch (e: Exception) {
                Log.e("LoginRequest", "Error creating request: ${e.message}")
            }
        }

        binding.signUpLink.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleSignInResult(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email ?: return
            val idToken = account.idToken ?: return

            // Attempt to log in with the Google ID token
            val loginRequest = LoginRequest(email = email, password = idToken)

            ApiClient(null).apiService.login(loginRequest)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (response.isSuccessful) {
                            // Login successful, proceed with Firebase authentication
                            firebaseAuthWithGoogle(idToken)
                        } else {
                            // Login failed, attempt to register the user
                            registerNewGoogleUser(account)
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("GoogleSignIn", "Failed to login with Google: ${t.message}")
                    }
                })
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google sign-in failed", e)
        }
    }

    private fun registerNewGoogleUser(account: GoogleSignInAccount) {
        val registerRequest = RegisterRequest(
            firstName = account.givenName ?: "Unknown",
            lastName = account.familyName ?: "Unknown",
            email = account.email ?: "",
            phoneNumber = "N/A",
            password = account.idToken ?: "default_password",
            languagePreference = "en",
            savingsGoal = 0.0
        )

        // Call the API to register the user
        ApiClient(null).apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    // Proceed with Firebase authentication
                    firebaseAuthWithGoogle(account.idToken!!)
                } else {
                    Log.e("RegisterError", "Failed to register Google user")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e("RegisterError", "Failed to register Google user: ${t.message}")
            }
        })
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
<<<<<<< Updated upstream
                    // Save the token for future authenticated requests
                    TokenManager(requireContext()).saveAccessToken(idToken)

                    // Direct the user to the main app activity
=======
                    TokenManager(requireContext()).saveAccessToken(idToken)
>>>>>>> Stashed changes
                    val mainIntent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(mainIntent)
                } else {
                    Log.e("GoogleSignIn", "Authentication Failed.")
                }
            }
    }

<<<<<<< Updated upstream
}
=======
}
>>>>>>> Stashed changes
