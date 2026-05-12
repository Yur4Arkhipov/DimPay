package com.example.dimpay.feature.home.ui.qr

data class OfflineQrUiState(
    val value: String = "",
    val qrCode: String? = null,
    val isLoading: Boolean = false,
    val error: String? = "",
    val showQrDialog: Boolean = false
)