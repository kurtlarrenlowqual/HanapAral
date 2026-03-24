package com.example.hanaparal

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings

object RemoteConfigTest {

    private val remoteConfig = Firebase.remoteConfig

    fun init() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf(
            "enable_group_creation" to true,
            "global_announcement_header" to "Default Header",
            "max_members_per_group" to 5L
        )
        remoteConfig.setDefaultsAsync(defaults)
    }

    fun fetchValues() {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val enableGroupCreation =
                        remoteConfig.getBoolean("enable_group_creation")
                    val header =
                        remoteConfig.getString("global_announcement_header")
                    val maxMembers =
                        remoteConfig.getLong("max_members_per_group")

                    Log.d("RC_TEST", "enable_group_creation: $enableGroupCreation")
                    Log.d("RC_TEST", "global_announcement_header: $header")
                    Log.d("RC_TEST", "max_members_per_group: $maxMembers")
                } else {
                    Log.e("RC_TEST", "Fetch failed", task.exception)
                }
            }
    }
}