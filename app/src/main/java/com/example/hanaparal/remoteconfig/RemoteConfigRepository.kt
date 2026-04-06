package com.example.hanaparal.remoteconfig

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.tasks.await
import com.example.hanaparal.core.Constants

class RemoteConfigRepository {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    fun initDefaults() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour in prod; lower in debug if needed
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf(
            Constants.RC_ENABLE_GROUP_CREATION to true,
            Constants.RC_GLOBAL_ANNOUNCEMENT_HEADER to "Welcome to HanapAral",
            Constants.RC_MAX_MEMBERS_PER_GROUP to 10L
        )
        remoteConfig.setDefaultsAsync(defaults)
    }

    suspend fun fetchAndActivate(): Result<Boolean> {
        return runCatching {
            remoteConfig.fetchAndActivate().await()
        }
    }

    fun readState(): RemoteConfigUiState {
        return RemoteConfigUiState(
            enableGroupCreation = remoteConfig.getBoolean(Constants.RC_ENABLE_GROUP_CREATION),
            globalAnnouncementHeader = remoteConfig.getString(Constants.RC_GLOBAL_ANNOUNCEMENT_HEADER),
            maxMembersPerGroup = remoteConfig.getLong(Constants.RC_MAX_MEMBERS_PER_GROUP)
        )
    }
}