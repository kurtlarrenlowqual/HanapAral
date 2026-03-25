package com.example.hanaparal.data.models

data class StudyGroup(
    val id: String = "",                    // Firestore auto-generated ID
    val name: String = "",
    val description: String = "",
    val adminUid: String = "",
    val members: List<String> = emptyList(), // UIDs of members (creator is first)
    val createdAt: Long = System.currentTimeMillis()
)