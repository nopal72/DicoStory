package com.example.dicostory.data.api

data class Response(
	val loginResult: LoginResult,
	val error: Boolean,
	val message: String
)

data class LoginResult(
	val name: String,
	val userId: String,
	val token: String
)

