package com.example.data.model

data class UserProfile(
    val id: String = "farmer_user_01",
    val name: String = "Ramesh Kumar",
    val email: String = "ramesh.farmer@example.com",
    val phone: String = "+91 98450 12345",
    val selectedLanguage: String = "en", // en, hi, kn, te, ta, mr, bn
    val regionState: String = "Karnataka",
    val district: String = "Kolar",
    val isGoogleAuthenticated: Boolean = true,
    val totalScansDone: Int = 14,
    val memberSince: String = "June 2026"
)

data class WeatherAdvisory(
    val temperatureC: Int = 28,
    val condition: String = "Partly Cloudy with High Humidity",
    val humidityPercent: Int = 84,
    val rainProbability: Int = 45,
    val fungalSporeRisk: String = "HIGH", // LOW, MODERATE, HIGH
    val recommendation: String = "High morning humidity elevates fungal spore germination risk for Tomato & Chilli. Inspect bottom foliage today."
)
