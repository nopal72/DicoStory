package com.example.dicostory.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.dicostory.data.local.entity.StoryEntity
import com.example.dicostory.data.local.room.StoryDao
import com.example.dicostory.data.remote.api.ApiService
import com.example.dicostory.data.remote.response.LoginResponse
import com.example.dicostory.data.pref.LoginRequest
import com.example.dicostory.data.pref.RegisterRequest
import com.example.dicostory.data.pref.UserModel
import com.example.dicostory.data.pref.UserPreference
import com.example.dicostory.data.remote.response.UploadStoryResponse
import com.example.dicostory.data.remote.response.DetailResponse
import com.example.dicostory.data.remote.response.ListStoryItem
import com.example.dicostory.data.remote.response.RegisterResponse
import com.example.dicostory.data.remote.response.StoryResponse
import com.example.dicostory.ui.home.StoryPagingSource
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UserRepository private constructor(
    private val userPreference: UserPreference,
    private val apiService: ApiService,
    private val storyDao: StoryDao
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    fun login(request: LoginRequest): LiveData<Result<LoginResponse>> {
        val result = MutableLiveData<Result<LoginResponse>>()
        result.value = Result.Loading
        val client = apiService.loginUser(request.email, request.password)
        client.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        if(!it.error){
                            val user = UserModel(
                                it.loginResult.name,
                                it.loginResult.userId,
                                it.loginResult.token,
                                true
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                saveSession(user)
                            }
                            result.postValue(Result.Success(it))
                        } else {
                            result.postValue(Result.Error(it.message))
                        }
                    }
                }
                else{
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        val errorResponse = Gson().fromJson(it, LoginResponse::class.java)
                        result.postValue(Result.Error(errorResponse.message))
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                result.postValue(Result.Error(t.toString()))
            }
        })

        return result
    }

    fun register(request: RegisterRequest): LiveData<Result<RegisterResponse>> {
        val result = MutableLiveData<Result<RegisterResponse>>()
        result.value = Result.Loading
        val client = apiService.registerUser(request.name,request.email, request.password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    body?.let {
                        if (!it.error) {
                            result.postValue(Result.Success(it))
                        } else {
                            result.postValue(Result.Error(body.message))
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    errorBody?.let {
                        val errorResponse = Gson().fromJson(it, RegisterResponse::class.java)
                        result.postValue(Result.Error(errorResponse.message))
                    }
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                result.postValue(Result.Error(t.toString()))
            }
        })

        return result
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        val token = runBlocking {
            userPreference.getSession().first().token
        }
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService, "Bearer $token")
            }
        ).liveData
    }

    fun getStoriesWithLocation(): LiveData<Result<List<StoryEntity>>> {
        val result = MutableLiveData<Result<List<StoryEntity>>>()
        result.value = Result.Loading
        CoroutineScope(Dispatchers.IO).launch {
            userPreference.getSession().collect { user ->
                val client = apiService.getStoriesWithLocation(1,"Bearer ${user.token}")
                client.enqueue(object : Callback<StoryResponse> {
                    override fun onResponse(
                        call: Call<StoryResponse>,
                        response: Response<StoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            val listStory = responseBody?.listStory?.map { story ->
                                StoryEntity(
                                    story.id,
                                    story.name,
                                    story.description,
                                    story.photoUrl,
                                    story.createdAt,
                                    story.lat,
                                    story.lon
                                )
                            } ?: emptyList()
                            CoroutineScope(Dispatchers.IO).launch {
                                storyDao.insertStories(listStory)
                                withContext(Dispatchers.Main) {
                                    result.postValue(Result.Success(listStory))
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                        result.postValue(Result.Error(t.toString()))
                    }
                })
            }
        }
        return result
    }

    fun uploadStory(imageFile: File, description: String): LiveData<Result<UploadStoryResponse>> {
        val result = MutableLiveData<Result<UploadStoryResponse>>()
        result.value = Result.Loading
        val requestBody =description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        CoroutineScope(Dispatchers.IO).launch {
            userPreference.getSession().collect{user->
                val client = apiService.addNewStory(multipartBody, requestBody,"Bearer ${user.token}")
                client.enqueue(object : Callback<UploadStoryResponse> {
                    override fun onResponse(
                        call: Call<UploadStoryResponse>,
                        response: Response<UploadStoryResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                result.postValue(Result.Success(it))
                            }
                        } else {
                            val errorBody = response.errorBody()?.string()
                            errorBody?.let {
                                val errorResponse = Gson().fromJson(it, UploadStoryResponse::class.java)
                                result.postValue(Result.Error(errorResponse.message))
                            }
                        }
                    }

                    override fun onFailure(call: Call<UploadStoryResponse>, t: Throwable) {
                        result.postValue(Result.Error(t.toString()))
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
            userPreference.getSession().collect{user->
                val client = apiService.getDetailStory(id,"Bearer ${user.token}")
                client.enqueue(object : Callback<DetailResponse> {
                    override fun onResponse(
                        call: Call<DetailResponse>,
                        response: Response<DetailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            responseBody?.let {
                                result.postValue(Result.Success(it))
                            } ?: run {
                                result.postValue(Result.Error(Exception("Empty response body").toString()))
                            }
                        } else {
                            result.postValue(
                                Result.Error(Exception("Error: ${response.code()} ${response.message()}").toString())
                            )
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
            userPreference: UserPreference,
            apiService: ApiService,
            storyDao: StoryDao
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService, storyDao)
            }.also { instance = it }
    }
}
