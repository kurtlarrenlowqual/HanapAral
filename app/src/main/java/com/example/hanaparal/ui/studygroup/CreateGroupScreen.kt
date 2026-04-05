package com.example.hanaparal.ui.studygroup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hanaparal.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreateGroupScreen(
    viewModel: CreateGroupViewModel,
    onGroupCreated: () -> Unit
) {

    var name by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Create Study Group")

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Group Name") })
        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })

        Button(onClick = {
            viewModel.createGroup(name, subject, 10) {
                onGroupCreated()
            }
        }) {
            Text("Create")
        }
    }
}