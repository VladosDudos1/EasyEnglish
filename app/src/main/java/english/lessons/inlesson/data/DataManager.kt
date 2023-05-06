package english.lessons.inlesson.data

import android.content.Context
import android.content.SharedPreferences

class DataManager(private val baseContext: Context){
    private val shared: SharedPreferences = baseContext.getSharedPreferences("cum", Context.MODE_PRIVATE)

    fun isLogin(): Boolean = shared.getBoolean("isLogin", false)

    fun endLogin() = shared.edit().putBoolean("isLogin", true).apply()

    fun logout(): Boolean = shared.edit().putBoolean("isLogin", false).commit()
}