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

class GroupListViewModel(
    private val repo: FirebaseRepositories
) : ViewModel() {

    var groups by mutableStateOf<List<StudyGroup>>(emptyList())
    var message by mutableStateOf("")

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
}