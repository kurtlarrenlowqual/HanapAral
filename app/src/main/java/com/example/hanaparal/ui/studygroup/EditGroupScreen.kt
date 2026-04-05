package com.example.hanaparal.ui.studygroup

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.example.hanaparal.data.models.StudyGroup

@Composable
fun EditGroupScreen(
    group: StudyGroup,
    onSave: (String, String, Int) -> Unit
) {

    var name by remember { mutableStateOf(group.name) }
    var subject by remember { mutableStateOf(group.subject) }
    var max by remember { mutableStateOf(group.maxMembers.toString()) }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Edit Group")

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
        OutlinedTextField(value = subject, onValueChange = { subject = it }, label = { Text("Subject") })
        OutlinedTextField(value = max, onValueChange = { max = it }, label = { Text("Max Members") })

        Button(onClick = {
            onSave(name, subject, max.toIntOrNull() ?: 10)
        }) {
            Text("Save")
        }
    }
}