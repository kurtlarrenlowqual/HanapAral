package com.example.hanaparal.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hanaparal.data.models.UserProfile
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onProfileSaved: () -> Unit) {

    val profile by viewModel.profileState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val user = FirebaseAuth.getInstance().currentUser ?: return

    var name by remember { mutableStateOf("") }
    var course by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    LaunchedEffect(profile) {
        profile?.let {
            name = it.name
            course = it.courseProgram
            email = it.email
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Student Profile", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            if (error != null) {
                Text(text = error!!, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = course,
                onValueChange = { course = it },
                label = { Text("Course / Program") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val updatedProfile = UserProfile(
                        uid = user.uid,
                        name = name,
                        courseProgram = course,
                        email = email
                    )
                    viewModel.saveProfile(updatedProfile)
                    onProfileSaved()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Save Profile")
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}