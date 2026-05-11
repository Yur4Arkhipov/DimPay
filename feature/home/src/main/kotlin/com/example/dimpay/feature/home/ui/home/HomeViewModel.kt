package com.example.dimpay.feature.home.ui.home

import androidx.lifecycle.ViewModel
import com.example.dimpay.feature.home.model.BankCardUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _cards = MutableStateFlow(
        listOf(
            BankCardUi(
                id = 1,
                cardName = "Тинькофф",
                cardType = "Дебетовая карта",
                lastNumbers = "1234",
//                icon = AppIcons.Home
            ),
            BankCardUi(
                id = 2,
                cardName = "Сбербанк",
                cardType = "Дебетовая карта",
                lastNumbers = "9101",
//                icon = Icons.Default.CheckCircle
            ),
            BankCardUi(
                id = 3,
                cardName = "Альфа-Банк",
                cardType = "Кредитная карта",
                lastNumbers = "3456",
//                icon = Icons.Default.Favorite
            )
        )
    )

    val cards = _cards.asStateFlow()
}