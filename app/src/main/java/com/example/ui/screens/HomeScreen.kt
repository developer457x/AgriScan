package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CropScan
import com.example.data.model.Farm
import com.example.data.model.UserProfile
import com.example.data.model.WeatherAdvisory
import com.example.ui.components.HealthScoreIndicator
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
import com.example.ui.theme.AgriWaterTeal

@Composable
fun HomeScreen(
    userProfile: UserProfile,
    weatherAdvisory: WeatherAdvisory,
    recentScans: List<CropScan>,
    farms: List<Farm>,
    onScanCrop: () -> Unit,
    onUploadPhoto: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenFarms: () -> Unit,
    onSelectScan: (CropScan) -> Unit,
    onOpenReminders: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgriMintWhite)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        // 1. Personalized Header & Greeting
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Namaste, ${userProfile.name}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "${userProfile.district}, ${userProfile.regionState} • Farm: ${farms.firstOrNull()?.name ?: "Main Field"}",
                    style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText)
                )
            }

            // Sync Status Badge & Notification Bell
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AgriLightMint,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDone,
                            contentDescription = "Synced",
                            tint = AgriFreshGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Online",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AgriDarkText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onOpenReminders,
                    modifier = Modifier.testTag("home_notifications_bell")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Reminders",
                        tint = AgriForestGreen
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 2. Weather & Fungal Risk Telemetry Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AgriDeepGreen),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${weatherAdvisory.temperatureC}°C",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = weatherAdvisory.condition,
                            style = MaterialTheme.typography.bodyMedium.copy(color = AgriContainerMint)
                        )
                    }

                    // Risk pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (weatherAdvisory.fungalSporeRisk == "HIGH") AgriDangerRed else AgriWarningAmber
                    ) {
                        Text(
                            text = "${weatherAdvisory.fungalSporeRisk} SPORE RISK",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = AgriContainerMint,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Humidity: ${weatherAdvisory.humidityPercent}%",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                    Text(
                        text = "Rain: ${weatherAdvisory.rainProbability}%",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = weatherAdvisory.recommendation,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 18.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 3. Quick Action Buttons Grid (Scan Crop, Upload Photo, Ask AI, History)
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AgriDarkText
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Scan Crop (Primary Action)
            ActionCard(
                title = "Scan Crop",
                subtitle = "Live Camera",
                icon = Icons.Default.CameraAlt,
                backgroundColor = AgriForestGreen,
                contentColor = Color.White,
                onClick = onScanCrop,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_scan_crop")
            )

            // Upload Image
            ActionCard(
                title = "Upload Photo",
                subtitle = "From Gallery",
                icon = Icons.Default.PhotoLibrary,
                backgroundColor = AgriLightMint,
                contentColor = AgriDarkText,
                onClick = onUploadPhoto,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_upload_photo")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ask Agri AI
            ActionCard(
                title = "Ask AI Doctor",
                subtitle = "Crop Assistant",
                icon = Icons.Default.Chat,
                backgroundColor = AgriLightMint,
                contentColor = AgriDarkText,
                onClick = onOpenChat,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_ask_ai")
            )

            // My Farms
            ActionCard(
                title = "My Farms",
                subtitle = "${farms.firstOrNull()?.activeCrops?.size ?: 3} Active Crops",
                icon = Icons.Default.Agriculture,
                backgroundColor = AgriLightMint,
                contentColor = AgriDarkText,
                onClick = onOpenFarms,
                modifier = Modifier
                    .weight(1f)
                    .testTag("action_my_farms")
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Recent Scans Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Inspections",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AgriDarkText
                )
            )

            Text(
                text = "View All (${recentScans.size})",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = AgriForestGreen
                ),
                modifier = Modifier
                    .clickable { onOpenHistory() }
                    .testTag("home_view_all_scans")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Recent Scans Carousel
        if (recentScans.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No scans yet",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Tap 'Scan Crop' to diagnose your first leaf",
                        style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText)
                    )
                }
            }
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(recentScans) { scan ->
                    RecentScanCard(
                        scan = scan,
                        onClick = { onSelectScan(scan) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 5. Seasonal Crop Alert Banner (ICAR Extension)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9ED)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF6D8A6))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AgriWarningAmber,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Regional Advisory: Late Blight Warning",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF7A4A06)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Continuous cloudy weather in Kolar & Chikkaballapur districts favor tomato late blight. Apply preventative Mancozeb or Copper Oxychloride spray before rainfall.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFF6B450B),
                            lineHeight = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Source: ICAR - Indian Institute of Horticultural Research (IIHR)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AgriMutedText,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 6. Practical Farming Tip of the Day
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AgriLightMint),
            border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = AgriForestGreen,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Agronomist Tip: Pruning Bottom Leaves",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Removing the bottom 30cm of foliage on tomato and chilli plants prevents soil-borne fungal spores from splashing onto lower leaves during irrigation.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AgriMutedText,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(96.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (backgroundColor != AgriForestGreen) androidx.compose.foundation.BorderStroke(1.dp, AgriBorder) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = contentColor.copy(alpha = 0.8f)
                    )
                )
            }
        }
    }
}

@Composable
private fun RecentScanCard(
    scan: CropScan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (scan.category) {
        "Healthy" -> AgriFreshGreen
        "Pest" -> AgriWarningAmber
        else -> AgriDangerRed
    }

    Card(
        modifier = modifier
            .width(220.dp)
            .clickable { onClick() }
            .testTag("scan_card_${scan.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Leaf Thumbnail with Score
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AgriLightMint)
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

                // Health Score Pill in top right corner
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Text(
                        text = "${scan.healthScore}% Health",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Crop Name & Diagnosis
            Text(
                text = scan.cropName,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AgriDarkText
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = scan.diagnosisName,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = statusColor,
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = scan.severity,
                    style = MaterialTheme.typography.labelSmall.copy(color = AgriMutedText)
                )

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Open",
                    tint = AgriForestGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
