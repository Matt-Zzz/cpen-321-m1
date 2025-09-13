package com.cpen321.usermanagement.data.repository

import android.content.Context
import android.util.Log
import com.cpen321.usermanagement.data.local.preferences.TokenManager
import com.cpen321.usermanagement.data.remote.api.CalendarInterface
import com.cpen321.usermanagement.data.remote.dto.CalendarEvent
import com.cpen321.usermanagement.data.remote.dto.CreateMilestoneRequest
import com.cpen321.usermanagement.data.remote.dto.CreateTaskRequest
import com.cpen321.usermanagement.utils.JsonUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val calendarInterface: CalendarInterface,
    private val tokenManager: TokenManager
) : CalendarRepository {

    companion object {
        private const val TAG = "CalendarRepositoryImpl"
    }

    override suspend fun getUpcomingMilestones(): Result<List<CalendarEvent>> {
        return try {
            val response = calendarInterface.getUpcomingMilestones("") // Auth header is handled by interceptor
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = JsonUtils.parseErrorMessage(
                    errorBodyString,
                    response.body()?.message ?: "Failed to get upcoming milestones."
                )
                Log.e(TAG, "Failed to get upcoming milestones: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network timeout while getting upcoming milestones", e)
            Result.failure(e)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network connection failed while getting upcoming milestones", e)
            Result.failure(e)
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error while getting upcoming milestones", e)
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP error while getting upcoming milestones: ${e.code()}", e)
            Result.failure(e)
        }
    }

    override suspend fun getTodaysSchedule(): Result<List<CalendarEvent>> {
        return try {
            val response = calendarInterface.getTodaysSchedule("") // Auth header is handled by interceptor
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = JsonUtils.parseErrorMessage(
                    errorBodyString,
                    response.body()?.message ?: "Failed to get today's schedule."
                )
                Log.e(TAG, "Failed to get today's schedule: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network timeout while getting today's schedule", e)
            Result.failure(e)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network connection failed while getting today's schedule", e)
            Result.failure(e)
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error while getting today's schedule", e)
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP error while getting today's schedule: ${e.code()}", e)
            Result.failure(e)
        }
    }

    override suspend fun getCalendarAuthUrl(): Result<String> {
        return try {
            val response = calendarInterface.getAuthUrl("") // Auth header is handled by interceptor
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = JsonUtils.parseErrorMessage(
                    errorBodyString,
                    response.body()?.message ?: "Failed to get calendar auth URL."
                )
                Log.e(TAG, "Failed to get calendar auth URL: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network timeout while getting calendar auth URL", e)
            Result.failure(e)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network connection failed while getting calendar auth URL", e)
            Result.failure(e)
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error while getting calendar auth URL", e)
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP error while getting calendar auth URL: ${e.code()}", e)
            Result.failure(e)
        }
    }

    override suspend fun createMilestone(request: CreateMilestoneRequest): Result<CalendarEvent> {
        return try {
            val response = calendarInterface.createMilestone("", request) // Auth header is handled by interceptor
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.event)
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = JsonUtils.parseErrorMessage(
                    errorBodyString,
                    response.body()?.message ?: "Failed to create milestone."
                )
                Log.e(TAG, "Failed to create milestone: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network timeout while creating milestone", e)
            Result.failure(e)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network connection failed while creating milestone", e)
            Result.failure(e)
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error while creating milestone", e)
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP error while creating milestone: ${e.code()}", e)
            Result.failure(e)
        }
    }

    override suspend fun createTask(request: CreateTaskRequest): Result<CalendarEvent> {
        return try {
            val response = calendarInterface.createTask("", request) // Auth header is handled by interceptor
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!.event)
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = JsonUtils.parseErrorMessage(
                    errorBodyString,
                    response.body()?.message ?: "Failed to create task."
                )
                Log.e(TAG, "Failed to create task: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network timeout while creating task", e)
            Result.failure(e)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network connection failed while creating task", e)
            Result.failure(e)
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error while creating task", e)
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP error while creating task: ${e.code()}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteEvent(eventId: String): Result<Unit> {
        return try {
            val response = calendarInterface.deleteEvent("", eventId) // Auth header is handled by interceptor
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBodyString = response.errorBody()?.string()
                val errorMessage = JsonUtils.parseErrorMessage(
                    errorBodyString,
                    response.body()?.message ?: "Failed to delete event."
                )
                Log.e(TAG, "Failed to delete event: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Network timeout while deleting event", e)
            Result.failure(e)
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Network connection failed while deleting event", e)
            Result.failure(e)
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error while deleting event", e)
            Result.failure(e)
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP error while deleting event: ${e.code()}", e)
            Result.failure(e)
        }
    }
}
