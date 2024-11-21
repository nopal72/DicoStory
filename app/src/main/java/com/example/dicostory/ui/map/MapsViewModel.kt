package com.example.dicostory.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicostory.data.Result
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.local.entity.StoryEntity

class MapsViewModel(private val repository: UserRepository) : ViewModel() {
    fun getStoriesWithLocation(): LiveData<Result<List<StoryEntity>>> = repository.getStoriesWithLocation()
}