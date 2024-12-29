package com.example.project.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp

// Data class to represent a navigation item
data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigationItemClick: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem("reports", Icons.Filled.Home, "Home"),
        BottomNavItem("profile", Icons.Filled.Person, "Profile"),
        BottomNavItem("settings", Icons.Filled.Settings, "Settings")
    )
    // BottomAppBar provides the container for navigation icons
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                IconButton(
                    onClick = { onNavigationItemClick(item.route) },
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        //tint = if (currentRoute == item.route) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    }
}