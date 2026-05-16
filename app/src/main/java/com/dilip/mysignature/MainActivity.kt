package com.dilip.mysignature

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dilip.mysignature.data.repository.SignatureRepository
import com.dilip.mysignature.navigation.AppNavGraph
import com.dilip.mysignature.navigation.Screen
import com.dilip.mysignature.ui.components.BottomNavigationBar
import com.dilip.mysignature.ui.viewmodels.SignatureViewModel
import com.dilip.mysignature.ui.viewmodels.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val captureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // Re-trigger load via VM by recreating or using a trigger
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // MVVM Injection
                val repository = remember { SignatureRepository(contentResolver) }
                val viewModel: SignatureViewModel = viewModel(
                    factory = ViewModelFactory(repository)
                )
                
                LaunchedEffect(Unit) {
                    viewModel.loadSignatures()
                }

                // Define routes that should NOT show bottom navigation
                val authRoutes = listOf(Screen.Splash.route, Screen.Login.route)
                val showBottomNav = currentRoute !in authRoutes

                Scaffold(
                    bottomBar = {
                        if (showBottomNav) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavGraph(
                        navController = navController,
                        paddingValues = innerPadding,
                        signatures = viewModel.signatures.map { it.uri },
                        onCaptureClick = {
                            val intent = Intent(this@MainActivity, CaptureSignature::class.java)
                            captureLauncher.launch(intent)
                        }
                    )
                }
            }
        }
    }
}
