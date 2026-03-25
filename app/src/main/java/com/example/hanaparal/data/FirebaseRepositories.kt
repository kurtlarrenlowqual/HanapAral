package com.example.hanaparal.data

import android.content.Context
import com.example.hanaparal.core.Constants
import com.example.hanaparal.data.models.StudyGroup          // ← NEW IMPORT
import com.example.hanaparal.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

class FirebaseRepositories(
    private val appContext: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    suspend fun subscribeToGlobalAnnouncements(): Result<Unit> {
        return runCatching {
            FirebaseMessaging.getInstance()
                .subscribeToTopic(Constants.TOPIC_GLOBAL_ANNOUNCEMENTS)
                .await()
        }
    }

    suspend fun getFcmToken(): Result<String> {
        return runCatching {
            FirebaseMessaging.getInstance().token.await()
        }
    }

    suspend fun saveCurrentUserTokenToFirestore(): Result<Unit> {
        return runCatching {
            val uid = auth.currentUser?.uid ?: error("No authenticated user.")
            val token = FirebaseMessaging.getInstance().token.await()

            firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .update("fcmToken", token)
                .await()
        }
    }

    suspend fun createOrUpdateUserProfile(profile: UserProfile): Result<Unit> {
        return runCatching {
            val uid = auth.currentUser?.uid ?: error("No authenticated user.")
            firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .set(profile.copy(uid = uid))
                .await()
        }
    }

    suspend fun getCurrentUserProfile(): Result<UserProfile?> {
        return runCatching {
            val uid = auth.currentUser?.uid ?: error("No authenticated user.")
            val snapshot = firestore.collection(Constants.USERS_COLLECTION)
                .document(uid)
                .get()
                .await()

            snapshot.toObject(UserProfile::class.java)
        }
    }

    suspend fun seedSuperuserIfMissing(
        name: String,
        courseProgram: String,
        email: String
    ): Result<Unit> {
        return runCatching {
            val uid = auth.currentUser?.uid ?: error("No authenticated user.")
            val docRef = firestore.collection(Constants.USERS_COLLECTION).document(uid)
            val snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                val token = FirebaseMessaging.getInstance().token.await()
                val user = UserProfile(
                    uid = uid,
                    name = name,
                    courseProgram = courseProgram,
                    email = email,
                    role = Constants.ROLE_SUPERUSER,
                    fcmToken = token
                )
                docRef.set(user).await()
            }
        }
    }

    suspend fun isCurrentUserSuperuser(): Result<Boolean> {
        return runCatching {
            val profile = getCurrentUserProfile().getOrThrow()
            profile?.role == Constants.ROLE_SUPERUSER
        }
    }

    // ================================================================
    // NEW FUNCTION FOR REQUIREMENT #3 – Study Group Creation
    // ================================================================
    /**
     * Creates a new study group.
     * - Only authenticated users can call this.
     * - Creator automatically becomes the group administrator (adminUid).
     * - Creator is added as the first member.
     * - Group is saved in Constants.GROUPS_COLLECTION.
     */
    suspend fun createStudyGroup(
        name: String,
        description: String
    ): Result<String> {
        return runCatching {
            val uid = auth.currentUser?.uid ?: error("No authenticated user.")

            // Create a reference with auto-generated document ID
            val groupRef = firestore.collection(Constants.GROUPS_COLLECTION).document()

            val group = StudyGroup(
                id = groupRef.id,
                name = name,
                description = description,
                adminUid = uid,
                members = listOf(uid),                    // Creator is first member
                createdAt = System.currentTimeMillis()
            )

            groupRef.set(group).await()

            group.id  // Return the generated group ID on success
        }
    }
}