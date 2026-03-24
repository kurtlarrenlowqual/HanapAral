package com.example.hanaparal.fcm

import android.util.Log
import com.example.hanaparal.core.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class HanapAralMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection(Constants.USERS_COLLECTION)
            .document(uid)
            .update("fcmToken", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "HanapAral"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: "You have a new notification."

        NotificationHelper.showNotification(
            context = applicationContext,
            title = title,
            body = body
        )
    }
}