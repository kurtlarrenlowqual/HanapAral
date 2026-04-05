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
import kotlin.text.get
import kotlin.text.toInt


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


    suspend fun getStudyGroups(): Result<List<StudyGroup>> {
        return runCatching {
            firestore.collection("study_groups")
                .get()
                .await()
                .toObjects(StudyGroup::class.java)
        }
    }


    suspend fun joinGroup(groupId: String): Result<Unit> {
        return runCatching {
            val user = FirebaseAuth.getInstance().currentUser ?: error("No user")

            val docRef = firestore.collection("study_groups").document(groupId)

            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(docRef)

                val members = snapshot.get("members") as List<String>

                if (members.contains(user.uid)) return@runTransaction

                if (members.size >= snapshot.getLong("maxMembers")!!.toInt()) {
                    throw Exception("Group is full")
                }

                val updated = members + user.uid
                transaction.update(docRef, "members", updated)
            }.await()
        }
    }

    suspend fun updateStudyGroup(
        groupId: String,
        name: String,
        subject: String,
        maxMembers: Int
    ): Result<Unit> {
        return runCatching {
            val docRef = firestore.collection("study_groups").document(groupId)

            firestore.runTransaction { tx ->
                val snapshot = tx.get(docRef)

                val adminId = snapshot.getString("adminId")
                val currentUser = auth.currentUser?.uid ?: error("No user")

                if (adminId != currentUser) {
                    throw Exception("Only admin can edit")
                }

                tx.update(docRef, mapOf(
                    "name" to name,
                    "subject" to subject,
                    "maxMembers" to maxMembers
                ))
            }.await()
        }
    }

    suspend fun deleteStudyGroup(groupId: String): Result<Unit> {
        return runCatching {
            val docRef = firestore.collection("study_groups").document(groupId)

            val snapshot = docRef.get().await()
            val adminId = snapshot.getString("adminId")

            val currentUser = auth.currentUser?.uid ?: error("No user")

            if (adminId != currentUser) {
                throw Exception("Only admin can delete")
            }

            docRef.delete().await()
        }
    }

    suspend fun getGroupById(groupId: String): Result<StudyGroup> {
        return runCatching {
            firestore.collection("study_groups")
                .document(groupId)
                .get()
                .await()
                .toObject(StudyGroup::class.java)
                ?: error("Group not found")
        }
    }
}