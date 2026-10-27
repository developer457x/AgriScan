package com.example.ui.navigation

sealed class Screen(val route: String) {
    data object Welcome : Screen("welcome")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object CameraScan : Screen("camera_scan")
    data object AnalysisResult : Screen("analysis_result")
    data object DiagnosisDetail : Screen("diagnosis_detail")
    data object TreatmentPlan : Screen("treatment_plan")
    data object Chat : Screen("chat")
    data object History : Screen("history")
    data object Comparison : Screen("comparison")
    data object Farms : Screen("farms")
    data object Reminders : Screen("reminders")
    data object Settings : Screen("settings")
}
