package com.example.dimpay.mainapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dimpay.navigation.AppNavHost

@Composable
fun App(appState: AppState) {

    val currentTopLevel = appState.currentTopLevelDestination

    Scaffold(
        bottomBar = {
            if (currentTopLevel != null) {
                NavigationBar(
                    modifier = Modifier.height(64.dp)
                ) {
                    appState.topLevelDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = destination == currentTopLevel,
                            onClick = {
                                appState.navigateToTopLevelDestination(destination)
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(destination.iconRes),
                                    contentDescription = destination.iconTextId.toString(),
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            alwaysShowLabel = false
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding(),
                    start = paddingValues.calculateStartPadding(LocalLayoutDirection.current),
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current)
                )
        ) {
            AppNavHost(appState = appState)
        }
    }
}