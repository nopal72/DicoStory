package com.example.dicostory.ui.map

import androidx.lifecycle.ViewModel
import com.example.dicostory.data.UserRepository

class MapViewModel(private val userRepository: UserRepository) : ViewModel()  {
    fun getStoriesWithLocation() = userRepository.getStoriesWithLocation()
}