package com.badal.fundtrackr.screens

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.platform.LocalContext // Import LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel() helper

// Import your existing screens and ViewModel
import com.badal.fundtrackr.navigation.BottomBar
import com.badal.fundtrackr.navigation.BottomNavItem
import com.badal.fundtrackr.viewmodel.HomeViewModel // Your HomeViewModel
import com.badal.fundtrackr.viewmodel.HomeViewModelFactory // Your HomeViewModelFactory


object ExtraRoutes {
    const val ADD_EXPENSE = "addExpense"
    const val ALL_TRANSACTIONS = "all_transactions_screen"

    const val SETTINGS_SCREEN = "settings_screen"
}

@Composable
fun NavHostScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in listOf(
                    BottomNavItem.Home.route,
                    BottomNavItem.Stats.route
                )
            ) {
                BottomBar(navController)
            }
        },

        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,

            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {

            composable(BottomNavItem.Home.route) {
                HomeScreen(navController)
            }
            composable(BottomNavItem.Stats.route) {
                StatsScreen()
            }


            composable(ExtraRoutes.ADD_EXPENSE) {
                AddExpense(navController)
            }
            composable(ExtraRoutes.ALL_TRANSACTIONS) {
                AllTransactionsScreen(navController, homeViewModel)
            }

            // --- Settings Menu Routes ---
            composable(ExtraRoutes.SETTINGS_SCREEN) {
                SettingsScreen(navController)
            }
            composable(SettingRoutes.ABOUT_APP) {
                AboutScreen(navController)
            }
            composable(SettingRoutes.PRIVACY_POLICY) {
                PrivacyPolicyScreen(navController)
            }
            composable(SettingRoutes.EXPORT_DATA) {
                ExportDataScreen(navController)
            }

            // --- Detailed/Edit Routes ---
            composable("edit_expense/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: return@composable
                EditExpenseScreen(
                    navController = navController,
                    viewModel = homeViewModel,
                    expenseId = id
                )
            }
        }
    }
}