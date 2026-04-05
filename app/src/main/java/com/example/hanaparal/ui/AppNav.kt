package com.example.hanaparal.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hanaparal.data.FirebaseRepositories
import com.example.hanaparal.ui.profile.ProfileScreen
import com.example.hanaparal.ui.profile.ProfileViewModel

object Routes {
    const val HOME = "home"
    const val SUPERUSER = "superuser"
    const val PROFILE = "profile"
}

@Composable
fun AppNav(
    navController: NavHostController,
    homeScreen: @Composable () -> Unit,
    superuserScreen: @Composable () -> Unit,
    firebaseRepositories: FirebaseRepositories
) {
    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { homeScreen() }
        composable(Routes.SUPERUSER) { superuserScreen() }
        composable(Routes.PROFILE) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(firebaseRepositories)
            )
            ProfileScreen(viewModel = profileViewModel)
        }
    }
}