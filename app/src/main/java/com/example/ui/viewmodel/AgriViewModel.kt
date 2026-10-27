package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.CropScan
import com.example.data.model.Farm
import com.example.data.model.Field
import com.example.data.model.TaskReminder
import com.example.data.model.UserProfile
import com.example.data.model.WeatherAdvisory
import com.example.data.repository.AgriRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "AgriViewModel"

sealed interface AnalysisUiState {
    data object Idle : AnalysisUiState
    data class Analyzing(val step: String) : AnalysisUiState
    data class Success(val scan: CropScan) : AnalysisUiState
    data class Error(val message: String) : AnalysisUiState
}

sealed interface ChatUiState {
    data object Idle : ChatUiState
    data class Streaming(val text: String) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

class AgriViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AgriRepository(application)

    val weatherAdvisory: WeatherAdvisory = repository.getWeatherAdvisory()

    private val _userProfile = MutableStateFlow(repository.getUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _selectedScan = MutableStateFlow<CropScan?>(null)
    val selectedScan: StateFlow<CropScan?> = _selectedScan.asStateFlow()

    private val _analysisState = MutableStateFlow<AnalysisUiState>(AnalysisUiState.Idle)
    val analysisState: StateFlow<AnalysisUiState> = _analysisState.asStateFlow()

    private val _chatUiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
    val chatUiState: StateFlow<ChatUiState> = _chatUiState.asStateFlow()

    // Filters and search for History Screen
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("All")

    // Scans flow
    val allScans: StateFlow<List<CropScan>> = repository.getAllScans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Scans for History Screen
    val filteredScans: StateFlow<List<CropScan>> = combine(
        allScans,
        searchQuery,
        selectedCategoryFilter
    ) { scans, query, filter ->
        scans.filter { scan ->
            val matchesQuery = query.isBlank() ||
                    scan.cropName.contains(query, ignoreCase = true) ||
                    scan.diagnosisName.contains(query, ignoreCase = true) ||
                    scan.scientificName.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                "All" -> true
                "Healthy" -> scan.category == "Healthy"
                "Disease Suspected" -> scan.category in listOf("Fungal", "Bacterial", "Viral")
                "Pest Suspected" -> scan.category == "Pest"
                "Follow-up" -> scan.isFollowUpNeeded
                else -> true
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Farms & Fields
    val allFarms: StateFlow<List<Farm>> = repository.getAllFarms()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFields: StateFlow<List<Field>> = repository.getAllFields()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reminders
    val allReminders: StateFlow<List<TaskReminder>> = repository.getAllReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Chat
    val chatMessages: StateFlow<List<ChatMessage>> = repository.getChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Comparison Mode
    private val _comparisonScanA = MutableStateFlow<CropScan?>(null)
    val comparisonScanA: StateFlow<CropScan?> = _comparisonScanA.asStateFlow()

    private val _comparisonScanB = MutableStateFlow<CropScan?>(null)
    val comparisonScanB: StateFlow<CropScan?> = _comparisonScanB.asStateFlow()

    fun selectScan(scan: CropScan) {
        _selectedScan.value = scan
    }

    fun selectScanById(id: String) {
        viewModelScope.launch {
            val scan = repository.getScanById(id)
            if (scan != null) {
                _selectedScan.value = scan
            }
        }
    }

    fun deleteScan(id: String) {
        viewModelScope.launch {
            repository.deleteScan(id)
            if (_selectedScan.value?.id == id) {
                _selectedScan.value = null
            }
        }
    }

    /**
     * Run full AI crop analysis on captured or selected crop photograph.
     */
    fun analyzeCrop(
        bitmap: Bitmap?,
        manualCropHint: String?,
        localImagePath: String = "",
        drawableName: String = "sample_crop_leaf",
        onComplete: (CropScan) -> Unit
    ) {
        viewModelScope.launch {
            _analysisState.value = AnalysisUiState.Analyzing("Preprocessing leaf texture & lighting...")
            kotlinx.coroutines.delay(600)
            _analysisState.value = AnalysisUiState.Analyzing("Scanning for foliar pathogens & lesions...")
            kotlinx.coroutines.delay(700)
            _analysisState.value = AnalysisUiState.Analyzing("Querying agricultural research compendium (ICAR/FAO)...")

            try {
                val result = repository.analyzeCrop(bitmap, manualCropHint, localImagePath, drawableName)
                _selectedScan.value = result
                _analysisState.value = AnalysisUiState.Success(result)
                onComplete(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Crop analysis error", e)
                _analysisState.value = AnalysisUiState.Error(e.localizedMessage ?: "Analysis failed")
            }
        }
    }

    fun resetAnalysisState() {
        _analysisState.value = AnalysisUiState.Idle
    }

    /**
     * Post a question to AgriScan AI with multi-turn streaming.
     */
    fun sendChatMessage(text: String) {
        if (text.isBlank()) return

        val scanCtx = _selectedScan.value
        viewModelScope.launch {
            repository.postUserMessage(text, scanCtx)
            _chatUiState.value = ChatUiState.Streaming("AgriScan AI is consulting agricultural guides...")

            val accumulated = StringBuilder()
            try {
                repository.streamAiResponse(text, scanCtx).collect { chunk ->
                    accumulated.append(chunk)
                    _chatUiState.value = ChatUiState.Streaming(accumulated.toString())
                }
                val finalAnswer = accumulated.toString()
                if (finalAnswer.isNotBlank()) {
                    repository.saveAiResponse(finalAnswer, scanCtx)
                    _chatUiState.value = ChatUiState.Idle
                } else {
                    _chatUiState.value = ChatUiState.Error("AI returned empty response")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Chat error", e)
                _chatUiState.value = ChatUiState.Error(e.localizedMessage ?: "Chat failed")
            }
        }
    }

    fun addFarm(name: String, location: String, sizeAcres: Double, soilType: String, irrigation: String, crops: List<String>) {
        viewModelScope.launch {
            val farm = Farm(
                id = UUID.randomUUID().toString(),
                name = name,
                location = location,
                fieldSizeAcres = sizeAcres,
                soilType = soilType,
                irrigationMethod = irrigation,
                activeCrops = crops
            )
            repository.addFarm(farm)
        }
    }

    fun addField(farmId: String, name: String, crop: String, variety: String, stage: String, acreage: Double) {
        viewModelScope.launch {
            val field = Field(
                id = UUID.randomUUID().toString(),
                farmId = farmId,
                fieldName = name,
                cropName = crop,
                variety = variety,
                growthStage = stage,
                acreage = acreage
            )
            repository.addField(field)
        }
    }

    fun addReminder(title: String, description: String, cropName: String, dueDate: String, type: String) {
        viewModelScope.launch {
            val reminder = TaskReminder(
                id = UUID.randomUUID().toString(),
                title = title,
                description = description,
                cropName = cropName,
                dueDate = dueDate,
                dueTimestamp = System.currentTimeMillis() + 86400000,
                reminderType = type
            )
            repository.addReminder(reminder)
        }
    }

    fun toggleReminder(id: String, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleReminder(id, completed)
        }
    }

    fun deleteReminder(reminder: TaskReminder) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
        }
    }

    fun updateLanguage(langCode: String) {
        val updated = _userProfile.value.copy(selectedLanguage = langCode)
        _userProfile.value = updated
        repository.updateUserProfile(updated)
    }

    fun setComparisonScans(a: CropScan?, b: CropScan?) {
        _comparisonScanA.value = a
        _comparisonScanB.value = b
    }
}
