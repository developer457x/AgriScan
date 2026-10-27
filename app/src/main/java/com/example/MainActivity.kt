package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AgriBottomBar
import com.example.ui.navigation.Screen
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.AnalysisResultScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CameraScanScreen
import com.example.ui.screens.ComparisonScreen
import com.example.ui.screens.DiagnosisDetailScreen
import com.example.ui.screens.FarmScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.RemindersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TreatmentPlanScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AgriViewModel
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Authoritative App Check initialization with launch intent as required for Firebase AI Logic
        configureAppCheck(this, intent)

        // STEP 2: Edge-to-Edge window styling
        enableEdgeToEdge()

        // STEP 3: Compose Content
        setContent {
            MyApplicationTheme {
                AgriScanApp()
            }
        }
    }
}

@Composable
fun AgriScanApp(viewModel: AgriViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Home.route

    val userProfile by viewModel.userProfile.collectAsState()
    val allScans by viewModel.allScans.collectAsState()
    val filteredScans by viewModel.filteredScans.collectAsState()
    val selectedScan by viewModel.selectedScan.collectAsState()
    val allFarms by viewModel.allFarms.collectAsState()
    val allFields by viewModel.allFields.collectAsState()
    val allReminders by viewModel.allReminders.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()
    val chatUiState by viewModel.chatUiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedCategoryFilter.collectAsState()
    val comparisonScanA by viewModel.comparisonScanA.collectAsState()
    val comparisonScanB by viewModel.comparisonScanB.collectAsState()

    // Determine whether to display the bottom navigation bar
    val showBottomBar = currentRoute in listOf(
        Screen.Home.route,
        Screen.History.route,
        Screen.Chat.route,
        Screen.Farms.route,
        Screen.Settings.route
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                AgriBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { targetRoute ->
                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Welcome Screen
            composable(Screen.Welcome.route) {
                WelcomeScreen(
                    onGetStarted = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = true }
                        }
                    },
                    onSignIn = {
                        navController.navigate(Screen.Auth.route)
                    }
                )
            }

            // 2. Authentication Screen
            composable(Screen.Auth.route) {
                AuthScreen(
                    currentProfile = userProfile,
                    onAuthSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 3. Home Dashboard
            composable(Screen.Home.route) {
                HomeScreen(
                    userProfile = userProfile,
                    weatherAdvisory = viewModel.weatherAdvisory,
                    recentScans = allScans,
                    farms = allFarms,
                    onScanCrop = { navController.navigate(Screen.CameraScan.route) },
                    onUploadPhoto = { navController.navigate(Screen.CameraScan.route) },
                    onOpenChat = { navController.navigate(Screen.Chat.route) },
                    onOpenHistory = { navController.navigate(Screen.History.route) },
                    onOpenFarms = { navController.navigate(Screen.Farms.route) },
                    onSelectScan = { scan ->
                        viewModel.selectScan(scan)
                        navController.navigate(Screen.AnalysisResult.route)
                    },
                    onOpenReminders = { navController.navigate(Screen.Reminders.route) }
                )
            }

            // 4. Camera Scanning Screen
            composable(Screen.CameraScan.route) {
                BackHandler {
                    viewModel.resetAnalysisState()
                    navController.popBackStack()
                }

                CameraScanScreen(
                    analysisState = analysisState,
                    onAnalyze = { bitmap, cropHint, drawableName ->
                        viewModel.analyzeCrop(
                            bitmap = bitmap,
                            manualCropHint = cropHint,
                            drawableName = drawableName
                        ) { resultScan ->
                            navController.navigate(Screen.AnalysisResult.route) {
                                popUpTo(Screen.CameraScan.route) { inclusive = true }
                            }
                        }
                    },
                    onBack = {
                        viewModel.resetAnalysisState()
                        navController.popBackStack()
                    }
                )
            }

            // 5. Analysis Result Screen
            composable(Screen.AnalysisResult.route) {
                val scan = selectedScan ?: allScans.firstOrNull()
                if (scan != null) {
                    BackHandler { navController.navigate(Screen.Home.route) }

                    AnalysisResultScreen(
                        scan = scan,
                        onBack = { navController.popBackStack() },
                        onOpenDiagnosisDetail = { navController.navigate(Screen.DiagnosisDetail.route) },
                        onOpenTreatmentPlan = { navController.navigate(Screen.TreatmentPlan.route) },
                        onAskAiAboutScan = { navController.navigate(Screen.Chat.route) },
                        onSetReminder = { targetScan ->
                            viewModel.addReminder(
                                title = "Recheck ${targetScan.cropName} Foliage",
                                description = "Verify dry-out of ${targetScan.diagnosisName} lesions",
                                cropName = targetScan.cropName,
                                dueDate = "In 2 days, 8:00 AM",
                                type = "Monitoring"
                            )
                            navController.navigate(Screen.Reminders.route)
                        }
                    )
                }
            }

            // 6. Detailed Diagnosis Screen
            composable(Screen.DiagnosisDetail.route) {
                val scan = selectedScan ?: allScans.firstOrNull()
                if (scan != null) {
                    DiagnosisDetailScreen(
                        scan = scan,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // 7. Treatment Plan Screen
            composable(Screen.TreatmentPlan.route) {
                val scan = selectedScan ?: allScans.firstOrNull()
                if (scan != null) {
                    TreatmentPlanScreen(
                        scan = scan,
                        onBack = { navController.popBackStack() }
                    )
                }
            }

            // 8. AI Chat Screen
            composable(Screen.Chat.route) {
                AiChatScreen(
                    messages = chatMessages,
                    chatUiState = chatUiState,
                    currentScan = selectedScan,
                    onSendMessage = { prompt -> viewModel.sendChatMessage(prompt) },
                    onBack = { navController.popBackStack() }
                )
            }

            // 9. Scan History Screen
            composable(Screen.History.route) {
                HistoryScreen(
                    scans = filteredScans,
                    searchQuery = searchQuery,
                    selectedFilter = selectedFilter,
                    onSearchChange = { viewModel.searchQuery.value = it },
                    onFilterChange = { viewModel.selectedCategoryFilter.value = it },
                    onSelectScan = { scan ->
                        viewModel.selectScan(scan)
                        navController.navigate(Screen.AnalysisResult.route)
                    },
                    onDeleteScan = { id -> viewModel.deleteScan(id) },
                    onStartComparison = { scanA, scanB ->
                        viewModel.setComparisonScans(scanA, scanB)
                        navController.navigate(Screen.Comparison.route)
                    }
                )
            }

            // 10. Comparison Screen
            composable(Screen.Comparison.route) {
                ComparisonScreen(
                    scanA = comparisonScanA,
                    scanB = comparisonScanB,
                    onBack = { navController.popBackStack() }
                )
            }

            // 11. Farm & Field Management Screen
            composable(Screen.Farms.route) {
                FarmScreen(
                    farms = allFarms,
                    fields = allFields,
                    onAddFarm = { name, loc, acres, soil, irrig, crops ->
                        viewModel.addFarm(name, loc, acres, soil, irrig, crops)
                    },
                    onAddField = { farmId, name, crop, variety, stage, acres ->
                        viewModel.addField(farmId, name, crop, variety, stage, acres)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 12. Reminders & Tasks Screen
            composable(Screen.Reminders.route) {
                RemindersScreen(
                    reminders = allReminders,
                    onToggleReminder = { id, completed -> viewModel.toggleReminder(id, completed) },
                    onDeleteReminder = { reminder -> viewModel.deleteReminder(reminder) },
                    onAddReminder = { title, desc, crop, due, type ->
                        viewModel.addReminder(title, desc, crop, due, type)
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            // 13. Settings & Accessibility Screen
            composable(Screen.Settings.route) {
                SettingsScreen(
                    userProfile = userProfile,
                    onUpdateLanguage = { lang -> viewModel.updateLanguage(lang) },
                    onOpenAuth = { navController.navigate(Screen.Auth.route) },
                    onOpenReminders = { navController.navigate(Screen.Reminders.route) },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
