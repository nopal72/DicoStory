package com.example.dicostory.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicostory.data.api.ApiConfig
import com.example.dicostory.data.api.RegisterResponse
import com.example.dicostory.data.pref.User

class RegisterViewModel : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResponse>()
    val registerResult: LiveData<RegisterResponse> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage


    fun registerUser(request: User.RegisterRequest) {
        val client = ApiConfig.getApiService().registerUser(request)
        client.enqueue(object : retrofit2.Callback<RegisterResponse> {
            override fun onResponse(
                call: retrofit2.Call<RegisterResponse>,
                response: retrofit2.Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    _registerResult.value = response.body()
                    _isLoading.value = false
                    Log.d("RegisterViewModel", "onResponse: ${response.body()}")
                } else {
                    _errorMessage.value = response.message()
                    Log.e("RegisterViewModel", "onFailure: ${response.message()}")
                }
            }
            override fun onFailure(call: retrofit2.Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = t.message
                Log.e("RegisterViewModel", "onFailure: ${t.message.toString()}")
            }
        })
    }
}