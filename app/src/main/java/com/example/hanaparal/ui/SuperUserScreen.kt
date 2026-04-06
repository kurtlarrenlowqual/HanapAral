package com.example.hanaparal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hanaparal.remoteconfig.RemoteConfigUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperuserScreen(
    uiState: RemoteConfigUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Superuser Panel") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Remote Config",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ConfigRow(
                        label = "Group Creation",
                        value = if (uiState.enableGroupCreation) "Enabled" else "Disabled"
                    )
                    HorizontalDivider()
                    ConfigRow(
                        label = "Global Announcement",
                        value = uiState.globalAnnouncementHeader.ifBlank { "—" }
                    )
                    HorizontalDivider()
                    ConfigRow(
                        label = "Max Members per Group",
                        value = uiState.maxMembersPerGroup.toString()
                    )
                }
            }

            Text(
                text = "These values are managed from the Firebase Remote Config console.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
private fun ConfigRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}