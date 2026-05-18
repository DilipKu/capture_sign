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
import com.dilip.mysignature.data.db.AppDatabase
import com.dilip.mysignature.data.repository.FirmRepository
import com.dilip.mysignature.data.repository.SignatureRepository
import com.dilip.mysignature.navigation.AppNavGraph
import com.dilip.mysignature.navigation.Screen
import com.dilip.mysignature.ui.components.BottomNavigationBar
import com.dilip.mysignature.ui.viewmodels.FirmViewModel
import com.dilip.mysignature.ui.viewmodels.SignatureViewModel
import com.dilip.mysignature.ui.viewmodels.ViewModelFactory

class MainActivity : ComponentActivity() {

    private val captureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
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
                val database = remember { AppDatabase.getDatabase(applicationContext) }
                val signatureRepository = remember { SignatureRepository(contentResolver) }
                val firmRepository = remember { FirmRepository(database.firmUserDao()) }
                
                val signatureViewModel: SignatureViewModel = viewModel(
                    factory = ViewModelFactory(signatureRepository)
                )
                val firmViewModel: FirmViewModel = viewModel(
                    factory = ViewModelFactory(firmRepository)
                )
                
                LaunchedEffect(Unit) {
                    signatureViewModel.loadSignatures()
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
                        firmViewModel = firmViewModel,
                        signatures = signatureViewModel.signatures.map { it.uri },
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
