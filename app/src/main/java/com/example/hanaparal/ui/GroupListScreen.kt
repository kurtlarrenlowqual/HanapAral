package com.example.hanaparal.ui

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hanaparal.R
import com.example.hanaparal.auth.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.coroutines.launch
import com.example.hanaparal.ui.studygroup.GroupListViewModel

@Composable
fun GroupListScreen(viewModel: GroupListViewModel) {

    val groups = viewModel.groups

    LaunchedEffect(Unit) {
        viewModel.loadGroups()
    }

    Column {
        Text("Available Study Groups")

        groups.forEach { group ->
            Card(modifier = Modifier.padding(8.dp)) {

                Column(modifier = Modifier.padding(8.dp)) {
                    Text(group.name)
                    Text("Subject: ${group.subject}")
                    Text("Members: ${group.members.size}/${group.maxMembers}")

                    Button(onClick = {
                        viewModel.joinGroup(group.id)
                    }) {
                        Text("Join")
                    }
                }
            }
        }
    }
}