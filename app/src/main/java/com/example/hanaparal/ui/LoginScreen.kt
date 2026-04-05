package com.example.hanaparal.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.example.hanaparal.auth.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    authRepository: AuthRepository,
    onLoginSuccess: () -> Unit
) {
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

        try {
            val account = task.result
            val idToken = account.idToken

            if (idToken == null) {
                Log.e("LOGIN", "❌ ID TOKEN IS NULL")
                return@rememberLauncherForActivityResult
            }

            Log.d("LOGIN", "✅ ID TOKEN RECEIVED")

            scope.launch {
                authRepository.firebaseAuthWithGoogle(idToken)
                    .onSuccess {
                        Log.d("LOGIN", "✅ FIREBASE AUTH SUCCESS")
                        onLoginSuccess()
                    }
                    .onFailure {
                        Log.e("LOGIN", "❌ FIREBASE AUTH FAILED", it)
                    }
            }

        } catch (e: Exception) {
            Log.e("LOGIN", "❌ GOOGLE SIGN-IN FAILED", e)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                val client = authRepository.getGoogleSignInClient()
                launcher.launch(client.signInIntent)
            }
        ) {
            Text("Sign in with Google")
        }
    }
}