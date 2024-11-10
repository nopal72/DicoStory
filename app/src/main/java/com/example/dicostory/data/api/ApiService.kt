package com.example.dicostory.data.api

import com.example.dicostory.data.pref.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("register")
    fun registerUser(
        @Body body: User.RegisterRequest
    ): Call<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun loginUser(
        @Body body: User.LoginRequest
    ): Call<LoginResponse>
}
