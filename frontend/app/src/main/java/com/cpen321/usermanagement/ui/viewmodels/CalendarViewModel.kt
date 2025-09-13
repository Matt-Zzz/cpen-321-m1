package com.cpen321.usermanagement.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cpen321.usermanagement.data.remote.dto.CalendarEvent
import com.cpen321.usermanagement.data.remote.dto.CreateMilestoneRequest
import com.cpen321.usermanagement.data.remote.dto.CreateTaskRequest
import com.cpen321.usermanagement.data.repository.CalendarRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class CalendarUiState(
    val isLoadingMilestones: Boolean = false,
    val isLoadingSchedule: Boolean = false,
    val milestones: List<CalendarEvent> = emptyList(),
    val todaysEvents: List<CalendarEvent> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    companion object {
        private const val TAG = "CalendarViewModel"
    }

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadCalendarData()
    }

    fun loadCalendarData() {
        loadUpcomingMilestones()
        loadTodaysSchedule()
    }

    fun loadUpcomingMilestones() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingMilestones = true,
                errorMessage = null
            )

            val result = calendarRepository.getUpcomingMilestones()
            if (result.isSuccess) {
                val milestones = result.getOrNull() ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoadingMilestones = false,
                    milestones = milestones,
                    successMessage = "Milestones loaded successfully"
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Failed to load upcoming milestones", error)
                val errorMessage = error?.message ?: "Failed to load upcoming milestones"
                _uiState.value = _uiState.value.copy(
                    isLoadingMilestones = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun loadTodaysSchedule() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingSchedule = true,
                errorMessage = null
            )

            val result = calendarRepository.getTodaysSchedule()
            if (result.isSuccess) {
                val events = result.getOrNull() ?: emptyList()
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    todaysEvents = events,
                    successMessage = "Today's schedule loaded successfully"
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Failed to load today's schedule", error)
                val errorMessage = error?.message ?: "Failed to load today's schedule"
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun createMilestone(request: CreateMilestoneRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingMilestones = true,
                errorMessage = null
            )

            val result = calendarRepository.createMilestone(request)
            if (result.isSuccess) {
                // Reload milestones to show the new one
                loadUpcomingMilestones()
                _uiState.value = _uiState.value.copy(
                    successMessage = "Milestone created successfully!"
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Failed to create milestone", error)
                val errorMessage = error?.message ?: "Failed to create milestone"
                _uiState.value = _uiState.value.copy(
                    isLoadingMilestones = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun createTask(request: CreateTaskRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingSchedule = true,
                errorMessage = null
            )

            val result = calendarRepository.createTask(request)
            if (result.isSuccess) {
                // Reload today's schedule to show the new task
                loadTodaysSchedule()
                _uiState.value = _uiState.value.copy(
                    successMessage = "Task created successfully!"
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Failed to create task", error)
                val errorMessage = error?.message ?: "Failed to create task"
                _uiState.value = _uiState.value.copy(
                    isLoadingSchedule = false,
                    errorMessage = errorMessage
                )
            }
        }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                errorMessage = null
            )

            val result = calendarRepository.deleteEvent(eventId)
            if (result.isSuccess) {
                // Reload both milestones and schedule to reflect the deletion
                loadUpcomingMilestones()
                loadTodaysSchedule()
                _uiState.value = _uiState.value.copy(
                    successMessage = "Event deleted successfully!"
                )
            } else {
                val error = result.exceptionOrNull()
                Log.e(TAG, "Failed to delete event", error)
                val errorMessage = error?.message ?: "Failed to delete event"
                _uiState.value = _uiState.value.copy(
                    errorMessage = errorMessage
                )
            }
        }
    }

    // Utility functions for formatting dates
    fun formatEventTime(event: CalendarEvent): String {
        return try {
            val startTime = event.start.dateTime ?: event.start.date
            if (startTime != null) {
                val inputFormat = if (event.start.dateTime != null) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                } else {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                }
                val outputFormat = if (event.start.dateTime != null) {
                    SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                } else {
                    SimpleDateFormat("MMM dd", Locale.getDefault())
                }
                
                val date = inputFormat.parse(startTime)
                outputFormat.format(date ?: Date())
            } else {
                "No time specified"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error formatting event time", e)
            "Invalid time"
        }
    }

    fun isEventToday(event: CalendarEvent): Boolean {
        return try {
            val eventDate = event.start.dateTime ?: event.start.date
            if (eventDate != null) {
                val inputFormat = if (event.start.dateTime != null) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                } else {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                }
                
                val eventDateObj = inputFormat.parse(eventDate)
                val today = Date()
                
                val eventCalendar = java.util.Calendar.getInstance()
                val todayCalendar = java.util.Calendar.getInstance()
                
                eventDateObj?.let { eventCalendar.time = it }
                todayCalendar.time = today
                
                eventCalendar.get(java.util.Calendar.DAY_OF_YEAR) == todayCalendar.get(java.util.Calendar.DAY_OF_YEAR) &&
                eventCalendar.get(java.util.Calendar.YEAR) == todayCalendar.get(java.util.Calendar.YEAR)
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if event is today", e)
            false
        }
    }
}
