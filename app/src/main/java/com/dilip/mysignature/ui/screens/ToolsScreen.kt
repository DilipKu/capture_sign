package com.dilip.mysignature.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ToolOption(
    val title: String,
    val icon: ImageVector
)

@Composable
fun ToolsScreen(onCaptureClick: () -> Unit, onNewDevClick: () -> Unit) {
    val tools = listOf(
        ToolOption("New Dev", Icons.Default.DeveloperMode),
        ToolOption("Signature", Icons.Default.Edit),
        ToolOption("Storage", Icons.Default.Storage),
        ToolOption("Security", Icons.Default.Security),
        ToolOption("Share", Icons.Default.Share),
        ToolOption("PDF", Icons.Default.PictureAsPdf),
        ToolOption("Info", Icons.Default.Info),
        ToolOption("Bluetooth", Icons.Default.Bluetooth),
        ToolOption("Wi-Fi", Icons.Default.Wifi)

    )

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(title = { Text("Android Tools") })
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(tools) { tool ->
                ToolCard(tool = tool) {
                    when (tool.title) {
                        "Signature" -> onCaptureClick()
                        "New Dev" -> onNewDevClick()
                    }
                }
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolOption, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(MaterialTheme.colors.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = tool.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}
