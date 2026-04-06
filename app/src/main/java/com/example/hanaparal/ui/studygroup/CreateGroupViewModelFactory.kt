package com.example.hanaparal.ui.studygroup

import androidx.lifecycle.ViewModelProvider
import com.example.hanaparal.data.FirebaseRepositories
import androidx.lifecycle.ViewModel

class CreateGroupViewModelFactory(
    private val repo: FirebaseRepositories
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CreateGroupViewModel(repo) as T
    }
}