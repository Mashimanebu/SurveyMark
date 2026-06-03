package com.survey.mark.domain.auth

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
