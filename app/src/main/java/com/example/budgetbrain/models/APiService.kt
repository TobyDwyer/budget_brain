import com.example.budgetbrain.models.BudgetDetails
import com.example.budgetbrain.models.BudgetItem
import com.example.budgetbrain.models.SessionUser
import com.example.budgetbrain.models.TransactionItem
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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
data class RegisterResponse(val token: String)

data class UserResponse(val user: SessionUser)
data class DashboardResponse(val presentageSaved: Int)

data class BudgetWriteRequest(val budget: BudgetItem)
data class BudgetWriteResponse(val budget : BudgetItem)
data class BudgetListResponse( val budgets : List<BudgetItem>)
data class BudgetDetailResponse( val budget : BudgetDetails)


data class TransactionWriteRequest(val transaction : TransactionItem)
data class TransactionWriteResponse(val transaction : TransactionItem)
data class TransactionListResponse( val transactions : List<TransactionItem>)

interface ApiService {
    // -------- AUTH ---------
    @POST("auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("auth/user")
    fun user(): Call<UserResponse>

    @GET("/user/details")
    fun getUserDetails(): Call<SessionUser>

    @PUT("/user/update")
    fun updateUserDetails(@Body user: SessionUser): Call<SessionUser>

    @POST("auth/dashboard")
    fun dashboard(): Call<DashboardResponse>

    // -------- BUDGETS ---------
    @POST("budgets")
    fun budgetWrite(@Body budgetWriteRequest: BudgetWriteRequest): Call<BudgetWriteResponse>

    @GET("budgets")
    fun budgets(): Call<BudgetListResponse>

    @GET("budgets/{id}")
    fun getBudget(@Path("id") id: String): Call<BudgetDetailResponse>

    @PUT("budgets")
    fun updateBudget(@Body budget: BudgetDetails): Call<BudgetDetailResponse>

    // -------- Transactions ---------
    @POST("transactions")
    fun transactionWrite(@Body transactionWriteRequest: TransactionWriteRequest): Call<TransactionWriteResponse>

    @GET("transactions")
    fun transactions(): Call<TransactionListResponse>
    abstract fun checkUserExists(email: String): Any

}


