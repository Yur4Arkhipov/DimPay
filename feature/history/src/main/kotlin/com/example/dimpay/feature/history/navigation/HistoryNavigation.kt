package com.example.dimpay.feature.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.dimpay.feature.history.HistoryScreen
import kotlinx.serialization.Serializable

@Serializable
data object HistoryRoute

@Serializable
data object HistoryBaseRoute

fun NavController.navigateToHistory(navOptions: NavOptions? = null) =
    navigate(HistoryRoute, navOptions)

fun NavGraphBuilder.historySection() {
    composable<HistoryRoute> {
        HistoryScreen()
    }
}