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
data class DashboardResponse(val presentageSaved: Int)

data class BudgetCreateRequest(
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val budgetedAmount: Double,
    val categories: List<String> = emptyList(),
)
data class BudgetCreateResponse( val budget : BudgetItem)
data class BudgetListResponse( val budgets : List<BudgetItem>)
data class BudgetDetailResponse( val budget : BudgetDetails)


data class TransactionCreateRequest(
    val date: Date,
    val amount: Double,
    val category: String,
    val budgetId: String,
    val notes: String
)
data class TransactionCreateResponse( val transaction : TransactionItem)
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
    fun budgetCreate(@Body budgetCreateRequest: BudgetCreateRequest): Call<BudgetCreateResponse>

    @GET("budgets")
    fun budgets(): Call<BudgetListResponse>

    @GET("budgets/{id}")
    fun getBudget(@Path("id") id: String): Call<BudgetDetailResponse>

    @PUT("budgets/{id}")
    fun updateBudget(@Path("id") budgetId: String, @Body budget: BudgetDetails): Call<BudgetDetailResponse>

    // -------- Transactions ---------
    @POST("transactions")
    fun transactionCreate(@Body budgetCreateRequest: TransactionCreateRequest): Call<TransactionCreateResponse>

    @GET("transactions")
    fun transactions(): Call<TransactionListResponse>


}


