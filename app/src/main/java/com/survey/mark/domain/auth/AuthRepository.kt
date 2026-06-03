package com.survey.mark.domain.auth

interface AuthRepository {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<Unit>

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun logout()
}