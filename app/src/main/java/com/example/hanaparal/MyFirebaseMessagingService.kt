package com.example.hanaparal

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_TEST", "New token: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        Log.d("FCM_TEST", "Message received")
        Log.d("FCM_TEST", "Title: ${message.notification?.title}")
        Log.d("FCM_TEST", "Body: ${message.notification?.body}")
    }
}