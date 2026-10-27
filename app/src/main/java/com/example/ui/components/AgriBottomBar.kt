package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.navigation.Screen
import com.example.ui.theme.AgriBorder
import com.example.ui.theme.AgriCardBackground
import com.example.ui.theme.AgriContainerMint
import com.example.ui.theme.AgriDarkText
import com.example.ui.theme.AgriForestGreen
import com.example.ui.theme.AgriMutedText

@Composable
fun AgriBottomBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = AgriCardBackground,
        tonalElevation = 6.dp
    ) {
        // 1. Home
        val homeSelected = currentRoute == Screen.Home.route
        NavigationBarItem(
            selected = homeSelected,
            onClick = { onNavigate(Screen.Home.route) },
            icon = {
                Icon(
                    imageVector = if (homeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriDarkText,
                indicatorColor = AgriContainerMint,
                unselectedIconColor = AgriMutedText,
                unselectedTextColor = AgriMutedText
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        // 2. History
        val historySelected = currentRoute == Screen.History.route
        NavigationBarItem(
            selected = historySelected,
            onClick = { onNavigate(Screen.History.route) },
            icon = {
                Icon(
                    imageVector = if (historySelected) Icons.Filled.History else Icons.Outlined.History,
                    contentDescription = "History"
                )
            },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriDarkText,
                indicatorColor = AgriContainerMint,
                unselectedIconColor = AgriMutedText,
                unselectedTextColor = AgriMutedText
            ),
            modifier = Modifier.testTag("nav_item_history")
        )

        // 3. Ask AI
        val chatSelected = currentRoute == Screen.Chat.route
        NavigationBarItem(
            selected = chatSelected,
            onClick = { onNavigate(Screen.Chat.route) },
            icon = {
                Icon(
                    imageVector = if (chatSelected) Icons.Filled.Chat else Icons.Outlined.Chat,
                    contentDescription = "Ask AI"
                )
            },
            label = { Text("Ask AI") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriDarkText,
                indicatorColor = AgriContainerMint,
                unselectedIconColor = AgriMutedText,
                unselectedTextColor = AgriMutedText
            ),
            modifier = Modifier.testTag("nav_item_chat")
        )

        // 4. Farms
        val farmsSelected = currentRoute == Screen.Farms.route
        NavigationBarItem(
            selected = farmsSelected,
            onClick = { onNavigate(Screen.Farms.route) },
            icon = {
                Icon(
                    imageVector = if (farmsSelected) Icons.Filled.Agriculture else Icons.Outlined.Agriculture,
                    contentDescription = "My Farms"
                )
            },
            label = { Text("Farms") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriDarkText,
                indicatorColor = AgriContainerMint,
                unselectedIconColor = AgriMutedText,
                unselectedTextColor = AgriMutedText
            ),
            modifier = Modifier.testTag("nav_item_farms")
        )

        // 5. More (Settings & Reminders)
        val moreSelected = currentRoute == Screen.Settings.route || currentRoute == Screen.Reminders.route
        NavigationBarItem(
            selected = moreSelected,
            onClick = { onNavigate(Screen.Settings.route) },
            icon = {
                Icon(
                    imageVector = if (moreSelected) Icons.Filled.MoreHoriz else Icons.Outlined.MoreHoriz,
                    contentDescription = "More"
                )
            },
            label = { Text("More") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AgriForestGreen,
                selectedTextColor = AgriDarkText,
                indicatorColor = AgriContainerMint,
                unselectedIconColor = AgriMutedText,
                unselectedTextColor = AgriMutedText
            ),
            modifier = Modifier.testTag("nav_item_more")
        )
    }
}
