package com.example.hanaparal.ui.studygroup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun GroupListScreen(
    viewModel: GroupListViewModel,
    onEdit: (String) -> Unit,
    onViewMembers: (String) -> Unit
) {

    val groups = viewModel.groups
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Available Study Groups")

        Text(viewModel.message)

        groups.forEach { group ->

            val alreadyJoined = userId != null && group.members.contains(userId)
            val isAdmin = userId == group.adminId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {

                Column(modifier = Modifier.padding(12.dp)) {

                    Text(group.name)
                    Text("Subject: ${group.subject}")
                    Text("Members: ${group.members.size}/${group.maxMembers}")

                    Spacer(modifier = Modifier.height(8.dp))

                    // JOIN BUTTON
                    Button(
                        enabled = !alreadyJoined,
                        onClick = { viewModel.joinGroup(group.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (alreadyJoined) "Joined" else "Join")
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // VIEW MEMBERS
                    Button(
                        onClick = { onViewMembers(group.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Members")
                    }

                    // ADMIN CONTROLS
                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(6.dp))

                        Button(
                            onClick = { onEdit(group.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Edit Group")
                        }

                        Button(
                            onClick = { viewModel.deleteGroup(group.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete Group")
                        }
                    }
                }
            }
        }
    }
}