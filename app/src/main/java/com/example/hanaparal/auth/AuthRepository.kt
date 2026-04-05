package com.example.hanaparal.auth

import android.content.Context
import com.example.hanaparal.data.FirebaseRepositories
import com.example.hanaparal.data.models.UserProfile
import com.google.android.gms.auth.api.signin.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context,
    private val firebaseRepo: FirebaseRepositories
) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun getGoogleSignInClient(): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("YOUR_WEB_CLIENT_ID") // 🔥 IMPORTANT
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, options)
    }

    suspend fun firebaseAuthWithGoogle(idToken: String): Result<Unit> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            val user = result.user ?: error("User is null")

            // Save to Firestore (Member 1 function)
            firebaseRepo.createOrUpdateUserProfile(
                UserProfile(
                    uid = user.uid,
                    name = user.displayName ?: "",
                    courseProgram = "",
                    email = user.email ?: "",
                    role = "student"
                )
            )

            firebaseRepo.saveCurrentUserTokenToFirestore()
            firebaseRepo.subscribeToGlobalAnnouncements()
        }
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
        GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
    }
}