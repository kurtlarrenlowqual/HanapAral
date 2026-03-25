package com.example.hanaparal

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.hanaparal.security.BiometricHelper
import com.example.hanaparal.ui.AppNav
import com.example.hanaparal.ui.HomeScreen
import com.example.hanaparal.ui.Routes
import com.example.hanaparal.ui.SuperuserScreen
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Testing helpers
        RemoteConfigTest.init()
        RemoteConfigTest.fetchValues()
        FirestoreTest.writeSampleUser()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: com.google.android.gms.tasks.Task<String> ->
                if (!task.isSuccessful) {
                    Log.e("FCM_TEST", "Fetching FCM token failed", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("FCM_TEST", "FCM Token: $token")
            }

        val app = application as HanapAralApp
        val repos = app.container.firebaseRepositories
        val remoteConfigRepo = app.container.remoteConfigRepository

        remoteConfigRepo.initDefaults()

        setContent {
            val navController = rememberNavController()
            var remoteState by remember { mutableStateOf(remoteConfigRepo.readState()) }

            val biometricHelper = remember {
                BiometricHelper(
                    activity = this,
                    executor = ContextCompat.getMainExecutor(this)
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
                        homeScreen = {
                            HomeScreen(
                                uiState = remoteState,
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
                                            onError = { /* show snackbar/toast if you want */ }
                                        )
                                    }
                                },
                                // Explicitly typed to fix "Cannot infer type" errors
                                onCreateStudyGroup = { name: String, description: String ->
                                    repos.createStudyGroup(name, description)
                                    Unit
                                }
                            )
                        },
                        superuserScreen = {
                            SuperuserScreen(uiState = remoteState)
                        }
                    )
                }
            }
        }
    }
}