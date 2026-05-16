package com.dilip.mysignature.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NewDevScreen() {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(title = { Text("MVVM Development") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Project Architecture: MVVM",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This project has been migrated to a clean MVVM architecture, following best practices found in your recent system development.",
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                ArchitectureSection(
                    title = "Data Layer",
                    description = "Contains Models and Repositories. Signature data is now managed by SignatureRepository.kt."
                )
                
                ArchitectureSection(
                    title = "UI Layer (View)",
                    description = "Jetpack Compose screens optimized for performance. Screens are now located in ui.screens package."
                )
                
                ArchitectureSection(
                    title = "ViewModel Layer",
                    description = "Manages UI state and communicates with Repositories. SignatureViewModel handles the business logic."
                )
                
                ArchitectureSection(
                    title = "Navigation",
                    description = "Centralized NavGraph managing Splash, Login, Home, Signatures, Profile, and Tools."
                )
            }
        }
    }
}

@Composable
fun ArchitectureSection(title: String, description: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = description, fontSize = 14.sp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f))
        Divider(modifier = Modifier.padding(top = 8.dp))
    }
}
