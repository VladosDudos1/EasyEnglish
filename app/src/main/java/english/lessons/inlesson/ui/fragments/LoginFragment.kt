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
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import english.lessons.inlesson.R
import english.lessons.inlesson.app.App
import english.lessons.inlesson.databinding.LoginFragmentBinding
import english.lessons.inlesson.ui.Case
import english.lessons.inlesson.ui.Case.backPressType

class LoginFragment : Fragment() {

    private lateinit var binding: LoginFragmentBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoginFragmentBinding.bind(
            inflater.inflate(
                R.layout.login_fragment, container, false
            )
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backPressType = 1
        onClick()
    }

    private fun onClick() {
        binding.buttonToReg.setOnClickListener {
            changeFragment(RegistrationFragment())
        }
        binding.buttonAuthorize.setOnClickListener {
            authorise()
        }
        binding.forgetPasswordTxt.setOnClickListener {
            resetEmail()
        }
    }

    private fun authorise() {
        val mail = binding.editLogin.text.toString()
        val password = binding.editPassword.text.toString()
        if (checkInput()) {
            auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                        Case.user = user
                        App.dm.endLogin()
                        changeFragment(GameFragment())
                    } else {
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        makeToast("Что-то пошло не так, проверьте качество соединения.")
                    }
                }
        }
    }

    private fun checkInput(): Boolean {
        if (checkEmail(binding.editLogin.text.toString())) {
            if (!binding.editPassword.text.isNullOrEmpty()) {
                return true
            } else makeToast("Введите пароль")
        }
        return false
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            makeToast("Вы успешно авторизовались")
        } else {
            makeToast("Не удалось выполнить авторизацию")
        }
    }

    private fun changeFragment(fmt: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fmt)
            .addToBackStack(null)
            .commit();
    }

    private fun resetEmail() {

        if (checkEmail(binding.editLogin.text.toString())) {
            MaterialDialog(requireActivity())
                .title(text = "Do you want to reset password for")
                .message(text = binding.editLogin.text.toString() + " ?")
                .positiveButton(text = "Yes") {
                    auth.sendPasswordResetEmail(binding.editLogin.text.toString())
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Toast.makeText(activity, "Some problems went", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                }
                .show { }
        } else Toast.makeText(activity, "Ваш email должен быть корректным", Toast.LENGTH_SHORT).show()
    }

    private fun checkEmail(email: String): Boolean {
        when {
            !Patterns.EMAIL_ADDRESS.matcher(binding.editLogin.text)
                .matches() -> makeToast("Введите правильную почту")
            binding.editLogin.text.isNullOrEmpty() -> makeToast("Введите почту")
            else -> return true
        }
        return false
    }
}