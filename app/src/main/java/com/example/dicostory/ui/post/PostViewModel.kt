package com.example.dicostory.ui.post

import androidx.lifecycle.ViewModel
import com.example.dicostory.data.UserRepository
import java.io.File

class PostViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun uploadStory(file: File, description: String, lat: Double?, lon: Double?) = userRepository.uploadStory(file, description, lat, lon)
}