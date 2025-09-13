package com.cpen321.usermanagement.data.remote.api

import com.cpen321.usermanagement.data.remote.dto.ApiResponse
import com.cpen321.usermanagement.data.remote.dto.CalendarAuthUrlResponse
import com.cpen321.usermanagement.data.remote.dto.CalendarEvent
import com.cpen321.usermanagement.data.remote.dto.CreateEventResponse
import com.cpen321.usermanagement.data.remote.dto.CreateMilestoneRequest
import com.cpen321.usermanagement.data.remote.dto.CreateTaskRequest
import com.cpen321.usermanagement.data.remote.dto.DeleteEventResponse
import com.cpen321.usermanagement.data.remote.dto.TodaysScheduleResponse
import com.cpen321.usermanagement.data.remote.dto.UpcomingMilestonesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CalendarInterface {
    @GET("calendar/milestones")
    suspend fun getUpcomingMilestones(
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<List<CalendarEvent>>>

    @GET("calendar/schedule")
    suspend fun getTodaysSchedule(
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<List<CalendarEvent>>>

    @GET("calendar/auth-url")
    suspend fun getAuthUrl(
        @Header("Authorization") authHeader: String
    ): Response<ApiResponse<String>>

    @POST("calendar/milestones")
    suspend fun createMilestone(
        @Header("Authorization") authHeader: String,
        @Body request: CreateMilestoneRequest
    ): Response<CreateEventResponse>

    @POST("calendar/tasks")
    suspend fun createTask(
        @Header("Authorization") authHeader: String,
        @Body request: CreateTaskRequest
    ): Response<CreateEventResponse>

    @DELETE("calendar/events/{eventId}")
    suspend fun deleteEvent(
        @Header("Authorization") authHeader: String,
        @Path("eventId") eventId: String
    ): Response<DeleteEventResponse>
}
