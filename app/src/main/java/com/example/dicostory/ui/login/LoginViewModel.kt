package com.example.dicostory.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.pref.User
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)
    {
        viewModelScope.launch {
            repository.saveSession(User.UserModel(email, it.loginResult.token))
        }
    }
}