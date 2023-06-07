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

    private fun onCLick() {
        binding.buttonToAuth.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        binding.buttonRegistration.setOnClickListener {
            registration()
        }
    }

    private fun registration() {
        val nick = binding.editNick.text.toString()
        val mail = binding.editLogin.text.toString()
        val password = binding.editPassword.text.toString()

        val userN = User(
            mail,
            nick,
            0
        )

        if (checkInput()) {
            auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)

                        store.child("users").child(user!!.uid).setValue(userN)
                            .addOnCompleteListener(requireActivity()) { res ->
                                if (res.isSuccessful) {
                                    requireActivity().supportFragmentManager.popBackStack()
                                } else makeToast(res.exception!!.message.toString())
                            }
                    } else {
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        makeToast(task.exception!!.message.toString())
                    }
                }
        }
    }

    private fun checkInput(): Boolean {
        when {
            !Patterns.EMAIL_ADDRESS.matcher(binding.editLogin.text)
                .matches() -> makeToast(getString(R.string.enter_email))

            binding.editLogin.text.isNullOrEmpty() -> makeToast(getString(R.string.enter_email))
            binding.editNick.text.isNullOrEmpty() -> makeToast(getString(R.string.enter_your_nickname))
            binding.editPassword.text.isNullOrEmpty() -> makeToast(getString(R.string.enter_the_password))
            binding.editPassword.text.toString().length < 6 -> makeToast(getString(R.string.password_must_be_6_symbols_at_least))
            binding.repeatPassword.text.toString() != binding.editPassword.text.toString() -> makeToast(
                getString(R.string.password_mismatch)
            )

            else -> return true
        }
        return false
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null)
            makeToast(getString(R.string.registered_successfully))
        else makeToast(getString(R.string.smth_went_wrong))
    }
}