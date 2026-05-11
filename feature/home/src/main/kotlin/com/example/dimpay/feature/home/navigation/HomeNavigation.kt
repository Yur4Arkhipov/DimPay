package com.example.dimpay.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.dimpay.feature.home.ui.addcard.AddCardScreen
import com.example.dimpay.feature.home.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute
@Serializable
data object AddCardRoute

@Serializable
data object HomeBaseRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)

fun NavController.navigateToAddCard() = navigate(route = AddCardRoute)

fun NavGraphBuilder.homeSection(
    onBackClick: () -> Unit,
    onNavigateToAddCard: () -> Unit,
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onNavigateToAddCard = onNavigateToAddCard,
            )
        }

        composable<AddCardRoute> {
            AddCardScreen(
                onBackClick = onBackClick
            )
        }
    }
}