package com.example.hanaparal

import android.os.Bundle
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
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetching remote config data for testing
        RemoteConfigTest.init()
        RemoteConfigTest.fetchValues()

        // Write user to the firestore for testing
        FirestoreTest.writeSampleUser()

        // Fetching FCM token for testing -- for cloud messaging
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
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
            val scope = rememberCoroutineScope()
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