package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TaskReminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY dueTimestamp ASC")
    fun getAllReminders(): Flow<List<TaskReminder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: TaskReminder)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminders(reminders: List<TaskReminder>)

    @Update
    suspend fun updateReminder(reminder: TaskReminder)

    @Delete
    suspend fun deleteReminder(reminder: TaskReminder)

    @Query("UPDATE reminders SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleCompleted(id: String, completed: Boolean)
}
