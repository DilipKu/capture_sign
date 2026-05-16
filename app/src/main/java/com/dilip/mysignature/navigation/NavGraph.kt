package com.dilip.mysignature.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dilip.mysignature.ui.screens.*

@Composable
fun AppNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    signatures: List<Uri>,
    onCaptureClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Login.route) {
            LoginScreen(onLoginSuccess = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            })
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Signatures.route) {
            SignatureListScreen(
                signatureUris = signatures,
                onCaptureClick = onCaptureClick
            )
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        composable(Screen.Tools.route) {
            ToolsScreen(
                onCaptureClick = onCaptureClick,
                onNewDevClick = { navController.navigate(Screen.NewDev.route) }
            )
        }
        composable(Screen.NewDev.route) {
            NewDevScreen()
        }
    }
}
