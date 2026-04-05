package com.example.hanaparal

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.hanaparal.core.AppContainer
import com.example.hanaparal.ui.AppNav
import com.example.hanaparal.ui.LoginScreen
import com.example.hanaparal.ui.ProfileScreen
import com.example.hanaparal.ui.Routes
import com.example.hanaparal.ui.theme.HanapAralTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val appContainer = AppContainer(applicationContext)
        val authRepository = appContainer.authRepository
        val firebaseRepo = appContainer.firebaseRepositories

        enableEdgeToEdge()
        setContent {
            HanapAralTheme {
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    val user = authRepository.getCurrentUser()
                    if (user == null) {
                        startDestination = Routes.LOGIN
                    } else {
                        val hasProfile = authRepository.hasUserProfile(user.uid)
                        startDestination = if (hasProfile) Routes.HOME else Routes.PROFILE
                    }
                }

                if (startDestination == null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    AppNav(
                        navController = navController,
                        startDestination = startDestination!!,
                        loginScreen = {
                            LoginScreen(
                                authRepository = authRepository,
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
                        },
                        profileScreen = {
                            ProfileScreen(
                                firebaseRepo = firebaseRepo,
                                onProfileSaved = {
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.PROFILE) { inclusive = true }
                                    }
                                }
                            )
                        },
                        homeScreen = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Home Screen (Coming Soon)")
                            }
                        },
                        superuserScreen = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Superuser Screen (Coming Soon)")
                            }
                        }
                    )
                }
            }
        }
    }
}