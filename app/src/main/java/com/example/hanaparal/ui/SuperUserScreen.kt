package com.example.hanaparal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import com.example.hanaparal.remoteconfig.RemoteConfigUiState

@Composable
fun SuperuserScreen(
    uiState: RemoteConfigUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Superuser Panel", style = MaterialTheme.typography.headlineMedium)

        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Remote Config Preview", style = MaterialTheme.typography.titleLarge)
                Text("Group creation enabled: ${uiState.enableGroupCreation}")
                Text("Global header: ${uiState.globalAnnouncementHeader}")
                Text("Max members: ${uiState.maxMembersPerGroup}")
            }
        }

        Text(
            text = "Superusers update these values from Firebase Remote Config console.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}