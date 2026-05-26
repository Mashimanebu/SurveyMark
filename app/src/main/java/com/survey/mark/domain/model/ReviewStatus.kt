package com.survey.mark.domain.model
enum class ReviewStatus(val label: String) {
    PENDING("Pending Review"),
    UNDER_REVIEW("Under Review"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    GAZETTED("Gazetted")
}
