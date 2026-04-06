package com.example.hanaparal.core

object Constants {
    const val USERS_COLLECTION = "users"
    const val GROUPS_COLLECTION = "study_groups"

    // Remote Config keys
    const val RC_ENABLE_GROUP_CREATION = "enable_group_creation"
    const val RC_GLOBAL_ANNOUNCEMENT_HEADER = "global_announcement_header"
    const val RC_MAX_MEMBERS_PER_GROUP = "max_members_per_group"

    // Roles
    const val ROLE_STUDENT = "student"
    const val ROLE_SUPERUSER = "superuser"

    // Notification channel
    const val NOTIFICATION_CHANNEL_ID = "hanaparal_general"
    const val NOTIFICATION_CHANNEL_NAME = "HanapAral Notifications"

    // FCM topics
    const val TOPIC_GLOBAL_ANNOUNCEMENTS = "global_announcements"
}