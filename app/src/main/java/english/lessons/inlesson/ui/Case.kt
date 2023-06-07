package english.lessons.inlesson.ui

import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import english.lessons.inlesson.app.App
import java.util.*

object Case {
    var store = FirebaseDatabase.getInstance().reference
    private var rating = 0
    var backPressType = 0

    fun addRating(){
        rating++
        store.child("users").child(App.dm.getUserKey()).child("rating").setValue(rating)
    }
    fun getRating(){
        store.child("users").child(App.dm.getUserKey()).child("rating").get()
            .addOnCompleteListener {
                rating = it.result.value.toString().toInt()
            }
    }
}