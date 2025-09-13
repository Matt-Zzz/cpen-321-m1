package com.cpen321.usermanagement.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.cpen321.usermanagement.R
import com.cpen321.usermanagement.data.remote.dto.CalendarEvent
import com.cpen321.usermanagement.ui.theme.LocalSpacing

@Composable
fun UpcomingMilestonesCard(
    milestones: List<CalendarEvent>,
    isLoading: Boolean,
    onEventClick: (CalendarEvent) -> Unit = {},
    onAddMilestone: () -> Unit = {},
    onDeleteMilestone: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(LocalSpacing.current.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "📅 Next CPEN 321 Milestones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                FloatingActionButton(
                    onClick = onAddMilestone,
                    modifier = Modifier.size(32.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(LocalSpacing.current.medium))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else if (milestones.isEmpty()) {
                Text(
                    text = "No upcoming milestones found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            } else {
                milestones.take(3).forEach { milestone ->
                    MilestoneItem(
                        event = milestone,
                        onClick = { onEventClick(milestone) },
                        onDelete = { onDeleteMilestone(milestone.id) }
                    )
                    if (milestone != milestones.take(3).last()) {
                        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                    }
                }
            }
        }
    }
}

@Composable
fun TodaysScheduleCard(
    events: List<CalendarEvent>,
    isLoading: Boolean,
    onEventClick: (CalendarEvent) -> Unit = {},
    onAddTask: () -> Unit = {},
    onDeleteEvent: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(LocalSpacing.current.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "📋 Today's Schedule",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                FloatingActionButton(
                    onClick = onAddTask,
                    modifier = Modifier.size(32.dp),
                    containerColor = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "+",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(LocalSpacing.current.medium))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            } else if (events.isEmpty()) {
                Text(
                    text = "No events scheduled for today",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            } else {
                events.take(5).forEach { event ->
                    ScheduleItem(
                        event = event,
                        onClick = { onEventClick(event) },
                        onDelete = { onDeleteEvent(event.id) }
                    )
                    if (event != events.take(5).last()) {
                        Spacer(modifier = Modifier.height(LocalSpacing.current.small))
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneItem(
    event: CalendarEvent,
    onClick: () -> Unit,
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(LocalSpacing.current.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Priority indicator
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.width(LocalSpacing.current.small))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatEventTime(event),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Text(
                    text = "🗑️",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ScheduleItem(
    event: CalendarEvent,
    onClick: () -> Unit,
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(LocalSpacing.current.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⏰",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(LocalSpacing.current.small))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = event.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = formatEventTime(event),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Text(
                    text = "🗑️",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun formatEventTime(event: CalendarEvent): String {
    return try {
        val startTime = event.start.dateTime ?: event.start.date
        if (startTime != null) {
            if (event.start.dateTime != null) {
                // Has specific time
                val timePart = startTime.substring(11, 16) // Extract HH:MM
                val datePart = startTime.substring(0, 10) // Extract YYYY-MM-DD
                val today = java.time.LocalDate.now().toString()
                
                if (datePart == today) {
                    "Today at $timePart"
                } else {
                    "$datePart at $timePart"
                }
            } else {
                // All-day event
                val datePart = startTime.substring(0, 10)
                val today = java.time.LocalDate.now().toString()
                
                if (datePart == today) {
                    "Today (All day)"
                } else {
                    "$datePart (All day)"
                }
            }
        } else {
            "No time specified"
        }
    } catch (e: Exception) {
        "Invalid time"
    }
}
