package com.example.dicostory.data

import com.example.dicostory.data.api.ApiConfig
import com.example.dicostory.data.api.ApiService
import com.example.dicostory.data.api.RegisterResponse
import com.example.dicostory.data.pref.User
import com.example.dicostory.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
private val userPreference: UserPreference, private val apiService: ApiService
){

    suspend fun saveSession(user: User.UserModel){
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<User.UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout(){
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, ApiConfig.getApiService())
            }.also { instance = it }
    }

}