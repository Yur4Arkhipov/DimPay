package com.example.dimpay.feature.home.ui.addcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimpay.core.domain.repository.CardRepository
import com.example.dimpay.feature.home.model.AddCardUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCardViewModel @Inject constructor(
    private val repository: CardRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCardUi())
    val uiState = _uiState.asStateFlow()

    fun addCard() {
        val state = uiState.value
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }
            runCatching {
                repository.addCard(
                    cardName = state.cardName,
                    cardNumber = state.cardNumber,
                    expireDate = state.expireDate,
                    cvv = state.cvv
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
            }
        }
    }

    fun onCardNameChange(value: String) {
        val formatted = value.replace("\n", "")
        if (formatted.length <= 20) {
            _uiState.update {
                it.copy(cardName = formatted)
            }
        }
    }

    fun onCardNumberChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        if (digitsOnly.length <= 16) {
            _uiState.update {
                it.copy(cardNumber = digitsOnly)
            }
        }
    }

    fun onExpireDateChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        if (digitsOnly.length <= 4) {
            _uiState.update {
                it.copy(expireDate = digitsOnly)
            }
        }
    }

    fun onCvvChange(value: String) {
        val digitsOnly = value.filter { it.isDigit() }
        if (digitsOnly.length <= 3) {
            _uiState.update {
                it.copy(cvv = digitsOnly)
            }
        }
    }

    fun toggleCvvVisibility() {
        _uiState.update {
            it.copy(
                isCvvVisible = !it.isCvvVisible
            )
        }
    }
}