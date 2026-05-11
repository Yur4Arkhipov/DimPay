package com.example.dimpay.core.domain.auth

class BiometricAuthException(val code: Int, message: String) : Exception(message)
class BiometricUserCanceledException : Exception("User canceled")
class NoDeviceCredentialException : Exception("No device credential enrolled")
class BiometricLockoutException : Exception("Biometric lockout")