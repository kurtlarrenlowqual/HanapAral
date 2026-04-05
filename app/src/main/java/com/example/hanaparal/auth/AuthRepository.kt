package com.example.hanaparal.auth

import android.content.Context
import com.example.hanaparal.data.FirebaseRepositories
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
            .requestIdToken("283422875187-1t3lkq2qe6v1cl5dprr3thku6t4clonr.apps.googleusercontent.com")
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, options)
    }

    // ✅ MAIN LOGIN FUNCTION
    suspend fun firebaseAuthWithGoogle(idToken: String): Result<Boolean> {
        return runCatching {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()

            val user = result.user ?: error("User is null")

            // 🔥 CHECK if profile exists FIRST
            val hasProfile = hasUserProfile(user.uid)

            // Optional Firebase features
            firebaseRepo.saveCurrentUserTokenToFirestore()
            firebaseRepo.subscribeToGlobalAnnouncements()

            hasProfile // ✅ return result
        }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<Boolean> {
        return runCatching {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            val user = result.user ?: error("User is null")
            val hasProfile = hasUserProfile(user.uid)
            
            firebaseRepo.saveCurrentUserTokenToFirestore()
            firebaseRepo.subscribeToGlobalAnnouncements()
            
            hasProfile
        }
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<Boolean> {
        return runCatching {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            val user = result.user ?: error("User is null")
            
            // New users won't have a profile yet
            false
        }
    }

    // ✅ OUTSIDE function (correct placement)
    suspend fun hasUserProfile(uid: String): Boolean {
        val doc = firebaseRepo.getUserProfile(uid)
        return doc.getOrNull() != null
    }

    fun getCurrentUser() = auth.currentUser

    fun signOut() {
        auth.signOut()
        getGoogleSignInClient().signOut()
    }
}