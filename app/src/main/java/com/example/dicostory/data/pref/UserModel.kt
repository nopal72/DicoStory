package com.example.dicostory.data.pref

object User {

    data class UserModel(
        val email: String,
        val token: String,
        val isLogin: Boolean = false
    )

    data class RegisterRequest(
        val name: String,
        val email: String,
        val password: String
    )

}
