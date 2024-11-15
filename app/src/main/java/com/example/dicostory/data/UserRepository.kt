package com.example.dicostory.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dicostory.data.remote.api.ApiConfig
import com.example.dicostory.data.remote.api.ApiService
import com.example.dicostory.data.remote.response.LoginResponse
import com.example.dicostory.data.pref.LoginRequest
import com.example.dicostory.data.pref.RegisterRequest
import com.example.dicostory.data.pref.UserModel
import com.example.dicostory.data.pref.UserPreference
import com.example.dicostory.data.remote.response.DetailResponse
import com.example.dicostory.data.remote.response.RegisterResponse
import com.example.dicostory.data.remote.response.StoryResponse
import com.example.dicostory.utils.AppExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        result.value = Result.Loading
        val client = apiService.loginUser(request)
        client.enqueue(object : Callback<LoginResponse>{
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    responseBody?.let {
                        val user = UserModel(
                            it.loginResult.name,
                            it.loginResult.userId,
                            it.loginResult.token,
                            true
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            userPreference.saveSession(user)
                        }
                        result.value = Result.Success(it)
                    }
                }
            }
            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                result.value = Result.Error(t.toString())
            }
        })

        return result
    }

    fun register(request: RegisterRequest): LiveData<Result<RegisterResponse>> {
        val result = MutableLiveData<Result<RegisterResponse>>()
        result.value = Result.Loading
        val client = apiService.registerUser(request)
        client.enqueue(object : Callback<RegisterResponse>{
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        if(!it.error){
                            result.postValue(Result.Error(body.message))
                        } else {
                            result.postValue(Result.Success(it))
                        }
                    }
                } else {
                    result.postValue(Result.Error(response.message()))
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                result.postValue(Result.Error(t.toString()))
            }
        })

        return result
    }

    suspend fun logout(){
        Log.d("UserRepository", "Logout called====================================================")
        userPreference.logout()
    }

    fun getStories(): LiveData<Result<StoryResponse>> {
        val result = MutableLiveData<Result<StoryResponse>>()
        result.value = Result.Loading
        CoroutineScope(Dispatchers.IO).launch {
            userPreference.getSession().collect { user ->
                val client = apiService.getStories("Bearer ${user.token}")
                client.enqueue(object : Callback<StoryResponse> {
                    override fun onResponse(
                        call: Call<StoryResponse>,
                        response: retrofit2.Response<StoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                result.value = Result.Success(it)
                            }
                        }
                }
                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        result.value = Result.Error(t.toString())
                    }
                })
            }
        }
        return result
    }

    fun getDetailStory(id: String): LiveData<Result<DetailResponse>> {
        val result = MutableLiveData<Result<DetailResponse>>()
        result.value = Result.Loading
        CoroutineScope(Dispatchers.IO).launch {
            userPreference.getSession().collect { user ->
                val client = apiService.getDetailStory("Bearer ${user.token}", id)
                client.enqueue(object : Callback<DetailResponse> {
                    override fun onResponse(
                        call: Call<DetailResponse>,
                        response: retrofit2.Response<DetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                result.postValue(Result.Success(it))
                            } ?: run {
                                result.postValue(Result.Error(Exception("Empty response body").toString()))
                            }
                        } else {
                            result.postValue(Result.Error(Exception("Error: ${response.code()} ${response.message()}").toString()))
                        }
                    }

                    override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                        result.postValue(Result.Error(t.toString()))
                    }
                })
            }
        }

        return result
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