package com.example.dimpay.feature.home.ui.addcard

import androidx.lifecycle.ViewModel
import com.example.dimpay.feature.home.model.AddCardUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class AddCardViewModel @Inject constructor(

) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCardUi())
    val uiState = _uiState.asStateFlow()

    fun onCardNameChange(value: String) {
        _uiState.update {
            it.copy(cardName = value)
        }
    }

    fun onCardNumberChange(value: String) {
        _uiState.update {
            it.copy(cardNumber = value)
        }
    }

    fun onExpireDateChange(value: String) {
        _uiState.update {
            it.copy(expireDate = value)
        }
    }

    fun onCvvChange(value: String) {
        _uiState.update {
            it.copy(cvv = value)
        }
    }
}