package com.survey.mark.routing


object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val PENDING = "pending"
    const val ADMIN = "admin_dashboard"
    const val DIRECTORY = "directory"
    const val NEW_MARK = "new_mark"
    const val DETAIL = "detail/{controlPointId}"
    const val REPORT = "report/{controlPointId}"
    const val LOG = "log/{controlPointId}"
    const val FIELD_NAV = "field_nav/{controlPointId}"

    fun detail(id: String) = "detail/$id"
    fun report(id: String) = "report/$id"
    fun log(id: String) = "log/$id"
    fun fieldNav(id: String) = "field_nav/$id"
}


