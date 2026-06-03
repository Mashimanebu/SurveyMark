package com.survey.mark.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val authState: Flow<AuthState>

    val currentUser: SurveyUser?

    suspend fun signIn(
        email: String, password: String
    ): Result<SurveyUser>

    suspend fun signUp(
        request: SignUpRequest
    ): Result<SurveyUser>

    suspend fun signOut()

    suspend fun getCurrentUser(): SurveyUser?


    suspend fun getPendingUsers(): Result<List<SurveyUser>>

    suspend fun getAllUsers(): Result<List<SurveyUser>>

    suspend fun approveUser(
        uid: String
    ): Result<Unit>

    suspend fun rejectUser(
        uid: String, reason: String
    ): Result<Unit>

    suspend fun suspendUser(
        uid: String, reason: String
    ): Result<Unit>

    suspend fun updateUserRole(
        uid: String, role: UserRole
    ): Result<Unit>
}