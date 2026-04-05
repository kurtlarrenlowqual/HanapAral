package com.example.hanaparal

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.hanaparal.security.BiometricHelper
import com.example.hanaparal.ui.AppNav
import com.example.hanaparal.ui.HomeScreen
import com.example.hanaparal.ui.Routes
import com.example.hanaparal.ui.SuperuserScreen
import com.google.firebase.messaging.FirebaseMessaging
import com.example.hanaparal.auth.AuthRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Test features
        RemoteConfigTest.init()
        RemoteConfigTest.fetchValues()
        FirestoreTest.writeSampleUser()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.e("FCM_TEST", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                Log.d("FCM_TEST", "FCM Token: ${task.result}")
            }

        val app = application as HanapAralApp
        val repos = app.container.firebaseRepositories
        val remoteConfigRepo = app.container.remoteConfigRepository
        val authRepo = AuthRepository(this, repos)

        remoteConfigRepo.initDefaults()

        setContent {
            val navController = rememberNavController()
            var remoteState by remember { mutableStateOf(remoteConfigRepo.readState()) }
            val authRepo = remember {
                AuthRepository(this, repos)
            }

            val biometricHelper = remember {
                BiometricHelper(
                    activity = this@MainActivity,
                    executor = ContextCompat.getMainExecutor(this@MainActivity)
                )
            }

            LaunchedEffect(Unit) {
                remoteConfigRepo.fetchAndActivate()
                remoteState = remoteConfigRepo.readState()

                repos.subscribeToGlobalAnnouncements()
            }

            MaterialTheme {
                Surface {
                    AppNav(
                        navController = navController,
                        authRepo = authRepo,
                        homeScreen = {
                            HomeScreen(
                                navController = navController,
                                uiState = remoteState,
                                onLogout = {
                                    authRepo.signOut()

                                    navController.navigate(Routes.LOGIN) {
                                        popUpTo(0) // 🔥 clears entire back stack
                                    }
                                },
                                onSubscribeTopic = {
                                    repos.subscribeToGlobalAnnouncements()
                                },
                                onFetchRemoteConfig = {
                                    remoteConfigRepo.fetchAndActivate()
                                    remoteState = remoteConfigRepo.readState()
                                },
                                onOpenSuperuser = {
                                    if (biometricHelper.canAuthenticate()) {
                                        biometricHelper.authenticate(
                                            onSuccess = {
                                                navController.navigate(Routes.SUPERUSER)
                                            },
                                            onError = { }
                                        )
                                    }
                                }
                            )
                        },
                        superuserScreen = {
                            SuperuserScreen(
                                uiState = remoteState
                            )
                        },
                        firebaseRepositories = repos
                    )
                }
            }
        }
    }
}