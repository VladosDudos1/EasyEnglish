package english.lessons.inlesson.ui.activities

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Handler
import android.widget.Toast
import androidx.core.app.ActivityCompat
import english.lessons.inlesson.R
import english.lessons.inlesson.app.App
import english.lessons.inlesson.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private var cancellationSignal: CancellationSignal? = null
    private val bioState = App.dm.getBioState()
    private val authenticationCallback: BiometricPrompt.AuthenticationCallback
        get() =
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                    super.onAuthenticationError(errorCode, errString)
                    notifyUser("Authentication error: $errString")
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                    super.onAuthenticationSucceeded(result)
                    notifyUser(getString(R.string.success))
                    successfulEnter()
                }
            }

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkBiometricSupport()

        Handler().postDelayed({
            if (bioState) {
                openBiometry()
            } else successfulEnter()
        }, 400)
    }

    private fun openBiometry() {
        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle(getString(R.string.use_your_fingerprint))
            .setNegativeButton("Cancel", this.mainExecutor) { dialog, i ->
                notifyUser(getString(R.string.authentication_cancelled))
                finish()
            }.build()
        biometricPrompt.authenticate(getCancellationSignal(), mainExecutor, authenticationCallback)
    }

    private fun checkBiometricSupport(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        if (!keyguardManager.isKeyguardSecure) {
            notifyUser(getString(R.string.fingerprint_authentication_has_t_been_enabled_in_settings))
            App.dm.saveBiometryState(false)
            return false
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser(getString(R.string.fingerprint_permission_is_not_enabled))
            return false
        }

        return if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
            true
        } else true
    }

    private fun getCancellationSignal(): CancellationSignal {
        cancellationSignal = CancellationSignal()
        cancellationSignal?.setOnCancelListener {
            notifyUser(getString(R.string.authentication_cancelled))
        }
        return cancellationSignal as CancellationSignal
    }

    private fun notifyUser(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun successfulEnter() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}