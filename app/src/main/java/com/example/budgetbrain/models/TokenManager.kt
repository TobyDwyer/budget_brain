import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveAccessToken(token: String) {
        val editor = prefs.edit()
        editor.putString("ACCESS_TOKEN", token)
        editor.apply()
    }

    fun getAccessToken(): String? {
        return prefs.getString("ACCESS_TOKEN", null)
    }

    fun removeAccessToken() {
        val editor = prefs.edit()
        editor.remove("ACCESS_TOKEN")
        editor.apply()
    }
}
