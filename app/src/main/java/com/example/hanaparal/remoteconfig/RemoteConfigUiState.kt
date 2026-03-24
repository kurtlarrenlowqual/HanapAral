package com.example.hanaparal.remoteconfig

data class RemoteConfigUiState(
    val enableGroupCreation: Boolean = true,
    val globalAnnouncementHeader: String = "Welcome to HanapAral",
    val maxMembersPerGroup: Long = 10
)