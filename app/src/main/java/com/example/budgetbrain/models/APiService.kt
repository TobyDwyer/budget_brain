import com.example.budgetbrain.models.BudgetItem
import com.example.budgetbrain.models.SessionUser
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date

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
data class RegisterResponse(val token: String)

data class UserResponse(val user: SessionUser)

data class BudgetCreateRequest(
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val budgetedAmount: Double,
    val categories: List<String> = emptyList(),
)
data class BudgetCreateResponse( val budget : BudgetItem)
data class BudgetListResponse( val budgets : List<BudgetItem>)


interface ApiService {
    // -------- AUTH ---------
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("auth/user")
    fun user(): Call<UserResponse>

    // -------- BUDGETS ---------
    @POST("budgets")
    fun budgetCreate(@Body budgetCreateRequest: BudgetCreateRequest): Call<BudgetCreateResponse>

    @GET("budgets")
    fun budgets(): Call<BudgetListResponse>
}


