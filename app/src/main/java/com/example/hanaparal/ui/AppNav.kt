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
import com.example.hanaparal.ui.studygroup.GroupMembersScreen
import com.example.hanaparal.ui.studygroup.EditGroupScreen
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Text


object Routes {
    const val HOME = "home"
    const val SUPERUSER = "superuser"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val CREATE_GROUP = "create_group"
    const val GROUP_LIST = "group_list"
    const val GROUP_MEMBERS = "group_members"
    const val EDIT_GROUP = "edit_group"
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

            GroupListScreen(
                viewModel = viewModel,
                onEdit = { groupId ->
                    navController.navigate("edit_group/$groupId")
                },
                onViewMembers = { groupId ->
                    navController.navigate("group_members/$groupId")
                }
            )
        }

        composable("group_members/{groupId}") { backStackEntry ->

            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable

            val viewModel: GroupListViewModel = viewModel(
                factory = GroupListViewModelFactory(firebaseRepositories)
            )

            val group = viewModel.selectedGroup

            LaunchedEffect(groupId) {
                viewModel.loadGroupById(groupId)
            }

            if (group == null) {
                Text("Loading...")
            } else {
                GroupMembersScreen(group, viewModel)
            }
        }

        composable("edit_group/{groupId}") { backStackEntry ->

            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable

            val viewModel: GroupListViewModel = viewModel(
                factory = GroupListViewModelFactory(firebaseRepositories)
            )

            val group = viewModel.groups.find { it.id == groupId }

            if (group != null) {
                EditGroupScreen(
                    group = group,
                    onSave = { name, subject, max ->
                        viewModel.updateGroup(groupId, name, subject, max)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("edit_group/{groupId}") { backStackEntry ->

            val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable

            val viewModel: GroupListViewModel = viewModel(
                factory = GroupListViewModelFactory(firebaseRepositories)
            )

            val group = viewModel.selectedGroup

            LaunchedEffect(groupId) {
                viewModel.loadGroupById(groupId)
            }

            if (group == null) {
                Text("Loading...")
            } else {
                EditGroupScreen(
                    group = group,
                    onSave = { name, subject, max ->
                        viewModel.updateGroup(groupId, name, subject, max)
                        navController.popBackStack()
                    }
                )
            }
        }


    }
}