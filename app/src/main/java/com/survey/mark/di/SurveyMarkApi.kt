package com.survey.mark.di

import com.survey.mark.data.remote.ApiResponse
import com.survey.mark.data.remote.ConditionReportRequestDto
import com.survey.mark.data.remote.ControlPointDto
import com.survey.mark.data.remote.NewMarkRequestDto
import com.survey.mark.data.remote.OccupationLogRequestDto
import com.survey.mark.data.remote.PagedResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response
import retrofit2.http.Query

interface SurveyMarkApi {

    @GET("control-points")
    suspend fun getControlPoints(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 500,
        @Query("updated_since") updatedSince: String? = null
    ): Response<PagedResponse<ControlPointDto>>

    @GET("control-points/{id}")
    suspend fun getControlPoint(
        @Path("id") id: String
    ): Response<ApiResponse<ControlPointDto>>

    @POST("condition-reports")
    suspend fun submitConditionReport(
        @Body report: ConditionReportRequestDto
    ): Response<ApiResponse<Unit>>

    @POST("occupation-logs")
    suspend fun submitOccupationLog(
        @Body log: OccupationLogRequestDto
    ): Response<ApiResponse<Unit>>

    @POST("new-marks")
    suspend fun submitNewMark(
        @Body mark: NewMarkRequestDto
    ): Response<ApiResponse<Unit>>

    @GET("health")
    suspend fun healthCheck(): Response<Unit>
}