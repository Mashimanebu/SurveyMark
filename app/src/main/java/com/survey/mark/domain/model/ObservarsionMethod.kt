package com.survey.mark.domain.model
enum class ObservationMethod(val label: String) {
    GNSS_STATIC_4H("GNSS Static (≥4 hrs)"),
    GNSS_STATIC_12H("GNSS Static (≥12 hrs)"),
    GNSS_RTK_MULTI("GNSS RTK (multiple sessions)"),
    DIFFERENTIAL_LEVELLING("Differential Levelling"),
    TRIG_HEIGHTING("Trigonometric Heighting"),
    TOTAL_STATION("Total Station Traverse")
}
