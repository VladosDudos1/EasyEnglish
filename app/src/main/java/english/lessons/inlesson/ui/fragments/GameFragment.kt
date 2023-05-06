package english.lessons.inlesson.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import english.lessons.inlesson.R
import english.lessons.inlesson.app.App
import english.lessons.inlesson.databinding.GameFragmentBinding
import english.lessons.inlesson.ui.Case
import english.lessons.inlesson.ui.Case.backPressType
import english.lessons.inlesson.ui.Case.user
import english.lessons.inlesson.ui.adapters.GameAdapter
import java.lang.Exception

class GameFragment : Fragment(), GameAdapter.OnClickListener {

    override fun click(data: Int) {
        when (data) {
            0 ->
                changeFragment(GamePictureFragment())
            1 ->
                changeFragment(GameWordFragment())
            2 ->
                changeFragment(GameQuizFragment())
        }
    }

    private lateinit var binding: GameFragmentBinding
    private var store = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            GameFragmentBinding.bind(inflater.inflate(R.layout.game_fragment, container, false))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLinearAdapter()
        backPressType = 1

        setUser()
        onClick()
    }

    private fun setLinearAdapter() {
        try {
            binding.rvGames.adapter = GameAdapter(this, requireContext())
            binding.rvGames.layoutManager = LinearLayoutManager(activity)
        } catch (ex: Exception) {
            Log.d("error", ex.message.toString())
        }
    }

    private fun onClick() {
        binding.imgLogout.setOnClickListener {
            MaterialDialog(requireActivity())
                .title(text = "Вы уверены, что хотите выйти?")
                .positiveButton(text = "Да") {
                    App.dm.logout()
                    changeFragment(LoginFragment())
                }
                .negativeButton { it.cancel() }
                .show()
        }
    }
//        binding.profileCardImg.setOnClickListener {
//            val et = EditText(this)
//            val dialog: AlertDialog = AlertDialog.Builder(this)
//                .setTitle("Введите ссылку на изображение")
//                .setView(et)
//                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
//                    val editTextInput: String = et.text.toString()
//                    if (URLUtil.isValidUrl(editTextInput)){
//                        store.child("users").child(user!!.uid).child("image").setValue(editTextInput)
//                            .addOnCompleteListener {
//                                if (it.isSuccessful){
//                                    Toast.makeText(this, "Успешно", Toast.LENGTH_SHORT).show()
//                                    setUser()
//                                }
//                                else Toast.makeText(this, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
//                            }
//                    }
//                })
//                .setNegativeButton("Cancel", null)
//                .create()
//            dialog.show()
//        }


    private fun setUser() {
        store.child("users").child(user!!.uid).get()
            .addOnCompleteListener {
                binding.nickName.text = it.result.child("name").value.toString()
            }
    }

    private fun changeFragment(fmt: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fmt)
            .addToBackStack(null)
            .commit();
    }
}