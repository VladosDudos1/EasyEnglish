package english.lessons.inlesson.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import english.lessons.inlesson.R
import english.lessons.inlesson.app.App
import english.lessons.inlesson.databinding.FragmentSettingsBinding
import english.lessons.inlesson.ui.Case
import english.lessons.inlesson.ui.activities.MainActivity
import java.util.Locale

class SettingsFragment : Fragment() {

    private val binding: FragmentSettingsBinding by lazy {
        FragmentSettingsBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Case.backPressType = 0
        setSwitchState()
        applyClick()
    }

    private fun applyClick() {
        binding.biometrySwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            App.dm.saveBiometryState(isChecked)
        }
        binding.easyModeSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
            App.dm.setEasyModeSate(isChecked)

            if (App.dm.easyModeState()) setLocale(requireActivity(), "ru") else setLocale(requireActivity(), "en")
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, SettingsFragment()).commit()
        }

        binding.logoutBtn.setOnClickListener {
            MaterialDialog(requireActivity())
                .title(text = getString(R.string.want_exit))
                .positiveButton(text = getString(R.string.yes)) {
                    App.dm.logout()
                    App.dm.saveBiometryState(false)
                    changeFragment(LoginFragment())
                }
                .negativeButton { it.cancel() }
                .show()
        }
    }

    private fun changeFragment(fmt: Fragment) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fmt)
            .addToBackStack(null)
            .commit();
    }

    private fun setSwitchState() {
        binding.biometrySwitch.isChecked = App.dm.getBioState()
        binding.easyModeSwitch.isChecked = App.dm.easyModeState()
    }

    private fun setLocale(activity: Activity, langCode: String){
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}