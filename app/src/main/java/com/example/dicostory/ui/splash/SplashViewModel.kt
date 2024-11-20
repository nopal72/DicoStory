package com.example.dicostory.ui.splash

import androidx.lifecycle.ViewModel
import com.example.dicostory.data.UserRepository

class SplashViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession() = repository.getSession()
}