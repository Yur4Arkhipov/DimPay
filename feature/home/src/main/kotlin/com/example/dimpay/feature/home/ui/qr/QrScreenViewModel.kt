package com.example.dimpay.feature.home.ui.qr

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class QrScreenViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(
        OfflineQrUiState()
    )
    val uiState = _uiState.asStateFlow()

    fun onBackspace() {
        val current = _uiState.value.value
        if (current.isEmpty()) return
        _uiState.update {
            it.copy(
                value = current.dropLast(1)
            )
        }
    }

    fun onDigitClick(digit: Int) {
        val current = _uiState.value.value
        val newValue = current + digit
        if (!isValidAmount(newValue)) return
        _uiState.update {
            it.copy(value = newValue)
        }
    }

    fun onCommaClick() {
        val current = _uiState.value.value
        if (current.isEmpty()) return
        if (current.contains(",")) return
        _uiState.update {
            it.copy(
                value = "$current,"
            )
        }
    }

    private fun isValidAmount(value: String): Boolean {
        if (value.count { it == ',' } > 1) {
            return false
        }
        val parts = value.split(",")
        val integerPart = parts[0]
        if (integerPart.isNotEmpty()) {
            val intValue = integerPart.toIntOrNull() ?: return false
            if (intValue > 10000) {
                return false
            }
        }

        if (parts.size > 1) {
            val decimalPart = parts[1]
            if (decimalPart.length > 2) {
                return false
            }
            if (
                integerPart == "10000" &&
                decimalPart.isNotEmpty() &&
                decimalPart != "0" &&
                decimalPart != "00"
            ) {
                return false
            }
        }
        return true
    }
}