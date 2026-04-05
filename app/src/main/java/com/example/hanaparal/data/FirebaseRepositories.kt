package com.example.hanaparal.data

import android.content.Context
import com.example.hanaparal.core.Constants
import com.example.hanaparal.data.models.UserProfile
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import  com.example.hanaparal.data.models.StudyGroup


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

    suspend fun createStudyGroup(
        name: String,
        subject: String,
        maxMembers: Int
    ): Result<Unit> {
        return runCatching {
            val user = FirebaseAuth.getInstance().currentUser ?: error("No user")

            val groupRef = firestore.collection("study_groups").document()

            val group = StudyGroup(
                id = groupRef.id,
                name = name,
                subject = subject,
                adminId = user.uid,
                members = listOf(user.uid),
                maxMembers = maxMembers
            )

            groupRef.set(group).await()
        }
    }
}