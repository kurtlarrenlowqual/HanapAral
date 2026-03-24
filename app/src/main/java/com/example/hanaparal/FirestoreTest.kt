package com.example.hanaparal

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreTest {
    fun writeSampleUser() {
        val db = FirebaseFirestore.getInstance()

        val sample = hashMapOf(
            "name" to "User1",
            "courseProgram" to "BSCS",
            "email" to "user@example.com",
            "role" to "superuser"
        )

        db.collection("users")
            .document("test_user_1")
            .set(sample)
            .addOnSuccessListener {
                Log.d("FIRESTORE_TEST", "User written successfully")
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE_TEST", "Write failed", e)
            }
    }
}