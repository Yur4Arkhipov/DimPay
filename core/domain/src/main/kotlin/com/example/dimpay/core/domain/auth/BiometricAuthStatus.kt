package com.example.dimpay.core.domain.auth

sealed interface BiometricAuthStatus {
    object Ready : BiometricAuthStatus
    object NotEnrolled : BiometricAuthStatus
    object Unavailable : BiometricAuthStatus
    data class WeakBiometricOnly(val canUseDeviceCredential: Boolean) : BiometricAuthStatus
}