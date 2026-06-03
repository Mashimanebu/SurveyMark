package com.survey.mark.auth.domain

sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()

    data class Authenticated(
        val user: SurveyUser
    ) : AuthState()

    data class PendingApproval(
        val user: SurveyUser
    ) : AuthState()

    data class Suspended(
        val user: SurveyUser
    ) : AuthState()

    data class Error(
        val message: String
    ) : AuthState()
}