package com.shakeexpense.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.shakeexpense.app.service.ShakeDetectionService
import com.shakeexpense.app.ui.navigation.AppNavHost
import com.shakeexpense.app.ui.navigation.Screen
import com.shakeexpense.app.ui.theme.ShakeExpenseTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var openExpenseRequest by mutableStateOf(false)

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* handled gracefully */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            ShakeExpenseTheme {
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    LaunchedEffect(openExpenseRequest) {
                        if (openExpenseRequest) {
                            navController.navigate(Screen.Expense.route) {
                                launchSingleTop = true
                            }
                            openExpenseRequest = false
                        }
                    }
                    AppNavHost(navController = navController)
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent?) {
        super.onNewIntent(intent)
        intent?.let(::handleIntent)
    }

    private fun handleIntent(intent: android.content.Intent?) {
        if (intent?.action == ShakeDetectionService.ACTION_OPEN_EXPENSE) {
            openExpenseRequest = true
            intent.action = null
        }
    }
}
