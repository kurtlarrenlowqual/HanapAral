package com.example.hanaparal.ui.studygroup

import androidx.lifecycle.ViewModelProvider
import com.example.hanaparal.data.FirebaseRepositories
import androidx.lifecycle.ViewModel

class GroupListViewModelFactory(
    private val repo: FirebaseRepositories
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GroupListViewModel(repo) as T
    }
}