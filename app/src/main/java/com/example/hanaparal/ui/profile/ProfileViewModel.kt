package com.example.hanaparal.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hanaparal.data.FirebaseRepositories
import com.example.hanaparal.data.models.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: FirebaseRepositories) : ViewModel() {

    private val _profileState = MutableStateFlow<UserProfile?>(null)
    val profileState: StateFlow<UserProfile?> = _profileState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getCurrentUserProfile()
                .onSuccess { profile ->
                    _profileState.value = profile
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to load profile"
                }
            _isLoading.value = false
        }
    }

    fun saveProfile(profile: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.createOrUpdateUserProfile(profile)
                .onSuccess {
                    _profileState.value = profile
                    _error.value = null
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to save profile"
                }
            _isLoading.value = false
        }
    }

    class Factory(private val repository: FirebaseRepositories) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileViewModel(repository) as T
        }
    }
}