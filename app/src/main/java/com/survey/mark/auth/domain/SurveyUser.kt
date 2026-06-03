package com.survey.mark.auth.domain

data class SurveyUser(
    val uid: String,
    val email: String,
    val displayName: String,
    val licenceNo: String,
    val role: UserRole,
    val status: AccountStatus,
    val region: String,
    val photoUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class UserRole(val label: String) {
    SURVEYOR("Licensed Surveyor"),
    SURVEYOR_GENERAL("Surveyor General")
}

enum class AccountStatus(val label: String) {
    PENDING("Pending Approval"),
    ACTIVE("Active"),
    SUSPENDED("Suspended"),
    REJECTED("Rejected")
}
