package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String = "default_agri_chat",
    val role: String = "user", // "user", "model", "system"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val citations: List<EvidenceSource> = emptyList(),
    val attachedScanId: String? = null,
    val attachedCropName: String? = null
)
