package com.example.hanaparal.core

import android.content.Context
import com.example.hanaparal.data.FirebaseRepositories
import com.example.hanaparal.remoteconfig.RemoteConfigRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AppContainer(context: Context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    val firebaseRepositories = FirebaseRepositories(
        appContext = context,
        auth = auth,
        firestore = firestore
    )

    val remoteConfigRepository = RemoteConfigRepository()
}