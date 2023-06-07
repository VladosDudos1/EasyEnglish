package english.lessons.inlesson.data

import android.content.Context
import android.content.SharedPreferences

class DataManager(private val baseContext: Context) {
    private val shared: SharedPreferences =
        baseContext.getSharedPreferences("cum", Context.MODE_PRIVATE)

    fun isLogin(): Boolean = shared.getBoolean("isLogin", false)

    fun endLogin() = shared.edit().putBoolean("isLogin", true).apply()

    fun logout(): Boolean {
        setUserKey("")
        return shared.edit().putBoolean("isLogin", false).commit()
    }

    fun getBioState() : Boolean = shared.getBoolean("biometry", false)
    fun saveBiometryState(checked: Boolean) = shared.edit().putBoolean("biometry", checked).apply()

    fun easyModeState(): Boolean = shared.getBoolean("easyMode", true)
    fun setEasyModeSate(checked: Boolean) = shared.edit().putBoolean("easyMode", checked).apply()

    fun getUserKey(): String {
        return shared.getString("UserKey", "") ?: ""
    }

    fun setUserKey(key: String) {
        return shared.edit().putString("UserKey", key).apply()
    }
}