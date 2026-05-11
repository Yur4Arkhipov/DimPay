package com.example.dimpay.feature.home.model

import androidx.compose.ui.graphics.Color


data class BankCardUi(
    val id: Int,
    val bankName: String,
    val cardType: String,
    val balance: String,
    val lastNumbers: String,
//    val icon: ImageVector,
    val gradientColors: List<Color>
)