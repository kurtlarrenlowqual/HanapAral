package com.example.hanaparal.ui.studygroup

import androidx.compose.runtime.Composable
import com.example.hanaparal.data.models.StudyGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import android.R.attr.name

@Composable
fun GroupMembersScreen(group: StudyGroup) {

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Members of ${group.name}")

        group.members.forEach { uid ->
            Text("$uid")
        }
    }
}