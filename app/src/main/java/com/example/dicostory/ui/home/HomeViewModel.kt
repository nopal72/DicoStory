package com.example.dicostory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.Result
import com.example.dicostory.data.local.entity.StoryEntity

class HomeViewModel(private val repository: UserRepository) : ViewModel() {
    fun getStories(): LiveData<Result<List<StoryEntity>>> = repository.getStories()
}