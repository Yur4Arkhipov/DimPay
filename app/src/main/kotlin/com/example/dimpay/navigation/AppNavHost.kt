package com.example.dimpay.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.example.dimpay.feature.history.navigation.historySection
import com.example.dimpay.feature.home.navigation.HomeBaseRoute
import com.example.dimpay.feature.home.navigation.homeSection
import com.example.dimpay.feature.home.navigation.navigateToAddCard
import com.example.dimpay.feature.home.navigation.navigateToQr
import com.example.dimpay.mainapp.AppState

@Composable
fun AppNavHost(
    appState: AppState,
    modifier: Modifier = Modifier
) {
    val navController = appState.navController

    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier
    ) {
        homeSection(
            onNavigateToAddCard = {
                navController.navigateToAddCard()
            },
            onNavigateQr = {
                navController.navigateToQr()
            },
            onBackClick = navController::popBackStack
        )

        historySection()
    }
}