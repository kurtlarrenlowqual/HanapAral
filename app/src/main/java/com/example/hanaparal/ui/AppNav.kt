package com.example.hanaparal.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hanaparal.data.FirebaseRepositories
import com.example.hanaparal.ui.profile.ProfileScreen
import com.example.hanaparal.ui.profile.ProfileViewModel
import com.example.hanaparal.auth.AuthRepository
import com.example.hanaparal.ui.studygroup.GroupListViewModel
import com.example.hanaparal.ui.studygroup.CreateGroupViewModel
import com.example.hanaparal.ui.studygroup.CreateGroupScreen
import com.example.hanaparal.ui.studygroup.GroupListScreen
import com.example.hanaparal.ui.studygroup.GroupListViewModelFactory
import com.example.hanaparal.ui.studygroup.CreateGroupViewModelFactory


object Routes {
    const val HOME = "home"
    const val SUPERUSER = "superuser"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val CREATE_GROUP = "create_group"
    const val GROUP_LIST = "group_list"
}


@Composable
fun AppNav(
    navController: NavHostController,
    authRepo: AuthRepository, // ✅ ADD THIS
    homeScreen: @Composable () -> Unit,
    superuserScreen: @Composable () -> Unit,
    firebaseRepositories: FirebaseRepositories
) {

    val user = authRepo.getCurrentUser()
    val startDestination = if (user != null) Routes.HOME else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                authRepository = authRepo,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToProfile = {
                    navController.navigate(Routes.PROFILE) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) { homeScreen() }

        composable(Routes.SUPERUSER) { superuserScreen() }

        composable(Routes.PROFILE) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModel.Factory(firebaseRepositories)
            )

            ProfileScreen(
                viewModel = profileViewModel,
                onProfileSaved = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.PROFILE) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CREATE_GROUP) {
            val viewModel: CreateGroupViewModel = viewModel(
                factory = CreateGroupViewModelFactory(firebaseRepositories)
            )

            CreateGroupScreen(
                viewModel = viewModel,
                onGroupCreated = {
                    navController.navigate(Routes.GROUP_LIST)
                }
            )
        }

        composable(Routes.GROUP_LIST) {
            val viewModel: GroupListViewModel = viewModel(
                factory = GroupListViewModelFactory(firebaseRepositories)
            )

            GroupListScreen(viewModel)
        }
    }
}