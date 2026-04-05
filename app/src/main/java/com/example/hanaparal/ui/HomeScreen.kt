package com.example.hanaparal.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hanaparal.remoteconfig.RemoteConfigUiState
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults

@Composable
fun HomeScreen(
    navController: NavController,
    uiState: RemoteConfigUiState,
    onSubscribeTopic: suspend () -> Unit,
    onFetchRemoteConfig: suspend () -> Unit,
    onOpenSuperuser: () -> Unit,
    onLogout: () -> Unit
) {
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(modifier = Modifier.padding(bottom = 8.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = uiState.globalAnnouncementHeader,
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Group creation enabled: ${uiState.enableGroupCreation}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Max members per group: ${uiState.maxMembersPerGroup}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        Button(onClick = { onOpenSuperuser() }) {
            Text("Open Superuser Panel")
        }

        Button(onClick = { kotlinx.coroutines.GlobalScope.launch { onFetchRemoteConfig() } }) {
            Text("Fetch Remote Config")
        }

        Button(onClick = { kotlinx.coroutines.GlobalScope.launch { onSubscribeTopic() } }) {
            Text("Subscribe to Global Announcements Topic")
        }

        Button(onClick = {
            navController.navigate(Routes.PROFILE)
        }) {
            Text("Go to Profile")
        }

        Button(
            onClick = {
                navController.navigate(Routes.CREATE_GROUP)
            },
            enabled = uiState.enableGroupCreation
        ) {
            Text(
                if (uiState.enableGroupCreation)
                    "Create Study Group"
                else
                    "Group Creation Disabled"
            )
        }

        Button(onClick = {
            navController.navigate(Routes.GROUP_LIST)
        }) {
            Text("View Study Groups")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onLogout() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Logout")
        }
    }
}