package com.example.dicostory.data.remote.api

import com.example.dicostory.data.remote.response.UploadStoryResponse
import com.example.dicostory.data.remote.response.DetailResponse
import com.example.dicostory.data.remote.response.LoginResponse
import com.example.dicostory.data.remote.response.RegisterResponse
import com.example.dicostory.data.remote.response.StoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Header
        ("Authorization") token: String
    ): Response<StoryResponse>

    @GET("stories")
    fun getStoriesWithLocation(
        @Query
        ("location") location : Int = 1,
        @Header
            ("Authorization") token: String
    ): Call<StoryResponse>

    @GET("stories/{id}")
    fun getDetailStory(
        @Path("id") id: String,
        @Header
        ("Authorization") token: String
    ): Call<DetailResponse>

    @Multipart
    @POST("stories")
    fun addNewStory(
        @Part file: MultipartBody.Part,
        @Part("description")description:RequestBody,
        @Part("lat")lat:RequestBody?,
        @Part("lon")lon:RequestBody?,
        @Header
        ("Authorization") token: String
    ): Call<UploadStoryResponse>
}
