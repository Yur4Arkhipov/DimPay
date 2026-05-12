package com.example.dimpay.feature.home.model

data class AddCardUi(
    val cardName: String = "",
    val cardNumber: String = "",
    val expireDate: String = "",
    val cvv: String = "",
    val isCvvVisible: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
) {
    val isAddButtonEnabled: Boolean
        get() {
            return cardName.isNotBlank() &&
                    cardNumber.length == 16 &&
                    expireDate.length == 4 &&
                    cvv.length == 3 &&
                    !isLoading
        }
}