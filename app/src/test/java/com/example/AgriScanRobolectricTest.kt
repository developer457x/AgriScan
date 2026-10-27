package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.AgriScanAiService
import com.example.data.local.AgriScanDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.CropScan
import com.example.data.model.DetectionBox
import com.example.data.model.EvidenceSource
import com.example.data.model.Farm
import com.example.data.model.Field
import com.example.data.model.TaskReminder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AgriScanRobolectricTest {

    private lateinit var db: AgriScanDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AgriScanDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testInsertAndRetrieveCropScan() = runBlocking {
        val scan = CropScan(
            id = "test_scan_01",
            cropName = "Tomato",
            diagnosisName = "Early Blight",
            scientificName = "Alternaria solani",
            healthScore = 72,
            confidenceScore = 0.95f,
            category = "Fungal"
        )
        db.scanDao().insertScan(scan)

        val retrieved = db.scanDao().getScanById("test_scan_01")
        assertNotNull(retrieved)
        assertEquals("Tomato", retrieved?.cropName)
        assertEquals("Early Blight", retrieved?.diagnosisName)
        assertEquals(72, retrieved?.healthScore)
    }

    @Test
    fun testExpertDiagnosticEngineForTomato() {
        val aiService = AgriScanAiService()
        val diagnosis = aiService.generateExpertAgriculturalDiagnosis("Tomato")

        assertEquals("Tomato", diagnosis.cropName)
        assertEquals("Early Blight", diagnosis.diagnosisName)
        assertEquals("Alternaria solani", diagnosis.scientificName)
        assertTrue(diagnosis.healthScore in 0..100)
        assertTrue(diagnosis.immediateActions.isNotEmpty())
        assertTrue(diagnosis.nonChemicalControls.isNotEmpty())
        assertTrue(diagnosis.citations.isNotEmpty())
        assertTrue(diagnosis.boundingBoxes.isNotEmpty())
    }

    @Test
    fun testExpertDiagnosticEngineForRice() {
        val aiService = AgriScanAiService()
        val diagnosis = aiService.generateExpertAgriculturalDiagnosis("Paddy")

        assertEquals("Paddy (Rice)", diagnosis.cropName)
        assertEquals("Rice Blast", diagnosis.diagnosisName)
        assertEquals("Magnaporthe oryzae", diagnosis.scientificName)
        assertTrue(diagnosis.chemicalControls.any { it.contains("Tricyclazole") })
    }

    @Test
    fun testFarmAndFieldInsertion() = runBlocking {
        val farm = Farm(
            id = "farm_test",
            name = "Mysore Agro Estate",
            location = "Mysore",
            fieldSizeAcres = 5.0
        )
        db.farmDao().insertFarm(farm)

        val field = Field(
            id = "field_test",
            farmId = "farm_test",
            fieldName = "Plot 1",
            cropName = "Chilli",
            acreage = 2.0
        )
        db.farmDao().insertField(field)

        val fields = db.farmDao().getFieldsForFarm("farm_test").first()
        assertEquals(1, fields.size)
        assertEquals("Chilli", fields[0].cropName)
    }

    @Test
    fun testReminderToggleAndPersistence() = runBlocking {
        val reminder = TaskReminder(
            id = "rem_test",
            title = "Check lower leaf spots",
            description = "Inspect canopy after rain",
            cropName = "Tomato",
            dueDate = "Tomorrow",
            dueTimestamp = System.currentTimeMillis() + 86400000,
            isCompleted = false
        )
        db.reminderDao().insertReminder(reminder)

        db.reminderDao().toggleCompleted("rem_test", true)
        val all = db.reminderDao().getAllReminders().first()
        assertTrue(all.any { it.id == "rem_test" && it.isCompleted })
    }

    @Test
    fun testChatMessageStorage() = runBlocking {
        val msg = ChatMessage(
            id = "msg_01",
            conversationId = "default_agri_chat",
            role = "user",
            content = "How to treat powdery mildew in chilli?"
        )
        db.chatDao().insertMessage(msg)

        val list = db.chatDao().getMessages("default_agri_chat").first()
        assertEquals(1, list.size)
        assertEquals("user", list[0].role)
        assertEquals("How to treat powdery mildew in chilli?", list[0].content)
    }

    @Test
    fun testEvidenceCitationsIntegrity() {
        val source = EvidenceSource(
            id = "icar_ref",
            title = "Integrated Disease Management in Solanaceous Crops",
            organization = "ICAR - Indian Institute of Horticultural Research",
            url = "https://iihr.icar.gov.in",
            publicationDate = "2024",
            retrievalDate = "Current Advisory",
            summary = "Recommends Trichoderma viride and protective copper sprays."
        )
        assertTrue(source.url.startsWith("https://"))
        assertTrue(source.organization.contains("ICAR"))
    }
}
