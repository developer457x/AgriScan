package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Farm
import com.example.data.model.Field
import com.example.ui.theme.AgriBorder
import com.example.ui.theme.AgriContainerMint
import com.example.ui.theme.AgriDarkText
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriFreshGreen
import com.example.ui.theme.AgriLightMint
import com.example.ui.theme.AgriMintWhite
import com.example.ui.theme.AgriMutedText

@Composable
fun FarmScreen(
    farms: List<Farm>,
    fields: List<Field>,
    onAddFarm: (name: String, loc: String, acres: Double, soil: String, irrig: String, crops: List<String>) -> Unit,
    onAddField: (farmId: String, name: String, crop: String, variety: String, stage: String, acres: Double) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddFarmDialog by remember { mutableStateOf(false) }
    var showAddFieldDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AgriMintWhite)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("farm_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AgriDarkText
                        )
                    }
                    Text(
                        text = "My Farms & Crops",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = AgriDarkText
                        )
                    )
                }

                Button(
                    onClick = { showAddFarmDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_farm_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Farm")
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(farms) { farm ->
                    FarmCardItem(
                        farm = farm,
                        fields = fields.filter { it.farmId == farm.id },
                        onAddField = { showAddFieldDialog = true }
                    )
                }
            }
        }

        // Add Farm Dialog
        if (showAddFarmDialog) {
            AddFarmDialog(
                onDismiss = { showAddFarmDialog = false },
                onConfirm = { name, loc, acres, soil, irrig, crops ->
                    onAddFarm(name, loc, acres, soil, irrig, crops)
                    showAddFarmDialog = false
                }
            )
        }

        // Add Field Dialog
        if (showAddFieldDialog && farms.isNotEmpty()) {
            AddFieldDialog(
                farmId = farms.first().id,
                onDismiss = { showAddFieldDialog = false },
                onConfirm = { fId, fName, crop, variety, stage, acres ->
                    onAddField(fId, fName, crop, variety, stage, acres)
                    showAddFieldDialog = false
                }
            )
        }
    }
}

@Composable
private fun FarmCardItem(
    farm: Farm,
    fields: List<Field>,
    onAddField: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(AgriLightMint),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = AgriForestGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = farm.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AgriDarkText
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, tint = AgriMutedText, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = farm.location,
                                style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = AgriLightMint
                ) {
                    Text(
                        text = "${farm.fieldSizeAcres} Acres",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = AgriForestGreen,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Farm Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Soil Type", style = MaterialTheme.typography.labelSmall.copy(color = AgriMutedText))
                    Text(farm.soilType, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = AgriDarkText))
                }
                Column {
                    Text("Irrigation Method", style = MaterialTheme.typography.labelSmall.copy(color = AgriMutedText))
                    Text(farm.irrigationMethod, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = AgriDarkText))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Registered Plots / Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Registered Plots (${fields.size})",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = AgriDarkText
                    )
                )

                TextButton(onClick = onAddField) {
                    Text("+ Add Plot", color = AgriForestGreen, fontWeight = FontWeight.Bold)
                }
            }

            fields.forEach { field ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AgriMintWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${field.fieldName} • ${field.cropName}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AgriDarkText
                                )
                            )
                            Text(
                                text = "Variety: ${field.variety} • Stage: ${field.growthStage}",
                                style = MaterialTheme.typography.bodySmall.copy(color = AgriMutedText)
                            )
                        }

                        Text(
                            text = "${field.acreage} ac",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = AgriForestGreen
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AddFarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, loc: String, acres: Double, soil: String, irrig: String, crops: List<String>) -> Unit
) {
    var name by remember { mutableStateOf("Sri Krishna Organic Farm") }
    var location by remember { mutableStateOf("Mandya, Karnataka") }
    var acresText by remember { mutableStateOf("3.0") }
    var soil by remember { mutableStateOf("Alluvial Clay Loam") }
    var irrigation by remember { mutableStateOf("Drip & Rainfed") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Farm") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Farm Name") }
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location / District") }
                )
                OutlinedTextField(
                    value = acresText,
                    onValueChange = { acresText = it },
                    label = { Text("Total Land (Acres)") }
                )
                OutlinedTextField(
                    value = soil,
                    onValueChange = { soil = it },
                    label = { Text("Soil Type") }
                )
                OutlinedTextField(
                    value = irrigation,
                    onValueChange = { irrigation = it },
                    label = { Text("Irrigation Method") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val acres = acresText.toDoubleOrNull() ?: 2.5
                    onConfirm(name, location, acres, soil, irrigation, listOf("Tomato", "Paddy"))
                },
                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen)
            ) {
                Text("Register")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun AddFieldDialog(
    farmId: String,
    onDismiss: () -> Unit,
    onConfirm: (farmId: String, name: String, crop: String, variety: String, stage: String, acres: Double) -> Unit
) {
    var name by remember { mutableStateOf("Plot 3 - South Hillside") }
    var crop by remember { mutableStateOf("Chilli") }
    var variety by remember { mutableStateOf("Byadgi") }
    var stage by remember { mutableStateOf("Vegetative Growth") }
    var acresText by remember { mutableStateOf("1.2") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Field Plot") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Plot Name") })
                OutlinedTextField(value = crop, onValueChange = { crop = it }, label = { Text("Crop") })
                OutlinedTextField(value = variety, onValueChange = { variety = it }, label = { Text("Variety") })
                OutlinedTextField(value = stage, onValueChange = { stage = it }, label = { Text("Growth Stage") })
                OutlinedTextField(value = acresText, onValueChange = { acresText = it }, label = { Text("Acreage") })
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val acres = acresText.toDoubleOrNull() ?: 1.0
                    onConfirm(farmId, name, crop, variety, stage, acres)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AgriForestGreen)
            ) {
                Text("Add Plot")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
