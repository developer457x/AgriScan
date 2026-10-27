package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reminders")
data class TaskReminder(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val cropName: String,
    val dueDate: String,
    val dueTimestamp: Long,
    val isCompleted: Boolean = false,
    val reminderType: String = "Monitoring" // Monitoring, Spray, Irrigation, SoilTest
)
