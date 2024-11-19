package com.example.dicostory.data.pref

data class UserModel(
    val id: String,
    val name: String,
    val token: String,
    val isLogin: Boolean = false
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)


