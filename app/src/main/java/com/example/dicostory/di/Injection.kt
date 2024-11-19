package com.example.dicostory.di

import android.content.Context
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.local.room.StoryDatabase
import com.example.dicostory.data.pref.UserPreference
import com.example.dicostory.data.pref.dataStore
import com.example.dicostory.data.remote.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        val database = StoryDatabase.getInstance(context)
        val dao = database.storyDao()
        return UserRepository.getInstance(pref, apiService, dao)
    }
}