package com.example.hubwifiv2.utils.auth.logic

data class SignInResult(
    val data: UserData?,
    val errorMessage: String?
)

data class UserData(
    val userId: String,
    val userName: String?,
    val profilePicURL: String?
)
