import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class ApiClient(private val token: String?) {

    companion object {
        private const val BASE_URL = "http://192.168.1.108:2323/api/"
//        private const val BASE_URL = "http://ec2-13-53-175-17.eu-north-1.compute.amazonaws.com/api/"
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .callTimeout(5, TimeUnit.MINUTES)
        .readTimeout(5, TimeUnit.MINUTES)
        .writeTimeout(5, TimeUnit.MINUTES)
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
            if (token != null) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)

        .addConverterFactory(GsonConverterFactory.create())
        .client(httpClient)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
