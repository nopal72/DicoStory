package com.example.dicostory.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dicostory.data.api.ApiConfig
import com.example.dicostory.data.api.ApiService
import com.example.dicostory.data.api.LoginResponse
import com.example.dicostory.data.pref.LoginRequest
import com.example.dicostory.data.pref.UserModel
import com.example.dicostory.data.pref.UserPreference
import com.example.dicostory.utils.AppExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback

class UserRepository private constructor(
private val userPreference: UserPreference, private val apiService: ApiService, private val appExecutors: AppExecutors
){

    suspend fun saveSession(user: UserModel){
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun login(request: LoginRequest): LiveData<Result<LoginResponse>> {
        val result = MutableLiveData<Result<LoginResponse>>()
        val client = apiService.loginUser(request)
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(
                call: Call<LoginResponse>,
                response: retrofit2.Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    responseBody?.let {
                        val user = UserModel(
                            it.loginResult.name,
                            it.loginResult.token,
                            true
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            userPreference.saveSession(user)
                        }
                        result.value = Result.success(it)
                    }
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })

        return result
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
                instance ?: UserRepository(userPreference, ApiConfig.getApiService(), AppExecutors())
            }.also { instance = it }
    }

}