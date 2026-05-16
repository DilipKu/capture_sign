package com.dilip.mysignature.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Auth Flow
    object Splash : Screen("splash", "Splash")
    object Login : Screen("login", "Login")

    // Bottom Nav Screens
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Signatures : Screen("signatures", "Signatures", Icons.Default.List)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Tools : Screen("tools", "Tools", Icons.Default.Build)
    object NewDev : Screen("new_dev", "New Development")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Signatures,
    Screen.Profile,
    Screen.Tools
)
