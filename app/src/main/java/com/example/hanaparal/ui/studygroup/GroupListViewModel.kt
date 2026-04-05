package com.example.hanaparal.ui.studygroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hanaparal.data.FirebaseRepositories
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.hanaparal.data.models.StudyGroup
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.hanaparal.data.models.UserProfile

class GroupListViewModel(
    private val repo: FirebaseRepositories
) : ViewModel() {

    var groups by mutableStateOf<List<StudyGroup>>(emptyList())
    var message by mutableStateOf("")

    var selectedGroup by mutableStateOf<StudyGroup?>(null)

    var groupMembers by mutableStateOf<List<UserProfile>>(emptyList())

    fun loadGroups() {
        viewModelScope.launch {
            repo.getStudyGroups()
                .onSuccess { groups = it }
        }
    }

    fun joinGroup(groupId: String) {
        viewModelScope.launch {
            repo.joinGroup(groupId)
                .onSuccess {
                    message = "Joined successfully"
                    loadGroups()
                }
                .onFailure { message = it.message ?: "Error" }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            repo.deleteStudyGroup(groupId)
                .onSuccess {
                    message = "Group deleted"
                    loadGroups()
                }
                .onFailure {
                    message = it.message ?: "Error deleting"
                }
        }
    }

    fun updateGroup(
        groupId: String,
        name: String,
        subject: String,
        maxMembers: Int
    ) {
        viewModelScope.launch {
            repo.updateStudyGroup(groupId, name, subject, maxMembers)
                .onSuccess {
                    message = "Group updated"
                    loadGroups()
                }
                .onFailure {
                    message = it.message ?: "Error updating"
                }
        }
    }

    fun loadGroupById(groupId: String) {
        viewModelScope.launch {
            repo.getGroupById(groupId)
                .onSuccess {
                    selectedGroup = it
                }
        }
    }

    fun loadMembers(group: StudyGroup) {
        viewModelScope.launch {
            repo.getUsersByIds(group.members)
                .onSuccess {
                    groupMembers = it
                }
        }
    }
}