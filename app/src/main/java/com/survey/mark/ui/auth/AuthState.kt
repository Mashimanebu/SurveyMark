package com.survey.mark.ui.auth

import android.util.Patterns

data class AuthState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)
fun validateRegister(
    name: String,
    email: String,
    password: String,
    confirmPassword: String
): String? {

    if (name.isBlank()) return "Name is required"

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return "Invalid email"

    if (password.length < 8)
        return "Password must be at least 8 characters"

    if (password != confirmPassword)
        return "Passwords do not match"

    return null
}