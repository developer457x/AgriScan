package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "farms")
data class Farm(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val name: String = "Green Valley Agro Farm",
    val location: String = "Kolar District, Karnataka",
    val fieldSizeAcres: Double = 4.5,
    val soilType: String = "Red Sandy Loam", // Clay, Black Cotton, Sandy Loam, Alluvial
    val irrigationMethod: String = "Drip Irrigation", // Drip, Sprinkler, Flood, Rainfed
    val activeCrops: List<String> = listOf("Tomato", "Chilli", "Paddy (Rice)"),
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "fields")
data class Field(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val farmId: String,
    val fieldName: String = "Plot A - Lower Terrace",
    val cropName: String = "Tomato",
    val variety: String = "Arka Rakshak (F1)",
    val plantingDate: String = "August 15, 2026",
    val growthStage: String = "Flowering & Early Fruiting", // Seedling, Vegetative, Flowering, Fruiting, Maturity
    val acreage: Double = 1.8
)
