package english.lessons.inlesson.ui.fragments

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import english.lessons.inlesson.R
import english.lessons.inlesson.databinding.RegistrationFragmentBinding
import english.lessons.inlesson.ui.Case
import english.lessons.inlesson.ui.Case.backPressType
import english.lessons.inlesson.ui.models.User

class RegistrationFragment : Fragment() {

    private lateinit var binding: RegistrationFragmentBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var store = FirebaseDatabase.getInstance().reference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = RegistrationFragmentBinding.bind(
            inflater.inflate(R.layout.registration_fragment, container, false)
        )
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onCLick()
    }

    private fun onCLick(){
        binding.buttonToAuth.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.buttonRegistration.setOnClickListener {
            registration()
        }
    }

    private fun registration(){
        val nick = binding.editNick.text.toString()
        val mail = binding.editLogin.text.toString()
        val password = binding.editPassword.text.toString()

        val userN = User(mail, "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAOEAAADhCAMAAAAJbSJIAAAAM1BMVEXk5ueutLfn6eqrsbTp6+zg4uOwtrnJzc/j5earsbW0uby4vcDQ09XGyszU19jd3+G/xMamCvwDAAAFLklEQVR4nO2d2bLbIAxAbYE3sDH//7WFbPfexG4MiCAcnWmnrzkjIRaD2jQMwzAMwzAMwzAMwzAMwzAMwzAMwzAMwzAMwzAMw5wQkHJczewxZh2lhNK/CBOQo1n0JIT74/H/qMV0Z7GU3aCcVPuEE1XDCtVLAhgtpme7H0s1N1U7QjO0L8F7llzGeh1hEG/8Lo7TUmmuSrOfns9xnGXpXxsONPpA/B6OqqstjC6Ax/0ujkNdYQQbKNi2k64qiiEZ+ohi35X+2YcZw/WujmslYewiAliVYrxgJYrdwUmwXsU+RdApUi83oNIE27YvrfB/ZPg8+BJETXnqh9CVzBbTQHgojgiCvtqU9thFJg/CKz3VIMKMEkIXxIWqIpIg2SkjYj+xC816mrJae2aiWGykxRNsW0UwiJghJDljYI5CD8GRiCtIsJxizYUPQ2pzItZy5pcisTRdk/a9m4amtNNfBuQkdVhSaYqfpNTSFGfb9GRIakrE2Pm+GFLaCQPqiu0OpWP+HMPQQcgQMiQprWXNmsVwIjQjYi/ZrhAqNTCgr2gu0Jnz85RSSjso0HkMFZ0YZjKkc26a/jlmh9JiDyDxi9oeorTYAzZkwwoMz19pzj9bnH/GP/+qbchjSGflneWYhtTuKdMOmNKZcJ5TjInQKcYXnESd/jQxy0ENpULTNGOGgxpap/oyw9pbUAqhfx2Dbkhovvfgz4iUzoM9+GlK6/Mh4q29hyC1mwro30hpVVLPF9wYQr71RazOeM5/cw81iBRD+A03aM9/C/obbrKjbYSpCmIVG3qT/Q8oeUo3Rz0IL7vI1tEbCB9pSiu8I/aV8x3Kg/BGWrWp4ZVs0nZfmAoEG4h/61yHYIJiFSl6Q0Vk6tTW1N8kYp8hdOkfHYYMXd2Qft+8CYwqYDSKvqIh+MCF8Wgca2u/cwdgeW3TtuVn6+1oBs3yLo5C2JpK6CvQzGpfUkz9UG/87gCsi5o2LIXolxN0FbwAsjOLEr+YJmXn7iR6N0BCt5p5cMxm7eAsfS+/CACQf4CTpKjzgkvr2cVarVTf96372yut7XLJ1sa7lv6VcfgYrWaxqr3Wlo1S6pvStr22sxOtTNPLzdY3nj20bPP+ejFdJYkLsjGLdtPBEbe/mr2bQKiXWJDroA+vtzc0p9aahuwqHMDYrQEXHEw9jwQl3drMpts9JBU1SdktPe5FBRdJQ6bwXBpa57ib2A8kukQDzMjh++Uo7Fo6Wd02Pkf4fknqoo4HtvAIjsqUcjx6DIPgWCaOML9rKI/oqD9/lgNrn+eF+p7j8tnzHBiR7+kdUGw/+V1Kzkc75mMy6U+FMaxjPibiM1U1uGM+puInHpmALZCgP4pt7i840MV8+0R1zPsRB6UTcqpizncYwZ89syDydfyWCwXB1l8/zRNGWbTG/GHKUm9AkxHMc/EGSk3z2+ArEhPEV5TUBLEvUGFcjEUH80J/jveTGOAJEljJbILWGQT3zRYiwuKsUXN1EEJAzBhRJFll7mBUG7KD8EqPkKekBREaL8hMDZLQSG6AQjtHPYmvTQnX0TtpC1SYCe2YdkkyLP3jj5BSbKiuR585eQhTgoje6yIb0Yb0C+mV6EYvebqw5SDy2WmubogZiF2AVxPC2FpDf8H2Q9QWo6IkjUxTWVEI3WY/wrCeSuqJ+eRWzXR/JXwgVjUMozbCOfoEZiSiKVGepqv5CJ8RyR4D7xBeamqa7z3BJ/z17JxuBPdv93d/a2Ki878MMAzDMAzDMAzDMAzDMF/KP09VUmxBAiI3AAAAAElFTkSuQmCC", nick)

        if (checkInput()){
            auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(requireActivity()) {task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)

                        store.child("users").child(Case.user!!.uid).setValue(userN)
                            .addOnCompleteListener(requireActivity()) { res ->
                                if (res.isSuccessful){
                                    requireActivity().supportFragmentManager.popBackStack()
                                }
                                else makeToast(res.exception!!.message.toString())
                            }
                    }
                    else {
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        updateUI(null)
                    }
                }
        }
    }

    private fun checkInput(): Boolean {
        when {
            !Patterns.EMAIL_ADDRESS.matcher(binding.editLogin.text)
                .matches() -> makeToast("Введите свою почту")
            binding.editLogin.text.isNullOrEmpty() -> makeToast("Введите свою почту")
            binding.editNick.text.isNullOrEmpty() -> makeToast("Введите свой ник")
            binding.editPassword.text.isNullOrEmpty() -> makeToast("Введите свой пароль")
            binding.editPassword.text.toString().length < 6 -> makeToast("Пароль должен содержать хотя бы 6 символов")
            binding.repeatPassword.text.toString() != binding.editPassword.text.toString() -> makeToast("Пароли не совпадают")
            else -> return true
        }
        return false
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null)
            makeToast("Регистрация прошла удачно")
        else makeToast("Что-то пошло не так")
    }
}