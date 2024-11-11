package com.example.dicostory.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.api.LoginResponse
import com.example.dicostory.data.pref.LoginRequest

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    fun login(email: String, password: String) {
        val request = LoginRequest(email, password)
        val result = repository.login(request)

        result.observeForever { loginResult ->
            _loginResult.value = loginResult
        }
    }

}