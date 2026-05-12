package com.example.dimpay.feature.home.ui.qr

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dimpay.core.data.usecase.GenerateQrUseCase
import com.example.dimpay.feature.home.ui.home.generateQrBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QrScreenViewModel @Inject constructor(
    private val generateQrUseCase: GenerateQrUseCase,
) : ViewModel() {

    private val cardId: String = "98940ea1-6305-4129-9a1b-c6b74347d02e"

    private val _uiState = MutableStateFlow(
        OfflineQrUiState()
    )
    val uiState = _uiState.asStateFlow()

    val qrBitmap = MutableStateFlow<Bitmap?>(null)

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
        if (current.contains(".")) return
        _uiState.update {
            it.copy(
                value = "$current."
            )
        }
    }

    fun generateQr(amount: Double) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                Log.d("QR", "generateQr called")
                val qr = generateQrUseCase(cardId, amount)
                Log.d("QR", "qr: $qr")
                _uiState.value = _uiState.value.copy(
                    qrCode = qr,
                    isLoading = false,
                    showQrDialog = true
                )
                qrBitmap.value = generateQrBitmap(qr)
            } catch (e: Exception) {
                Log.e("QR", "error", e)
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }

    fun closeQrDialog() {
        _uiState.value = _uiState.value.copy(
            showQrDialog = false,
            qrCode = null
        )
    }

    private fun isValidAmount(value: String): Boolean {
        if (value.count { it == '.' } > 1) {
            return false
        }
        val parts = value.split(".")
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