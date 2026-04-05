package com.example.hanaparal.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hanaparal.ui.profile.ProfileScreen

object Routes {
    const val HOME = "home"
    const val SUPERUSER = "superuser"
    const val PROFILE = "profile"
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
        composable(Routes.PROFILE) {
            ProfileScreen()
        }
    }
}