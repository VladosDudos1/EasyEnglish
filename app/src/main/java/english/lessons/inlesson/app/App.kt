package english.lessons.inlesson.app

import android.app.Application
import english.lessons.inlesson.data.DataManager

class App : Application() {
    companion object {
        lateinit var dm: DataManager
    }
    override fun onCreate() {
        super.onCreate()
        dm = DataManager(baseContext)
    }
}