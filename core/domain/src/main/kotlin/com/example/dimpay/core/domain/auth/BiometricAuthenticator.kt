package com.example.dimpay.core.domain.auth

import androidx.fragment.app.FragmentActivity

interface BiometricAuthenticator {
    suspend fun authenticate(activity: FragmentActivity): Result<Unit>
    fun checkAvailability(): BiometricAuthStatus
}