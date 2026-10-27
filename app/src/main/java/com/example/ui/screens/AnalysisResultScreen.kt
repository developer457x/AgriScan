package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CropScan
import com.example.ui.components.HealthScoreIndicator
import com.example.ui.components.ScanBoundingBoxOverlay
import com.example.ui.theme.AgriBorder
import com.example.ui.theme.AgriContainerMint
import com.example.ui.theme.AgriDangerRed
import com.example.ui.theme.AgriDarkText
import com.example.ui.theme.AgriDeepGreen
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriFreshGreen
import com.example.ui.theme.AgriLightMint
import com.example.ui.theme.AgriMintWhite
import com.example.ui.theme.AgriMutedText
import com.example.ui.theme.AgriWarningAmber

@Composable
fun AnalysisResultScreen(
    scan: CropScan,
    onBack: () -> Unit,
    onOpenDiagnosisDetail: () -> Unit,
    onOpenTreatmentPlan: () -> Unit,
    onAskAiAboutScan: () -> Unit,
    onSetReminder: (CropScan) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Overview, 1: Treatments, 2: Research

    val statusColor = when (scan.category) {
        "Healthy" -> AgriFreshGreen
        "Pest" -> AgriWarningAmber
        else -> AgriDangerRed
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgriMintWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("analysis_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = AgriDarkText
                    )
                }
                Text(
                    text = "Crop Health Diagnosis",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AgriDarkText
                    )
                )
            }

            // Share & Reminder icons
            Row {
                IconButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "AgriScan Report: ${scan.cropName} diagnosed with ${scan.diagnosisName} (${scan.scientificName}). Health Score: ${scan.healthScore}%. Action: ${scan.immediateActions.firstOrNull() ?: "Monitor canopy"}."
                            )
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Crop Health Report"))
                    },
                    modifier = Modifier.testTag("analysis_share_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Report",
                        tint = AgriForestGreen
                    )
                }

                IconButton(
                    onClick = { onSetReminder(scan) },
                    modifier = Modifier.testTag("analysis_reminder_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddAlert,
                        contentDescription = "Set Reminder",
                        tint = AgriForestGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 1. Interactive Annotated Crop Photo Card with Bounding Boxes
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                val resId = if (scan.thumbnailDrawableName == "farm_hero_banner") {
                    R.drawable.farm_hero_banner
                } else {
                    R.drawable.sample_crop_leaf
                }

                Image(
                    painter = painterResource(id = resId),
                    contentDescription = scan.cropName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Visual lesion localization overlay
                ScanBoundingBoxOverlay(
                    boxes = scan.boundingBoxes,
                    modifier = Modifier.fillMaxSize()
                )

                // Top Left Chip: Crop & Organ
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.75f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                ) {
                    Text(
                        text = "${scan.cropName} • ${scan.plantPart}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                // Bottom Left Detection Tag
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(statusColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Detected: ${scan.diagnosisName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. Health Score Meter & Severity Rationale
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Animated Meter
                HealthScoreIndicator(
                    score = scan.healthScore,
                    size = 90.dp,
                    strokeWidth = 9.dp
                )

                Spacer(modifier = Modifier.width(20.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = scan.diagnosisName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )
                    Text(
                        text = scan.scientificName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AgriForestGreen,
                            fontWeight = FontWeight.Medium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = statusColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Severity: ${scan.severity}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = statusColor,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AgriLightMint
                        ) {
                            Text(
                                text = "${(scan.confidenceScore * 100).toInt()}% Confidence",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AgriDarkText,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            // Factors Contributing to Score
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AgriLightMint.copy(alpha = 0.5f))
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Factors Contributing to Score:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AgriForestGreen
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = scan.factorsExplanation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AgriMutedText,
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tabs: Overview | Treatment Plan | Research Citations
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = AgriForestGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AgriForestGreen
                )
            },
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Overview", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Treatment Plan", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Sources (${scan.citations.size})", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            0 -> {
                // Tab 0: Overview (Visual symptoms & immediate actions)
                OverviewTabContent(
                    scan = scan,
                    onOpenDiagnosisDetail = onOpenDiagnosisDetail
                )
            }
            1 -> {
                // Tab 1: Treatment Plan (IPM non-chemical vs chemical with PPE)
                TreatmentTabContent(
                    scan = scan,
                    onOpenFullPlan = onOpenTreatmentPlan
                )
            }
            2 -> {
                // Tab 2: Research & Official Extension Citations (ICAR, FAO, TNAU)
                SourcesTabContent(scan = scan, context = context)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Floating Action Banner: "Ask AI About This Scan"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriDeepGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Have questions about this leaf?",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Ask AgriScan AI for tank-mix safety, fertilizer adjustment, or local product alternatives.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AgriContainerMint,
                            lineHeight = 16.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = onAskAiAboutScan,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AgriFreshGreen,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("analysis_ask_ai_button")
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ask AI")
                }
            }
        }
    }
}

@Composable
private fun OverviewTabContent(
    scan: CropScan,
    onOpenDiagnosisDetail: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Immediate Actions Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Immediate Actions",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                scan.immediateActions.forEachIndexed { idx, action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${idx + 1}.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            ),
                            modifier = Modifier.width(24.dp)
                        )
                        Text(
                            text = action,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = AgriDarkText,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }

        // Observed Visual Symptoms
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Observed Visual Symptoms",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = AgriDarkText
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                scan.visualSymptoms.forEach { symptom ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(AgriForestGreen)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = symptom,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = AgriDarkText,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onOpenDiagnosisDetail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("analysis_view_diagnosis_detail"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AgriForestGreen)
                ) {
                    Text("View Detailed Biology & Causes")
                }
            }
        }
    }
}

@Composable
private fun TreatmentTabContent(
    scan: CropScan,
    onOpenFullPlan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Non-Chemical & IPM Options (Highlighted first per IPM guidelines)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = AgriLightMint),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AgriFreshGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Integrated Pest Management (IPM)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )
                }

                Text(
                    text = "Recommended biological & cultural practices to prioritize before synthetic sprays.",
                    style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText),
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                )

                scan.nonChemicalControls.forEach { tip ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text("🌿", modifier = Modifier.padding(end = 8.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = AgriDarkText,
                                lineHeight = 20.sp
                            )
                        )
                    }
                }
            }
        }

        // Chemical Controls (With Safety & Dosage)
        if (scan.chemicalControls.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Approved Chemical Options (Label Dosage)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    scan.chemicalControls.forEach { chem ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("💧", modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = chem,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = AgriDarkText,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Safety & PPE Notice
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFF3F3),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCCCC))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = AgriDangerRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = scan.safetyPrecautions,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF8A1E1E),
                                    lineHeight = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SourcesTabContent(
    scan: CropScan,
    context: android.content.Context
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Official Extension & Research Guidance",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AgriDarkText
            )
        )

        scan.citations.forEach { source ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                            context.startActivity(browserIntent)
                        } catch (e: Exception) {
                            // Ignore if browser not available in emulator
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = source.organization,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AgriForestGreen,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open Link",
                            tint = AgriForestGreen,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = source.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = source.summary,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AgriMutedText,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = source.url,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AgriForestGreen,
                            fontSize = 11.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
