package com.example.hanaparal.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val PROFILE = "profile"
    const val SUPERUSER = "superuser"
}

@Composable
fun AppNav(
    navController: NavHostController,
    loginScreen: @Composable () -> Unit,
    profileScreen: @Composable () -> Unit,
    homeScreen: @Composable () -> Unit,
    superuserScreen: @Composable () -> Unit,
    startDestination: String = Routes.LOGIN
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) { loginScreen() }
        composable(Routes.PROFILE) { profileScreen() }
        composable(Routes.HOME) { homeScreen() }
        composable(Routes.SUPERUSER) { superuserScreen() }
    }
}