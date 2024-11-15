package com.example.dicostory.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicostory.data.Result
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.remote.response.RegisterResponse
import com.example.dicostory.data.pref.RegisterRequest

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun register(request: RegisterRequest): LiveData<Result<RegisterResponse>> = userRepository.register(request)
}