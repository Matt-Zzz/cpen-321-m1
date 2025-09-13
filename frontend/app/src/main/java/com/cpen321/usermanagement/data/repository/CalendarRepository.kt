package com.cpen321.usermanagement.data.repository

import com.cpen321.usermanagement.data.remote.dto.CalendarEvent
import com.cpen321.usermanagement.data.remote.dto.CreateMilestoneRequest
import com.cpen321.usermanagement.data.remote.dto.CreateTaskRequest

interface CalendarRepository {
    suspend fun getUpcomingMilestones(): Result<List<CalendarEvent>>
    suspend fun getTodaysSchedule(): Result<List<CalendarEvent>>
    suspend fun getCalendarAuthUrl(): Result<String>
    suspend fun createMilestone(request: CreateMilestoneRequest): Result<CalendarEvent>
    suspend fun createTask(request: CreateTaskRequest): Result<CalendarEvent>
    suspend fun deleteEvent(eventId: String): Result<Unit>
}
