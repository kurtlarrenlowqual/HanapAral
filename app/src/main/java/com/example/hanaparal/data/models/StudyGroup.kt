package com.example.hanaparal.data.models

data class StudyGroup(
    val id: String = "",
    val name: String = "",
    val subject: String = "",
    val adminId: String = "",
    val members: List<String> = emptyList(),
    val maxMembers: Int = 10
)