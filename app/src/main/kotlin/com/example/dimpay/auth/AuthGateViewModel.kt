package com.example.dimpay.auth

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimpay.core.data.usecase.SyncPaymentTokensUseCase
import com.example.dimpay.core.domain.auth.BiometricAuthStatus
import com.example.dimpay.core.domain.auth.BiometricAuthenticator
import com.example.dimpay.core.domain.auth.BiometricLockoutException
import com.example.dimpay.core.domain.auth.BiometricUserCanceledException
import com.example.dimpay.core.domain.auth.NoDeviceCredentialException
import com.example.dimpay.core.domain.auth.SessionManager
import com.example.dimpay.core.domain.repository.AppInstanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthGateViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val biometricAuthenticator: BiometricAuthenticator,
    private val appInstanceRepository: AppInstanceRepository,
    private val syncPaymentTokensUseCase: SyncPaymentTokensUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthGateState>(AuthGateState.Checking)
    val uiState: StateFlow<AuthGateState> = _uiState

    fun start() {
        viewModelScope.launch {
            _uiState.value = AuthGateState.Checking
            _uiState.value = AuthGateState.CheckingAppInstance
            val appTokenResult = runCatching {
                appInstanceRepository.getOrFetchToken()
            }
            Log.d("AuthGateVM", "Token result: $appTokenResult")
            if (appTokenResult.isFailure) {
                _uiState.value = AuthGateState.Error("Нет соединения с сервером")
                return@launch
            }
            if (!sessionManager.isAuthenticated()) {
                _uiState.value = AuthGateState.RequireAuth
                return@launch
            }
            try {
                _uiState.value = AuthGateState.SyncingTokens
                syncPaymentTokensUseCase()
                _uiState.value = AuthGateState.Authenticated
            } catch (e: Exception) {
                _uiState.value = AuthGateState.Error(e.message ?: "Ошибка загрузки токенов")
            }
            _uiState.value = AuthGateState.Authenticated
        }
    }

    fun authenticate(activity: FragmentActivity) {
        when (biometricAuthenticator.checkAvailability()) {
            is BiometricAuthStatus.Ready -> {
                _uiState.value = AuthGateState.Authenticating
                viewModelScope.launch {
                    val result = biometricAuthenticator.authenticate(activity)
                    handleAuthResult(result)
                }
            }
            is BiometricAuthStatus.NotEnrolled -> {
                _uiState.value = AuthGateState.SetupRequired
            }
            is BiometricAuthStatus.Unavailable -> {
                _uiState.value = AuthGateState.Error("Аутентификация не поддерживается на этом устройстве")
            }
            is BiometricAuthStatus.WeakBiometricOnly -> {
                _uiState.value = AuthGateState.Authenticating
                viewModelScope.launch {
                    val result = biometricAuthenticator.authenticate(activity)
                    handleAuthResult(result)
                }
            }
        }
    }

    private fun handleAuthResult(result: Result<Unit>) {
        if (result.isSuccess) {
            viewModelScope.launch {
                try {
                    sessionManager.markAuthenticated()
                    _uiState.value = AuthGateState.SyncingTokens
                    syncPaymentTokensUseCase()
                    _uiState.value = AuthGateState.Authenticated
                } catch (e: Exception) {
                    _uiState.value =
                        AuthGateState.Error(
                            e.message ?: "Ошибка загрузки токенов"
                        )
                }
            }
        } else {
            when (result.exceptionOrNull()) {
                is BiometricUserCanceledException -> {
                    _uiState.value = AuthGateState.RequireAuth
                }
                is NoDeviceCredentialException -> {
                    _uiState.value = AuthGateState.SetupRequired
                }
                is BiometricLockoutException -> {
                    _uiState.value = AuthGateState.Error("Слишком много попыток. Попробуйте позже.")
                }
                else -> {
                    _uiState.value = AuthGateState.Error(result.exceptionOrNull()?.message ?: "Ошибка")
                }
            }
        }
    }

    fun onAppBackgrounded() {
        sessionManager.invalidateSession()
    }
}

sealed interface AuthGateState {
    object Checking : AuthGateState
    object CheckingAppInstance : AuthGateState
    object RequireAuth : AuthGateState
    object Authenticating : AuthGateState
    object Authenticated : AuthGateState
    object SetupRequired : AuthGateState
    object SyncingTokens : AuthGateState
    data class Error(val message: String) : AuthGateState
}