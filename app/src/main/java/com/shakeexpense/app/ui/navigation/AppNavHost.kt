package com.shakeexpense.app.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.shakeexpense.app.ui.dashboard.DashboardScreen
import com.shakeexpense.app.ui.expense.ExpenseScreen
import com.shakeexpense.app.ui.settings.SettingsScreen
import com.shakeexpense.app.ui.setup.SetupScreen

sealed class Screen(val route: String) {
    object Setup : Screen("setup")
    object Dashboard : Screen("dashboard")
    object Expense : Screen("expense")
    object Settings : Screen("settings")
}

@Composable
fun AppNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = Screen.Dashboard.route, modifier = modifier) {
        composable(Screen.Setup.route) {
            SetupScreen(onSetupComplete = { navController.navigate(Screen.Dashboard.route) { popUpTo(Screen.Setup.route) { inclusive = true } } })
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onAddExpense = { navController.navigate(Screen.Expense.route) },
                onSettings = { navController.navigate(Screen.Settings.route) }
            )
        }
        composable(Screen.Expense.route) {
            ExpenseScreen(onDismiss = { navController.popBackStack() })
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
