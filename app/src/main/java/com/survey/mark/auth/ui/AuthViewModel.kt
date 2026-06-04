package com.survey.mark.auth.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.survey.mark.auth.domain.AuthRepository
import com.survey.mark.auth.domain.AuthState
import com.survey.mark.auth.domain.SignUpRequest
import com.survey.mark.auth.ui.login.LoginState
import com.survey.mark.auth.ui.register.SignUpState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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


    fun setLoginEmail(s: String) = _login.update { it.copy(email = s, errorMessage = null) }
    fun setLoginPassword(s: String) = _login.update { it.copy(password = s, errorMessage = null) }
    fun clearLoginError() = _login.update { it.copy(errorMessage = null) }

    fun setDisplayName(s: String) = _signUp.update { it.copy(displayName = s, errorMessage = null) }
    fun setSignUpEmail(s: String) = _signUp.update { it.copy(email = s, errorMessage = null) }
    fun setSignUpPassword(s: String) = _signUp.update { it.copy(password = s, errorMessage = null) }
    fun setConfirmPassword(s: String) =
        _signUp.update { it.copy(confirmPwd = s, errorMessage = null) }

    fun setLicenceNo(s: String) = _signUp.update { it.copy(licenceNo = s, errorMessage = null) }
    fun setRegion(s: String) = _signUp.update { it.copy(region = s, errorMessage = null) }
    fun clearSignUpError() = _signUp.update { it.copy(errorMessage = null) }

    fun login() {
        val s = _login.value
        if (s.email.isBlank()) {
            _login.update { it.copy(errorMessage = "Email is required") }
            return
        }
        if (s.password.isBlank()) {
            _login.update { it.copy(errorMessage = "Password is required") }
            return
        }

        viewModelScope.launch {
            _login.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.signIn(s.email.trim(), s.password)
                .onSuccess {
                    Timber.d("Firebase Auth succeeded — waiting for authState to resolve")
                    _login.update { it.copy(isLoading = false) }
                }
                .onFailure { e ->
                    Timber.e(e, "Login failed")
                    _login.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = friendlyError(e.message)
                        )
                    }
                }
        }
    }

    fun signUp() {
        val s = _signUp.value

        if (s.displayName.isBlank()) {
            _signUp.update { it.copy(errorMessage = "Full name is required") }; return
        }
        if (s.email.isBlank()) {
            _signUp.update { it.copy(errorMessage = "Email is required") }; return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) {
            _signUp.update { it.copy(errorMessage = "Enter a valid email address") }; return
        }
        if (s.password.length < 8) {
            _signUp.update { it.copy(errorMessage = "Password must be at least 8 characters") }; return
        }
        if (s.password != s.confirmPwd) {
            _signUp.update { it.copy(errorMessage = "Passwords do not match") }; return
        }
        if (s.licenceNo.isBlank()) {
            _signUp.update { it.copy(errorMessage = "Licence number is required") }; return
        }
        if (s.region.isBlank()) {
            _signUp.update { it.copy(errorMessage = "Region is required") }; return
        }

        viewModelScope.launch {
            _signUp.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.signUp(
                SignUpRequest(
                    email = s.email.trim(),
                    password = s.password,
                    displayName = s.displayName.trim(),
                    licenceNo = s.licenceNo.trim().uppercase(),
                    region = s.region.trim()
                )
            )
                .onSuccess {
                    _signUp.update { it.copy(isLoading = false, registrationSuccess = true) }
                }
                .onFailure { e ->
                    Timber.e(e, "Sign up failed")
                    _signUp.update {
                        it.copy(isLoading = false, errorMessage = friendlyError(e.message))
                    }
                }
        }
    }


    fun signOut() {
        viewModelScope.launch { authRepository.signOut() }
    }


    private fun friendlyError(raw: String?): String = when {
        raw == null -> "An unexpected error occurred"
        raw.contains("email address is already") -> "An account with this email already exists"
        raw.contains("password is invalid") -> "Incorrect password"
        raw.contains("no user record") -> "No account found with this email"
        raw.contains("network error") -> "No internet connection"
        raw.contains("too many requests") -> "Too many attempts — try again later"
        raw.contains("user has been disabled") -> "This account has been suspended"
        raw.contains("PERMISSION_DENIED") -> "Access denied — check your licence number with the Surveyor General"
        else -> raw
    }
}