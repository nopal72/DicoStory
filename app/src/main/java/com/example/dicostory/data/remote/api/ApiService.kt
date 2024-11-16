package com.example.dicostory.data.remote.api

import com.example.dicostory.data.pref.AddNewStoryRequest
import com.example.dicostory.data.pref.LoginRequest
import com.example.dicostory.data.pref.RegisterRequest
import com.example.dicostory.data.remote.response.AddNewStoryResponse
import com.example.dicostory.data.remote.response.DetailResponse
import com.example.dicostory.data.remote.response.LoginResponse
import com.example.dicostory.data.remote.response.RegisterResponse
import com.example.dicostory.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("register")
    fun registerUser(
        @Body body: RegisterRequest
    ): Call<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginUser(
        @Body body: LoginRequest
    ): Call<LoginResponse>

    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<StoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<DetailResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description")description:RequestBody,
        @Header("Authorization") token: String
    ): Call<AddNewStoryResponse>
}
