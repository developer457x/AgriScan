package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CropScan
import com.example.ui.components.HealthScoreIndicator
import com.example.ui.theme.AgriBorder
import com.example.ui.theme.AgriContainerMint
import com.example.ui.theme.AgriDarkText
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriFreshGreen
import com.example.ui.theme.AgriLightMint
import com.example.ui.theme.AgriMintWhite
import com.example.ui.theme.AgriMutedText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ComparisonScreen(
    scanA: CropScan?,
    scanB: CropScan?,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgriMintWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 32.dp)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("comparison_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AgriDarkText
                )
            }
            Text(
                text = "Crop Health Progression",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = AgriDarkText
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (scanA != null && scanB != null) {
            val deltaScore = scanB.healthScore - scanA.healthScore
            val isRecovering = deltaScore >= 0

            // Delta Summary Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AgriLightMint),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = if (isRecovering) AgriFreshGreen else Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (isRecovering) "+$deltaScore% Health Improvement" else "$deltaScore% Health Decline",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isRecovering) AgriForestGreen else Color.Red
                            )
                        )
                        Text(
                            text = "Tracking ${scanA.cropName} progression across treatments",
                            style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Side by Side Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Scan A (Earlier)
                ScanCompareColumn(
                    scan = scanA,
                    label = "Inspection A",
                    dateString = dateFormat.format(Date(scanA.timestamp)),
                    modifier = Modifier.weight(1f)
                )

                // Scan B (Later)
                ScanCompareColumn(
                    scan = scanB,
                    label = "Inspection B",
                    dateString = dateFormat.format(Date(scanB.timestamp)),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Comparison Metrics Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Treatment & Foliage Assessment",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    MetricRow("Initial Condition", scanA.diagnosisName, scanA.severity)
                    MetricRow("Current Status", scanB.diagnosisName, scanB.severity)
                    MetricRow("Health Meter", "${scanA.healthScore}%", "${scanB.healthScore}%")
                    MetricRow("Recorded Treatment", scanA.treatmentApplied.ifBlank { "Pruning & sanitation" }, scanB.treatmentApplied.ifBlank { "Follow-up monitoring" })
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Please select two scans from History to compare.")
            }
        }
    }
}

@Composable
private fun ScanCompareColumn(
    scan: CropScan,
    label: String,
    dateString: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AgriForestGreen
                )
            )
            Text(
                text = dateString,
                style = MaterialTheme.typography.labelSmall.copy(color = AgriMutedText)
            )

            Spacer(modifier = Modifier.height(8.dp))

            val resId = if (scan.thumbnailDrawableName == "farm_hero_banner") {
                R.drawable.farm_hero_banner
            } else {
                R.drawable.sample_crop_leaf
            }

            Image(
                painter = painterResource(id = resId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            HealthScoreIndicator(score = scan.healthScore, size = 68.dp, strokeWidth = 7.dp)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = scan.diagnosisName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AgriDarkText
                )
            )
            Text(
                text = scan.severity,
                style = MaterialTheme.typography.labelSmall.copy(color = AgriMutedText)
            )
        }
    }
}

@Composable
private fun MetricRow(label: String, valA: String, valB: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(color = AgriMutedText)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = valA,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            )
            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = AgriForestGreen)
            Text(
                text = valB,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = AgriForestGreen)
            )
        }
    }
}
