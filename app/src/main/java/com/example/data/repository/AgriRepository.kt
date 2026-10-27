package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.data.ai.AgriScanAiService
import com.example.data.local.AgriScanDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.CropScan
import com.example.data.model.Farm
import com.example.data.model.Field
import com.example.data.model.TaskReminder
import com.example.data.model.UserProfile
import com.example.data.model.WeatherAdvisory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

class AgriRepository(context: Context) {

    private val database = AgriScanDatabase.getDatabase(context)
    private val scanDao = database.scanDao()
    private val farmDao = database.farmDao()
    private val reminderDao = database.reminderDao()
    private val chatDao = database.chatDao()

    private val aiService = AgriScanAiService()

    private var currentUserProfile = UserProfile()

    // Weather advisory based on seasonal agricultural telemetry
    private val weatherAdvisory = WeatherAdvisory()

    fun getAllScans(): Flow<List<CropScan>> = scanDao.getAllScans()

    suspend fun getScanById(id: String): CropScan? = scanDao.getScanById(id)

    suspend fun saveScan(scan: CropScan) = scanDao.insertScan(scan)

    suspend fun deleteScan(id: String) = scanDao.deleteScanById(id)

    suspend fun analyzeCrop(
        bitmap: Bitmap?,
        cropHint: String?,
        localImagePath: String = "",
        drawableName: String = "sample_crop_leaf"
    ): CropScan {
        val scan = aiService.analyzeCropImage(bitmap, cropHint, localImagePath, drawableName)
        scanDao.insertScan(scan)
        return scan
    }

    fun getAllFarms(): Flow<List<Farm>> = farmDao.getAllFarms()

    suspend fun addFarm(farm: Farm) = farmDao.insertFarm(farm)

    fun getAllFields(): Flow<List<Field>> = farmDao.getAllFields()

    suspend fun addField(field: Field) = farmDao.insertField(field)

    fun getAllReminders(): Flow<List<TaskReminder>> = reminderDao.getAllReminders()

    suspend fun addReminder(reminder: TaskReminder) = reminderDao.insertReminder(reminder)

    suspend fun toggleReminder(id: String, completed: Boolean) = reminderDao.toggleCompleted(id, completed)

    suspend fun deleteReminder(reminder: TaskReminder) = reminderDao.deleteReminder(reminder)

    fun getChatMessages(conversationId: String = "default_agri_chat"): Flow<List<ChatMessage>> =
        chatDao.getMessages(conversationId)

    suspend fun postUserMessage(text: String, scanContext: CropScan? = null): ChatMessage {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = "default_agri_chat",
            role = "user",
            content = text,
            timestamp = System.currentTimeMillis(),
            attachedScanId = scanContext?.id,
            attachedCropName = scanContext?.cropName
        )
        chatDao.insertMessage(userMsg)
        return userMsg
    }

    suspend fun saveAiResponse(text: String, scanContext: CropScan? = null): ChatMessage {
        val citations = scanContext?.citations ?: emptyList()
        val aiMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            conversationId = "default_agri_chat",
            role = "model",
            content = text,
            timestamp = System.currentTimeMillis(),
            citations = citations,
            attachedScanId = scanContext?.id,
            attachedCropName = scanContext?.cropName
        )
        chatDao.insertMessage(aiMsg)
        return aiMsg
    }

    fun streamAiResponse(text: String, scanContext: CropScan? = null): Flow<String> {
        return aiService.streamChatResponse(text, scanContext)
    }

    fun getUserProfile(): UserProfile = currentUserProfile

    fun updateUserProfile(profile: UserProfile) {
        currentUserProfile = profile
    }

    fun getWeatherAdvisory(): WeatherAdvisory = weatherAdvisory
}
