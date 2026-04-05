package com.example.hanaparal.ui.studygroup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hanaparal.data.FirebaseRepositories
import com.example.hanaparal.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateGroupViewModel(
    private val repo: FirebaseRepositories
) : ViewModel() {

    fun createGroup(
        name: String,
        subject: String,
        maxMembers: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repo.createStudyGroup(name, subject, maxMembers)
                .onSuccess { onSuccess() }
        }
    }
}