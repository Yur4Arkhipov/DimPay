package com.example.dimpay.navigation

import com.example.dimpay.core.designsystem.R
import com.example.dimpay.core.designsystem.icons.AppIcons
import com.example.dimpay.feature.history.navigation.HistoryBaseRoute
import com.example.dimpay.feature.history.navigation.HistoryRoute
import com.example.dimpay.feature.home.navigation.HomeBaseRoute
import com.example.dimpay.feature.home.navigation.HomeRoute
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val iconRes: Int,
    val iconTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route
) {
    HOME(
        iconRes = AppIcons.Home,
        iconTextId = R.string.home_title,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class
    ),
    HISTORY(
        iconRes = AppIcons.History,
        iconTextId = R.string.history_title,
        route = HistoryRoute::class,
        baseRoute = HistoryBaseRoute::class
    ),
}