package com.example.dicostory.data.pref

data class UserModel (
    val email: String,
    val token: String,
    val isLogin: Boolean = false

)