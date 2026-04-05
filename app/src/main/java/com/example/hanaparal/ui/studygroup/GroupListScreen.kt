package com.example.hanaparal.ui.studygroup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GroupListScreen(viewModel: GroupListViewModel) {

    val groups = viewModel.groups
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Available Study Groups")

        Spacer(modifier = Modifier.height(8.dp))

        groups.forEach { group ->

            val alreadyJoined = userId != null && group.members.contains(userId)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {

                Column(modifier = Modifier.padding(12.dp)) {

                    Text(group.name, style = MaterialTheme.typography.titleMedium)
                    Text("Subject: ${group.subject}")
                    Text("Members: ${group.members.size}/${group.maxMembers}")

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        enabled = !alreadyJoined,
                        onClick = {
                            viewModel.joinGroup(group.id)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (alreadyJoined) "Joined" else "Join")
                    }
                }
            }
        }
    }
}