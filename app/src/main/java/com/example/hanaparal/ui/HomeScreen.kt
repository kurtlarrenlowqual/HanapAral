package com.example.hanaparal.ui

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.hanaparal.remoteconfig.RemoteConfigUiState
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    uiState: RemoteConfigUiState,
    onSubscribeTopic: suspend () -> Unit,
    onFetchRemoteConfig: suspend () -> Unit,
    onOpenSuperuser: () -> Unit,
    onCreateStudyGroup: suspend (String, String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Dialog states
    var showCreateDialog by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }

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

        Button(onClick = { coroutineScope.launch { onFetchRemoteConfig() } }) {
            Text("Fetch Remote Config")
        }

        Button(onClick = { coroutineScope.launch { onSubscribeTopic() } }) {
            Text("Subscribe to Global Announcements Topic")
        }

        // ==================== REQUIREMENT #3 – IMPROVED ====================
        if (uiState.enableGroupCreation) {
            Button(onClick = { showCreateDialog = true }) {
                Text("Create Study Group")
            }
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { if (!isCreating) showCreateDialog = false },
                title = { Text("Create New Study Group") },
                text = {
                    Column {
                        TextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text("Group Name *") },
                            isError = groupName.isBlank(),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TextField(
                            value = groupDescription,
                            onValueChange = { groupDescription = it },
                            label = { Text("Description") }
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (groupName.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "Group name cannot be empty",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            isCreating = true
                            coroutineScope.launch {
                                try {
                                    onCreateStudyGroup(groupName, groupDescription)
                                    Toast.makeText(
                                        context,
                                        "Study group created successfully!",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        "Failed to create group: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } finally {
                                    isCreating = false
                                    showCreateDialog = false
                                    groupName = ""
                                    groupDescription = ""
                                }
                            }
                        },
                        enabled = !isCreating
                    ) {
                        Text(if (isCreating) "Creating..." else "Create")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { if (!isCreating) showCreateDialog = false },
                        enabled = !isCreating
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}