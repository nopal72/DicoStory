package com.example.dicostory.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.remote.response.DetailResponse
import com.example.dicostory.data.Result

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    fun getDetail(id: String): LiveData<Result<DetailResponse>> = repository.getDetailStory(id)
}