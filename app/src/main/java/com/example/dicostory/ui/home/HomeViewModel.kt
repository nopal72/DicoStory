package com.example.dicostory.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.remote.response.ListStoryItem

class HomeViewModel(private val repository: UserRepository) : ViewModel() {
    val story: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)
}