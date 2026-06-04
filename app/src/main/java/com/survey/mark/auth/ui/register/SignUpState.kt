package com.survey.mark.auth.ui.register

data class SignUpState(
    val displayName         : String  = "",
    val email               : String  = "",
    val password            : String  = "",
    val confirmPwd          : String  = "",
    val licenceNo           : String  = "",
    val region              : String  = "",
    val isLoading           : Boolean = false,
    val errorMessage        : String? = null,
    val registrationSuccess : Boolean = false
)