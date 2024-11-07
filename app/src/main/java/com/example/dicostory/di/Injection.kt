package com.example.dicostory.di

import android.content.Context
import com.example.dicostory.data.UserRepository
import com.example.dicostory.data.pref.UserPreference
import com.example.dicostory.data.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}