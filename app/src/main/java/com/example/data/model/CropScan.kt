package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "crop_scans")
data class CropScan(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val cropName: String = "Tomato",
    val cropVariety: String = "Hybrid",
    val plantPart: String = "Leaf",
    val diagnosisName: String = "Early Blight",
    val scientificName: String = "Alternaria solani",
    val category: String = "Fungal", // Fungal, Bacterial, Viral, Pest, Deficiency, Healthy
    val healthScore: Int = 68, // 0 - 100
    val confidenceScore: Float = 0.92f,
    val severity: String = "Moderate", // Healthy, Low, Moderate, High, Critical
    val factorsExplanation: String = "Concentric dark rings and chlorotic halos observed on lower foliage; spread is contained to 15% leaf area with healthy surrounding tissue.",
    val visualSymptoms: List<String> = listOf(
        "Circular dark brown spots with concentric target-board rings",
        "Yellow chlorotic margin surrounding lesions",
        "Mild leaf curling on lower branches"
    ),
    val probableCauses: List<String> = listOf(
        "Soil-borne Alternaria spores activated by humid microclimate",
        "Prolonged leaf wetness from overhead irrigation",
        "Dense foliage restricting air circulation"
    ),
    val favorableConditions: String = "Warm temperatures (24°C - 29°C) combined with high humidity (>80%) or frequent dew.",
    val immediateActions: List<String> = listOf(
        "Prune and safely dispose of infected lower leaves away from the field",
        "Avoid overhead sprinkler irrigation; switch to drip or base watering",
        "Disinfect pruning shears with 70% alcohol or bleach solution"
    ),
    val nonChemicalControls: List<String> = listOf(
        "Apply organic Trichoderma harzianum or Bacillus subtilis bio-fungicide to soil",
        "Spray cold-pressed Neem Oil (5ml/L + mild soap emulsifier) at dawn or dusk",
        "Mulch base of plants with straw to prevent soil-splash onto lower foliage",
        "Maintain 60cm row spacing to maximize canopy ventilation"
    ),
    val chemicalControls: List<String> = listOf(
        "Mancozeb 75% WP @ 2.0g per Liter of water - broad spectrum protective fungicide",
        "Copper Oxychloride 50% WP @ 2.5g per Liter of water - preventative contact spray",
        "Chlorothalonil 75% WP @ 2.0g per Liter of water as rotation option"
    ),
    val preventionTips: List<String> = listOf(
        "Practice 3-year crop rotation with non-solanaceous crops (e.g. legumes or corn)",
        "Select disease-resistant certified seed varieties",
        "Stake plants upright to keep leaves elevated above soil dampness"
    ),
    val safetyPrecautions: String = "Wear chemical-resistant gloves, goggles, and mask during fungicide mixing and spraying. Re-Entry Interval (REI) is 24 hours; Pre-Harvest Interval (PHI) is 5 days. Avoid spraying when pollinators and bees are active.",
    val citations: List<EvidenceSource> = listOf(
        EvidenceSource(
            id = "src_icar_01",
            title = "Integrated Disease Management in Solanaceous Crops",
            organization = "ICAR - Indian Institute of Horticultural Research",
            url = "https://iihr.icar.gov.in/integrated-pest-and-disease-management-tomato",
            publicationDate = "2024",
            retrievalDate = "Current advisory",
            summary = "Recommends early sanitation, copper-based preventive sprays, and Trichoderma seed treatment for Alternaria solani control."
        ),
        EvidenceSource(
            id = "src_fao_02",
            title = "Farmer Field School Guidance: Tomato IPM Protocols",
            organization = "Food and Agriculture Organization (FAO)",
            url = "https://www.fao.org/plant-production-protection/ipm",
            publicationDate = "2023",
            retrievalDate = "Extension manual",
            summary = "Highlights moisture management, cultural pruning of infected bottom leaves, and biopesticide alternation."
        )
    ),
    val boundingBoxes: List<DetectionBox> = listOf(
        DetectionBox(
            label = "Early Blight Lesion",
            confidence = 0.94f,
            left = 0.42f,
            top = 0.41f,
            right = 0.58f,
            bottom = 0.58f,
            severity = "Moderate"
        )
    ),
    val localImagePath: String = "",
    val thumbnailDrawableName: String = "sample_crop_leaf",
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = "",
    val isFollowUpNeeded: Boolean = true,
    val isSynced: Boolean = true,
    val farmId: String = "farm_main",
    val treatmentApplied: String = ""
)

data class EvidenceSource(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val organization: String,
    val url: String,
    val publicationDate: String,
    val retrievalDate: String,
    val summary: String
)

data class DetectionBox(
    val label: String,
    val confidence: Float,
    val left: Float,   // 0.0 - 1.0 (relative to image width)
    val top: Float,    // 0.0 - 1.0 (relative to image height)
    val right: Float,  // 0.0 - 1.0
    val bottom: Float, // 0.0 - 1.0
    val severity: String = "Moderate"
)
