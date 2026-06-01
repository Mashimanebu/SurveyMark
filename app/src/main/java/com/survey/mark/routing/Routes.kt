package com.survey.mark.routing


object Routes {
    const val DIRECTORY = "directory"
    const val DETAIL = "detail/{controlPointId}"
    const val FIELD_NAV = "field_nav/{controlPointId}"
    const val REPORT = "report/{controlPointId}"
    const val LOG = "log/{controlPointId}"
    const val NEW_MARK = "new_mark"

    fun detail(id: String) = "detail/$id"
    fun fieldNav(id: String) = "field_nav/$id"
    fun report(id: String) = "report/$id"
    fun log(id: String) = "log/$id"
}


