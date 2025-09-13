package com.cpen321.usermanagement.data.remote.dto

import com.google.gson.annotations.SerializedName

// Calendar Event Data Transfer Objects
// ------------------------------------------------------------

data class CalendarEvent(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("summary")
    val summary: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("start")
    val start: EventTime,
    
    @SerializedName("end")
    val end: EventTime,
    
    @SerializedName("location")
    val location: String? = null,
    
    @SerializedName("htmlLink")
    val htmlLink: String? = null,
    
    @SerializedName("created")
    val created: String,
    
    @SerializedName("updated")
    val updated: String
)

data class EventTime(
    @SerializedName("dateTime")
    val dateTime: String? = null,
    
    @SerializedName("date")
    val date: String? = null
)

// Response Data Transfer Objects
// ------------------------------------------------------------

data class UpcomingMilestonesResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: MilestonesData? = null
)

data class MilestonesData(
    @SerializedName("milestones")
    val milestones: List<CalendarEvent>
)

data class TodaysScheduleResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: ScheduleData? = null
)

data class ScheduleData(
    @SerializedName("events")
    val events: List<CalendarEvent>
)

data class CalendarAuthUrlResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: AuthUrlData? = null
)

data class AuthUrlData(
    @SerializedName("authUrl")
    val authUrl: String
)

// Create Milestone/Task Request DTOs
data class CreateMilestoneRequest(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("dueDate")
    val dueDate: String,
    
    @SerializedName("dueTime")
    val dueTime: String? = null,
    
    @SerializedName("isAllDay")
    val isAllDay: Boolean? = false
)

data class CreateTaskRequest(
    @SerializedName("title")
    val title: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    @SerializedName("dueDate")
    val dueDate: String,
    
    @SerializedName("dueTime")
    val dueTime: String? = null,
    
    @SerializedName("isAllDay")
    val isAllDay: Boolean? = false
)

// Create Event Response DTOs
data class CreateEventResponse(
    @SerializedName("message")
    val message: String,
    
    @SerializedName("data")
    val data: CreateEventData? = null
)

data class CreateEventData(
    @SerializedName("event")
    val event: CalendarEvent
)

// Delete Event Response DTO
data class DeleteEventResponse(
    @SerializedName("message")
    val message: String
)
