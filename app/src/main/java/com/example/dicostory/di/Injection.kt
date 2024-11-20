package com.example.dicostory.di

import android.content.Context
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.local.room.StoryDatabase
import com.example.dicostory.data.pref.UserPreference
import com.example.dicostory.data.pref.dataStore
import com.example.dicostory.data.remote.api.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        return UserRepository.getInstance(pref, apiService, dao)
    }
}