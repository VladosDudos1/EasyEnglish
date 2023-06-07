package english.lessons.inlesson.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val rating = MutableLiveData<Int>(0)
}