package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.CropScan
import com.example.data.model.DetectionBox
import com.example.data.model.EvidenceSource
import com.example.data.model.Farm
import com.example.data.model.Field
import com.example.data.model.TaskReminder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CropScan::class,
        Farm::class,
        Field::class,
        TaskReminder::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(AgriTypeConverters::class)
abstract class AgriScanDatabase : RoomDatabase() {

    abstract fun scanDao(): ScanDao
    abstract fun farmDao(): FarmDao
    abstract fun reminderDao(): ReminderDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AgriScanDatabase? = null

        fun getDatabase(context: Context): AgriScanDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgriScanDatabase::class.java,
                    "agriscan_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate initial seed data
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    populateInitialData(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun populateInitialData(db: AgriScanDatabase) {
            // Seed 1: Realistic Early Blight Scan matching the showcase
            val scan1 = CropScan(
                id = "scan_demo_tomato_early_blight",
                cropName = "Tomato",
                cropVariety = "Roma Hybrid",
                plantPart = "Lower Leaf",
                diagnosisName = "Early Blight",
                scientificName = "Alternaria solani",
                category = "Fungal",
                healthScore = 68,
                confidenceScore = 0.94f,
                severity = "Moderate",
                factorsExplanation = "Concentric dark brown target rings with surrounding yellow halo on bottom leaf canopy. Moderate foliage impact (15% area). Stems and fruits currently uninfected.",
                visualSymptoms = listOf(
                    "Concentric 'bullseye' target rings on older foliage",
                    "Yellow chlorotic halo circumscribing lesions",
                    "Early wilting and leaf drop on lowest branch nodes"
                ),
                probableCauses = listOf(
                    "Fungal spores splashing upward from wet soil during rainfall",
                    "Overhead sprinkler irrigation extending leaf dampness beyond 8 hours",
                    "Dense plant canopy restricting airflow and sunlight penetration"
                ),
                favorableConditions = "Warm humid weather (24°C - 30°C) with persistent dew or rain splash.",
                immediateActions = listOf(
                    "Remove and destroy severely spotted lower leaves (do not compost)",
                    "Transition irrigation from overhead sprinkler to ground drip lines",
                    "Sanitize cutting tools with 70% isopropyl alcohol between plants"
                ),
                nonChemicalControls = listOf(
                    "Apply Trichoderma viride bio-fungicide @ 5g/L around root zone",
                    "Spray Neem seed kernel extract (NSKE 5%) or 10,000 ppm Neem oil",
                    "Lay dry straw mulch 5cm thick to prevent soil splash onto lower foliage",
                    "Prune suckers to improve air circulation across plant rows"
                ),
                chemicalControls = listOf(
                    "Mancozeb 75% WP @ 2.0g per Liter of water - preventative contact spray",
                    "Copper Oxychloride 50% WP @ 2.5g per Liter of water - broad spectrum fungicide",
                    "Azoxystrobin 23% SC @ 1.0ml per Liter of water for systemic protection"
                ),
                preventionTips = listOf(
                    "Maintain strict 3-year crop rotation without solanaceous crops (potato, brinjal, chilli)",
                    "Stake and trellising vines to elevate foliage 30cm above ground level",
                    "Choose certified pathogen-free seeds with F1 hybrid resistance"
                ),
                safetyPrecautions = "Mandatory PPE: Wear nitrile gloves, protective eye goggles, and an N95 respirator. Pre-Harvest Interval (PHI) is 5 days for Mancozeb. Spray during calm early morning hours to safeguard honeybees.",
                citations = listOf(
                    EvidenceSource(
                        id = "src_icar_01",
                        title = "Integrated Disease Management in Solanaceous Crops",
                        organization = "ICAR - Indian Institute of Horticultural Research (IIHR)",
                        url = "https://iihr.icar.gov.in/integrated-pest-and-disease-management-tomato",
                        publicationDate = "2024",
                        retrievalDate = "Current Advisory",
                        summary = "Prescribes Trichoderma viride seed/soil application, sanitation of bottom 30cm canopy, and Mancozeb/Copper Oxychloride protective alternation."
                    ),
                    EvidenceSource(
                        id = "src_fao_02",
                        title = "Tomato IPM Field Guide for Extension Officers",
                        organization = "Food and Agriculture Organization (FAO)",
                        url = "https://www.fao.org/plant-production-protection/ipm",
                        publicationDate = "2023",
                        retrievalDate = "Extension Protocol",
                        summary = "Emphasizes drip irrigation to shorten leaf wetness duration below critical fungal germination thresholds."
                    )
                ),
                boundingBoxes = listOf(
                    DetectionBox(
                        label = "Early Blight Lesion (Alternaria solani)",
                        confidence = 0.94f,
                        left = 0.44f,
                        top = 0.42f,
                        right = 0.56f,
                        bottom = 0.57f,
                        severity = "Moderate"
                    )
                ),
                localImagePath = "",
                thumbnailDrawableName = "sample_crop_leaf",
                timestamp = System.currentTimeMillis() - 7200000,
                notes = "Field inspection Plot A. Treatment scheduled with copper spray tomorrow morning.",
                isFollowUpNeeded = true,
                isSynced = true,
                farmId = "farm_main",
                treatmentApplied = "Pruned infected lower foliage"
            )

            // Seed 2: Healthy Rice/Paddy scan
            val scan2 = CropScan(
                id = "scan_demo_paddy_healthy",
                cropName = "Paddy (Rice)",
                cropVariety = "Sona Masoori",
                plantPart = "Panicle & Leaves",
                diagnosisName = "Healthy Crop",
                scientificName = "Oryza sativa",
                category = "Healthy",
                healthScore = 96,
                confidenceScore = 0.98f,
                severity = "Healthy",
                factorsExplanation = "Vigorous tillering with dark green erect leaves. No blast lesions, brown spot, or stem borer pinholes detected. Optimal vegetative vigor.",
                visualSymptoms = listOf(
                    "Uniform emerald green leaf blades without necrotic spotting",
                    "Erect stem structure with active tillering",
                    "Absence of sheath blight or leaf folder webbing"
                ),
                probableCauses = listOf(
                    "Balanced N-P-K nutrient availability",
                    "Regulated water depth (3-5 cm)",
                    "Beneficial predatory spider populations active"
                ),
                favorableConditions = "Current warm daytime sun (29°C) and controlled standing water level ideal for panicle initiation.",
                immediateActions = listOf(
                    "Continue maintaining 3-5cm intermittent water depth",
                    "Scout field borders weekly for brown planthopper (BPH) baseline"
                ),
                nonChemicalControls = listOf(
                    "Erect bird perches (40-50 per hectare) for natural caterpillar control",
                    "Use Azolla bio-fertilizer layer to suppress weeds and fix nitrogen"
                ),
                chemicalControls = emptyList(),
                preventionTips = listOf(
                    "Avoid excessive top-dressing with urea, which promotes blast susceptibility",
                    "Alternate wetting and drying (AWD) to strengthen root anchorage and reduce methane"
                ),
                safetyPrecautions = "No chemical intervention needed at this stage. Keep protective gear ready for seasonal advisories.",
                citations = listOf(
                    EvidenceSource(
                        id = "src_icar_paddy",
                        title = "Rice Production Technology & Crop Health Monitoring",
                        organization = "ICAR - National Rice Research Institute (NRRI)",
                        url = "https://crri.icar.gov.in/rice-technologies",
                        publicationDate = "2024",
                        retrievalDate = "Extension Advisory",
                        summary = "Confirms optimal tiller counts and recommends AWD water management for disease prevention."
                    )
                ),
                boundingBoxes = emptyList(),
                localImagePath = "",
                thumbnailDrawableName = "farm_hero_banner",
                timestamp = System.currentTimeMillis() - 86400000,
                notes = "Plot B. Tillering stage looking excellent.",
                isFollowUpNeeded = false,
                isSynced = true,
                farmId = "farm_main",
                treatmentApplied = ""
            )

            // Seed 3: Chilli Powdery Mildew
            val scan3 = CropScan(
                id = "scan_demo_chilli_mildew",
                cropName = "Chilli",
                cropVariety = "Guntur Sannam",
                plantPart = "Upper Leaf Surface",
                diagnosisName = "Powdery Mildew",
                scientificName = "Leveillula taurica",
                category = "Fungal",
                healthScore = 74,
                confidenceScore = 0.91f,
                severity = "Low",
                factorsExplanation = "Early white powdery fungal patches on leaf undersides with corresponding yellow blotches on top surface. Restricted to 8% canopy.",
                visualSymptoms = listOf(
                    "White powdery talc-like patches on underside of leaves",
                    "Chlorotic yellow patches on upper leaf surface corresponding to fungal colonies",
                    "Mild upward curling of young tender leaves"
                ),
                probableCauses = listOf(
                    "Dry warm days followed by cool humid nights",
                    "Air currents dispersing airborne conidia spores from nearby fields"
                ),
                favorableConditions = "Dry warm days (28°C - 32°C) followed by night relative humidity above 75%.",
                immediateActions = listOf(
                    "Spray Wettable Sulphur 80% WP early in the morning before sun heat peaks",
                    "Isolate infected plants along field boundary"
                ),
                nonChemicalControls = listOf(
                    "Spray diluted fresh milk solution (1:9 with water) in direct sunlight",
                    "Apply potassium bicarbonate (3g/L) to alter leaf surface pH against spores"
                ),
                chemicalControls = listOf(
                    "Wettable Sulphur 80% WP @ 2.5g per Liter of water",
                    "Hexaconazole 5% EC @ 1.0ml per Liter of water"
                ),
                preventionTips = listOf(
                    "Provide adequate potassium nutrition to fortify plant cell walls",
                    "Space chilli rows at 60x45cm to allow cross-ventilation"
                ),
                safetyPrecautions = "Do not apply sulphur when field temperature exceeds 32°C to prevent sulfur burn. Re-entry interval: 24 hours.",
                citations = listOf(
                    EvidenceSource(
                        id = "src_tnau_chilli",
                        title = "Crop Protection Compendium: Chilli Powdery Mildew",
                        organization = "Tamil Nadu Agricultural University (TNAU)",
                        url = "https://agritech.tnau.ac.in/crop_protection/chilli_diseases.html",
                        publicationDate = "2024",
                        retrievalDate = "Extension Protocol",
                        summary = "Recommends Wettable Sulphur or Hexaconazole rotation for Leveillula taurica management."
                    )
                ),
                boundingBoxes = emptyList(),
                localImagePath = "",
                thumbnailDrawableName = "sample_crop_leaf",
                timestamp = System.currentTimeMillis() - 172800000,
                notes = "Applied wettable sulphur spray on Oct 25.",
                isFollowUpNeeded = true,
                isSynced = true,
                farmId = "farm_main",
                treatmentApplied = "Wettable Sulphur spray applied"
            )

            db.scanDao().insertScans(listOf(scan1, scan2, scan3))

            // Seed Farms
            val farm = Farm(
                id = "farm_main",
                name = "Siddapura Agri Farm",
                location = "Kolar District, Karnataka",
                fieldSizeAcres = 4.5,
                soilType = "Red Sandy Loam",
                irrigationMethod = "Drip Irrigation",
                activeCrops = listOf("Tomato", "Paddy (Rice)", "Chilli")
            )
            db.farmDao().insertFarm(farm)

            val field1 = Field(
                farmId = "farm_main",
                fieldName = "Plot 1 - North Terrace",
                cropName = "Tomato",
                variety = "Roma Hybrid",
                plantingDate = "Aug 10, 2026",
                growthStage = "Early Fruiting",
                acreage = 1.8
            )
            val field2 = Field(
                farmId = "farm_main",
                fieldName = "Plot 2 - Canal Bed",
                cropName = "Paddy (Rice)",
                variety = "Sona Masoori",
                plantingDate = "Jul 25, 2026",
                growthStage = "Active Tillering",
                acreage = 2.0
            )
            db.farmDao().insertFields(listOf(field1, field2))

            // Seed Reminders
            val r1 = TaskReminder(
                id = "rem_01",
                title = "Recheck Tomato Lower Leaves",
                description = "Inspect Plot 1 after Mancozeb spray to verify lesion dry-out and no new halos.",
                cropName = "Tomato",
                dueDate = "Tomorrow, 7:00 AM",
                dueTimestamp = System.currentTimeMillis() + 86400000,
                reminderType = "Monitoring"
            )
            val r2 = TaskReminder(
                id = "rem_02",
                title = "Alternate Wetting & Drying Check",
                description = "Measure water level in Paddy Plot 2 field tube before re-flooding.",
                cropName = "Paddy (Rice)",
                dueDate = "In 2 days, 9:00 AM",
                dueTimestamp = System.currentTimeMillis() + 172800000,
                reminderType = "Irrigation"
            )
            db.reminderDao().insertReminders(listOf(r1, r2))

            // Seed Chat Messages
            val m1 = ChatMessage(
                conversationId = "default_agri_chat",
                role = "model",
                content = "Namaste! I am AgriScan AI, your smart farming crop doctor. You can ask me any question about crop diseases, pest control, organic solutions, fertilizer dosage, or details about your recent tomato scan.",
                timestamp = System.currentTimeMillis() - 100000
            )
            val m2 = ChatMessage(
                conversationId = "default_agri_chat",
                role = "user",
                content = "Is cold-pressed neem oil safe to use with copper fungicide for tomato blight?",
                timestamp = System.currentTimeMillis() - 60000
            )
            val m3 = ChatMessage(
                conversationId = "default_agri_chat",
                role = "model",
                content = "Good question! No, do NOT tank-mix neem oil directly with copper oxychloride or copper hydroxide. Combining oils with copper compounds can cause severe phytotoxicity (foliage scorching and leaf burn), particularly in direct sunlight. Instead, apply copper fungicide first, wait 5 to 7 days, and then apply neem oil in the cool evening hours.",
                timestamp = System.currentTimeMillis() - 30000,
                citations = listOf(
                    EvidenceSource(
                        id = "src_kvk_chem",
                        title = "Compatibility Chart of Agricultural Spray Chemicals",
                        organization = "ICAR - Krishi Vigyan Kendra Extension Guide",
                        url = "https://icar.org.in/kvk-chemical-compatibility",
                        publicationDate = "2024",
                        retrievalDate = "Advisory Reference",
                        summary = "Confirms petroleum and organic horticultural oils must never be combined with copper formulations due to phytotoxicity risks."
                    )
                )
            )
            db.chatDao().insertMessages(listOf(m1, m2, m3))
        }
    }
}
