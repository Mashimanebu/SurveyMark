package com.survey.mark.auth.domain

data class SignUpRequest(
    val email: String,
    val password: String,
    val displayName: String,
    val licenceNo: String,
    val region: String
)
