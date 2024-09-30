import com.example.budgetbrain.models.SessionUser
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val languagePreference: String,
    val savingsGoal: Double
)

data class UserResponse(val user: SessionUser)

data class RegisterResponse(val token: String)
interface ApiService {
    @POST("api/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("api/auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/user")
    fun user(): Call<UserResponse>
}


