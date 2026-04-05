package com.example.hanaparal.data

import com.example.hanaparal.data.models.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        return runCatching {
            usersCollection.document(profile.uid)
                .set(profile)
                .await()
        }
    }

    suspend fun getUserProfile(uid: String): Result<UserProfile?> {
        return runCatching {
            val doc = usersCollection.document(uid).get().await()
            doc.toObject(UserProfile::class.java)
        }
    }
}