package com.example.expensetracker.screens // Your existing package

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
import com.example.expensetracker.navigation.BottomBar
import com.example.expensetracker.navigation.BottomNavItem
import com.example.expensetracker.viewmodel.HomeViewModel // Your HomeViewModel
import com.example.expensetracker.viewmodel.HomeViewModelFactory // Your HomeViewModelFactory


// Optional: centralize extra route(s)
object ExtraRoutes {
    const val ADD_EXPENSE = "addExpense"
    const val ALL_TRANSACTIONS = "all_transactions_screen" // Define the route for AllTransactionsScreen
}

@Composable
fun NavHostScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current // Get the current context

    // Properly instantiate HomeViewModel using the viewModel() helper and your factory
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))

    // Observe current route reactively for BottomBar visibility
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                // Pass the HomeViewModel to HomeScreen
                HomeScreen(navController)
            }
            composable(BottomNavItem.Stats.route) {
                // If Stats needs HomeViewModel, pass it here too
                StatsScreen()
            }
            composable(ExtraRoutes.ADD_EXPENSE) {
                // Pass the HomeViewModel to AddExpense if it needs to interact with it
                AddExpense(navController)
            }
            composable(ExtraRoutes.ALL_TRANSACTIONS) {
                // Define the route for AllTransactionsScreen and pass the HomeViewModel
                AllTransactionsScreen(navController, homeViewModel)
            }
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
