package com.dilip.mysignature.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dilip.mysignature.data.model.FirmUser
import com.dilip.mysignature.ui.viewmodels.FirmViewModel

@Composable
fun FirmUsersScreen(viewModel: FirmViewModel) {
    var mail by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var firmName by remember { mutableStateOf("") }
    
    val firmUsers by viewModel.allFirmUsers.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Firm Users") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Add New Firm User", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            OutlinedTextField(
                value = firmName,
                onValueChange = { firmName = it },
                label = { Text("Firm Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = mail,
                onValueChange = { mail = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = mobile,
                onValueChange = { mobile = it },
                label = { Text("Mobile") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Button(
                onClick = {
                    if (mail.isNotBlank() && mobile.isNotBlank() && firmName.isNotBlank()) {
                        viewModel.addFirmUser(mail, mobile, firmName)
                        mail = ""
                        mobile = ""
                        firmName = ""
                    }
                },
                modifier = Modifier.padding(top = 8.dp).align(Alignment.End)
            ) {
                Text("Add User")
            }
            
            Divider(modifier = Modifier.padding(vertical = 16.dp))
            
            Text(text = "Registered Firms", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(firmUsers) { user ->
                    FirmUserItem(user = user) {
                        viewModel.deleteFirmUser(user)
                    }
                }
            }
        }
    }
}

@Composable
fun FirmUserItem(user: FirmUser, onDelete: () -> Unit) {
    Card(
        elevation = 4.dp,
        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.firmName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Email: ${user.mail}", fontSize = 14.sp)
                Text(text = "Mobile: ${user.mobile}", fontSize = 14.sp)
            }
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}
