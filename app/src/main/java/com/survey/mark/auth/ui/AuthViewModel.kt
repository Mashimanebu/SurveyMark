package com.survey.mark.auth.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.auth.domain.AuthRepository
import com.survey.mark.auth.domain.AuthState
import com.survey.mark.auth.domain.SignUpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState = authRepository.authState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthState.Loading
        )

    private val _login = MutableStateFlow(LoginState())
    val loginForm = _login.asStateFlow()

    private val _signUp = MutableStateFlow(SignUpState())
    val signUpForm = _signUp.asStateFlow()

    fun setLoginEmail(email: String) {
        _login.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun setLoginPassword(password: String) {
        _login.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun clearLoginError() {
        _login.update {
            it.copy(errorMessage = null)
        }
    }

    fun setDisplayName(displayName: String) {
        _signUp.update {
            it.copy(
                displayName = displayName,
                errorMessage = null
            )
        }
    }

    fun setSignUpEmail(email: String) {
        _signUp.update {
            it.copy(
                email = email,
                errorMessage = null
            )
        }
    }

    fun setSignUpPassword(password: String) {
        _signUp.update {
            it.copy(
                password = password,
                errorMessage = null
            )
        }
    }

    fun setConfirmPassword(confirmPassword: String) {
        _signUp.update {
            it.copy(
                confirmPwd = confirmPassword,
                errorMessage = null
            )
        }
    }

    fun setLicenceNo(licenceNo: String) {
        _signUp.update {
            it.copy(
                licenceNo = licenceNo,
                errorMessage = null
            )
        }
    }

    fun setRegion(region: String) {
        _signUp.update {
            it.copy(
                region = region,
                errorMessage = null
            )
        }
    }

    fun clearSignUpError() {
        _signUp.update {
            it.copy(errorMessage = null)
        }
    }

    fun login() {
        val state = _login.value

        when {
            state.email.isBlank() -> {
                _login.update {
                    it.copy(errorMessage = "Email is required")
                }
                return
            }

            state.password.isBlank() -> {
                _login.update {
                    it.copy(errorMessage = "Password is required")
                }
                return
            }
        }

        viewModelScope.launch {

            _login.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            authRepository
                .signIn(
                    email = state.email.trim(),
                    password = state.password
                )
                .onSuccess {
                    _login.update {
                        it.copy(isLoading = false)
                    }
                }
                .onFailure { error ->
                    _login.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendlyAuthError(error.message)
                        )
                    }
                }
        }
    }

    fun signUp() {

        val state = _signUp.value

        when {
            state.displayName.isBlank() -> {
                _signUp.update {
                    it.copy(errorMessage = "Full name is required")
                }
                return
            }

            state.email.isBlank() -> {
                _signUp.update {
                    it.copy(errorMessage = "Email is required")
                }
                return
            }

            !Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> {
                _signUp.update {
                    it.copy(errorMessage = "Enter a valid email address")
                }
                return
            }

            state.password.length < 8 -> {
                _signUp.update {
                    it.copy(errorMessage = "Password must be at least 8 characters")
                }
                return
            }

            state.password != state.confirmPwd -> {
                _signUp.update {
                    it.copy(errorMessage = "Passwords do not match")
                }
                return
            }

            state.licenceNo.isBlank() -> {
                _signUp.update {
                    it.copy(errorMessage = "Licence number is required")
                }
                return
            }

            state.region.isBlank() -> {
                _signUp.update {
                    it.copy(errorMessage = "Region is required")
                }
                return
            }
        }

        viewModelScope.launch {

            _signUp.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            authRepository.signUp(
                SignUpRequest(
                    email = state.email.trim(),
                    password = state.password,
                    displayName = state.displayName.trim(),
                    licenceNo = state.licenceNo.trim().uppercase(),
                    region = state.region.trim()
                )
            )
                .onSuccess {
                    _signUp.update {
                        it.copy(
                            isLoading = false,
                            registrationSuccess = true
                        )
                    }
                }
                .onFailure { error ->
                    _signUp.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendlyAuthError(error.message)
                        )
                    }
                }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    private fun friendlyAuthError(raw: String?): String =
        when {
            raw == null ->
                "An unexpected error occurred"

            raw.contains(
                "email address is already",
                ignoreCase = true
            ) ->
                "An account with this email already exists"

            raw.contains(
                "password is invalid",
                ignoreCase = true
            ) ->
                "Incorrect password"

            raw.contains(
                "no user record",
                ignoreCase = true
            ) ->
                "No account found with this email"

            raw.contains(
                "network error",
                ignoreCase = true
            ) ->
                "No internet connection"

            raw.contains(
                "too many requests",
                ignoreCase = true
            ) ->
                "Too many attempts. Please try again later"

            raw.contains(
                "user has been disabled",
                ignoreCase = true
            ) ->
                "This account has been suspended"

            else -> raw
        }
}