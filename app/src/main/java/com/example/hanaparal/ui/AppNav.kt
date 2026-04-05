package com.example.hanaparal.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hanaparal.auth.AuthRepository
import com.example.hanaparal.ui.LoginScreen


object Routes {
    const val HOME = "home"
    const val SUPERUSER = "superuser"
    const val LOGIN = "login"
}

@Composable
fun AppNav(
    navController: NavHostController,
    authRepo: AuthRepository,
    homeScreen: @Composable () -> Unit,
    superuserScreen: @Composable () -> Unit
) {
    NavHost(navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                authRepository = authRepo,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) { homeScreen() }
        composable(Routes.SUPERUSER) { superuserScreen() }
    }
}