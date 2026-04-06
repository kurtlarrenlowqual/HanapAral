package com.example.hanaparal.ui.studygroup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.hanaparal.data.models.StudyGroup

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditGroupScreen(
    group: StudyGroup,
    onSave: (String, String, Int) -> Unit
) {
    var name by remember { mutableStateOf(group.name) }
    var subject by remember { mutableStateOf(group.subject) }
    var max by remember { mutableStateOf(group.maxMembers.toString()) }
    val canSave = name.isNotBlank() && subject.isNotBlank() && max.toIntOrNull() != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Group") },
                actions = {
                    TextButton(
                        onClick = { onSave(name, subject, max.toIntOrNull() ?: 10) },
                        enabled = canSave
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Group Details",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Group Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                )
            )

            OutlinedTextField(
                value = subject,
                onValueChange = { subject = it },
                label = { Text("Subject") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words
                )
            )

            OutlinedTextField(
                value = max,
                onValueChange = { max = it.filter { c -> c.isDigit() } },
                label = { Text("Max Members") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                supportingText = {
                    if (max.toIntOrNull() == null && max.isNotBlank()) {
                        Text("Enter a valid number")
                    }
                },
                isError = max.isNotBlank() && max.toIntOrNull() == null
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onSave(name, subject, max.toIntOrNull() ?: 10) },
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Save Changes")
            }
        }
    }
}