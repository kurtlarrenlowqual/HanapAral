package com.example.hanaparal.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hanaparal.data.models.UserProfile

@Composable
fun ProfileScreen() {

    val viewModel: ProfileViewModel = viewModel()

    // 🔥 TEMP UID (since no auth yet)
    val fakeUid = "test_user_123"

    var name by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val profile by viewModel.profileState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadProfile(fakeUid)
    }

    // If profile exists → autofill
    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            course = it.courseProgram
            email = it.email
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Student Profile", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = course,
            onValueChange = { course = it },
            label = { Text("Course / Program") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val profile = UserProfile(
                    uid = fakeUid,
                    name = name,
                    courseProgram = course,
                    email = email
                )
                viewModel.saveProfile(profile)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Profile")
        }
    }
}