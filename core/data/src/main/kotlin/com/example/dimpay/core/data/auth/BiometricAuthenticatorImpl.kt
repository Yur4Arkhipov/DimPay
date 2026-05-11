package com.example.dimpay.core.data.auth

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.dimpay.core.domain.auth.BiometricAuthException
import com.example.dimpay.core.domain.auth.BiometricAuthStatus
import com.example.dimpay.core.domain.auth.BiometricAuthenticator
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class BiometricAuthenticatorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BiometricAuthenticator {

    override fun checkAvailability(): BiometricAuthStatus {
        val biometricManager = BiometricManager.from(context)

        val strongAuthenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL

        return when (biometricManager.canAuthenticate(strongAuthenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricAuthStatus.Ready

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                BiometricAuthStatus.NotEnrolled
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                BiometricAuthStatus.Unavailable
            }

            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED,
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                BiometricAuthStatus.Unavailable
            }

            else -> {
                val weakAuthenticators = BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL

                when (biometricManager.canAuthenticate(weakAuthenticators)) {
                    BiometricManager.BIOMETRIC_SUCCESS ->
                        BiometricAuthStatus.WeakBiometricOnly(canUseDeviceCredential = true)
                    else -> BiometricAuthStatus.Unavailable
                }
            }
        }
    }

    override suspend fun authenticate(activity: FragmentActivity): Result<Unit> =
        suspendCancellableCoroutine { cont ->

            val executor = ContextCompat.getMainExecutor(context)

            val callback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    if (!cont.isCompleted) cont.resume(Result.success(Unit))
                }

                override fun onAuthenticationFailed() {
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    if (!cont.isCompleted) {
                        val error = BiometricAuthException(errorCode, errString.toString())
                        cont.resume(Result.failure(error))
                    }
                }
            }

            val prompt = BiometricPrompt(activity, executor, callback)

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Подтвердите личность")
                .setSubtitle("Используйте биометрию или пароль устройства")
                .setAllowedAuthenticators(
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .setConfirmationRequired(false)
                .build()

            prompt.authenticate(promptInfo)

            cont.invokeOnCancellation { prompt.cancelAuthentication() }
        }
}