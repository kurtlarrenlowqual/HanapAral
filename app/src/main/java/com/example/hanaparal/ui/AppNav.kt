package com.example.hanaparal.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Routes {
    const val HOME = "home"
    const val SUPERUSER = "superuser"
}

@Composable
fun AppNav(
    navController: NavHostController,
    homeScreen: @Composable () -> Unit,
    superuserScreen: @Composable () -> Unit
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { homeScreen() }
        composable(Routes.SUPERUSER) { superuserScreen() }
    }
}