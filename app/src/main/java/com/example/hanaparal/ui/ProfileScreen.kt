package com.example.hanaparal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hanaparal.core.Constants
import com.example.hanaparal.data.FirebaseRepositories
import com.example.hanaparal.data.models.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    firebaseRepo: FirebaseRepositories,
    onProfileSaved: () -> Unit,
    onBack: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var courseProgram by remember { mutableStateOf("") }
    var role by remember { mutableStateOf(Constants.ROLE_STUDENT) }

    LaunchedEffect(Unit) {
        firebaseRepo.getCurrentUserProfile().onSuccess { profile ->
            if (profile != null) {
                name = profile.name
                email = profile.email
                courseProgram = profile.courseProgram
                role = profile.role
            }
            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile Setup") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Full Name",
                    icon = Icons.Default.Badge
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    icon = Icons.Default.Email,
                    enabled = false // Usually email is from auth provider
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileField(
                    value = courseProgram,
                    onValueChange = { courseProgram = it },
                    label = "Course / Program",
                    icon = Icons.Default.School
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        isSaving = true
                        scope.launch {
                            val profile = UserProfile(
                                name = name,
                                email = email,
                                courseProgram = courseProgram,
                                role = role
                            )
                            firebaseRepo.createOrUpdateUserProfile(profile)
                                .onSuccess {
                                    onProfileSaved()
                                }
                            isSaving = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSaving && name.isNotBlank() && courseProgram.isNotBlank()
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Profile")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}