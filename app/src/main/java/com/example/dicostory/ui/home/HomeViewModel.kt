package com.example.dicostory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.remote.response.StoryResponse
import com.example.dicostory.data.Result

class HomeViewModel(private val repository: UserRepository) : ViewModel() {
    val stories: LiveData<Result<StoryResponse>> = repository.getStories()
}