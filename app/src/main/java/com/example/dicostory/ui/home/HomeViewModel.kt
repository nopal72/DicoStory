package com.example.dicostory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.pref.UserModel
import com.example.dicostory.data.remote.response.StoryResponse

class HomeViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }


    val stories: LiveData<Result<StoryResponse>> = repository.getStories()
}