package com.example.hanaparal.data.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val courseProgram: String = "",
    val email: String = "",
    val role: String = "student",
    val fcmToken: String? = null
)