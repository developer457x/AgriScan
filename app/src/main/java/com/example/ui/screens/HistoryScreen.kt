package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CropScan
import com.example.ui.theme.AgriBorder
import com.example.ui.theme.AgriContainerMint
import com.example.ui.theme.AgriDangerRed
import com.example.ui.theme.AgriDarkText
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriFreshGreen
import com.example.ui.theme.AgriLightMint
import com.example.ui.theme.AgriMintWhite
import com.example.ui.theme.AgriMutedText
import com.example.ui.theme.AgriWarningAmber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    scans: List<CropScan>,
    searchQuery: String,
    selectedFilter: String,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onSelectScan: (CropScan) -> Unit,
    onDeleteScan: (String) -> Unit,
    onStartComparison: (CropScan, CropScan) -> Unit,
    modifier: Modifier = Modifier
) {
    val filterOptions = listOf("All", "Healthy", "Disease Suspected", "Pest Suspected", "Follow-up")

    var scanToDelete by remember { mutableStateOf<CropScan?>(null) }
    var comparisonSelectionMode by remember { mutableStateOf(false) }
    var selectedForCompare by remember { mutableStateOf<List<CropScan>>(emptyList()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AgriMintWhite)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Scan History",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AgriDarkText
                    )
                )
                Text(
                    text = "${scans.size} persistent crop health records",
                    style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText)
                )
            }

            // Compare Mode Button
            Button(
                onClick = {
                    comparisonSelectionMode = !comparisonSelectionMode
                    selectedForCompare = emptyList()
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (comparisonSelectionMode) AgriWarningAmber else AgriLightMint,
                    contentColor = if (comparisonSelectionMode) Color.White else AgriForestGreen
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("history_compare_toggle")
            ) {
                Icon(Icons.Default.Compare, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (comparisonSelectionMode) "Cancel" else "Compare Scans")
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search by crop, disease, or scientific name...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AgriForestGreen) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .testTag("history_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AgriForestGreen,
                unfocusedBorderColor = AgriBorder,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterOptions) { filter ->
                val isSelected = filter == selectedFilter
                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AgriForestGreen,
                        selectedLabelColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = AgriDarkText
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = AgriBorder,
                        selectedBorderColor = AgriForestGreen,
                        enabled = true,
                        selected = isSelected
                    ),
                    modifier = Modifier.testTag("history_filter_$filter")
                )
            }
        }

        if (comparisonSelectionMode) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp),
                color = AgriContainerMint,
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Selected for comparison: ${selectedForCompare.size}/2",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )

                    if (selectedForCompare.size == 2) {
                        Button(
                            onClick = {
                                onStartComparison(selectedForCompare[0], selectedForCompare[1])
                                comparisonSelectionMode = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Launch Comparison")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Scans List
        if (scans.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = AgriForestGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No matching scans found",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Try clearing search keywords or filters",
                        style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(scans, key = { it.id }) { scan ->
                    HistoryItemCard(
                        scan = scan,
                        isSelectionMode = comparisonSelectionMode,
                        isSelected = selectedForCompare.contains(scan),
                        onClick = {
                            if (comparisonSelectionMode) {
                                selectedForCompare = if (selectedForCompare.contains(scan)) {
                                    selectedForCompare - scan
                                } else {
                                    if (selectedForCompare.size < 2) selectedForCompare + scan else selectedForCompare
                                }
                            } else {
                                onSelectScan(scan)
                            }
                        },
                        onDelete = { scanToDelete = scan }
                    )
                }
            }
        }

        // Delete Confirmation Dialog
        if (scanToDelete != null) {
            AlertDialog(
                onDismissRequest = { scanToDelete = null },
                title = { Text("Delete Scan Record?") },
                text = { Text("Are you sure you want to remove the inspection record for '${scanToDelete?.cropName}'? This cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            scanToDelete?.let { onDeleteScan(it.id) }
                            scanToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AgriDangerRed)
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { scanToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun HistoryItemCard(
    scan: CropScan,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (scan.category) {
        "Healthy" -> AgriFreshGreen
        "Pest" -> AgriWarningAmber
        else -> AgriDangerRed
    }

    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateString = dateFormat.format(Date(scan.timestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("history_item_${scan.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AgriLightMint else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 2.dp else 1.dp,
            if (isSelected) AgriForestGreen else AgriBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            val resId = if (scan.thumbnailDrawableName == "farm_hero_banner") {
                R.drawable.farm_hero_banner
            } else {
                R.drawable.sample_crop_leaf
            }

            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = scan.cropName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.Black.copy(alpha = 0.7f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = "${scan.healthScore}%",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = scan.cropName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = scan.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = statusColor,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = scan.diagnosisName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = AgriDarkText,
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateString,
                    style = MaterialTheme.typography.labelSmall.copy(color = AgriMutedText)
                )
            }

            // Delete or Select Action
            if (isSelectionMode) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) AgriForestGreen else Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Text("✓", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            } else {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AgriMutedText,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
