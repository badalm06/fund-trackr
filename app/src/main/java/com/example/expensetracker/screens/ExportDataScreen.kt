package com.example.expensetracker.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.* // Import all needed runtime composables
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel // Import for viewModel() function

import com.example.expensetracker.viewmodel.HomeViewModel
import com.example.expensetracker.viewmodel.HomeViewModelFactory
import com.example.expensetracker.widget.ExpenseTextView

// Global variables imported from MainActivity.kt
import com.example.expensetracker.exportLauncher
import com.example.expensetracker.csvDataToExport

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDataScreen(navController: NavController) {
    val context = LocalContext.current

    // Instantiate ViewModel locally using the Factory
    val viewModel: HomeViewModel = remember {
        HomeViewModelFactory(context).create(HomeViewModel::class.java)
    }

    // --- CRUCIAL FIX: Collect the StateFlow as Compose State ---
    // This makes the screen recompose whenever the underlying expense data changes.
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    // ---

    var isExporting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ExpenseTextView(text = "Export Data") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Icon(
                imageVector = Icons.Default.Archive,
                contentDescription = "Export Icon",
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))

            // Display the size of the reactive list
            ExpenseTextView(
                text = "Save all transaction history to a CSV file. (${expenses.size} records found)",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    if (expenses.isEmpty()) {
                        Toast.makeText(context, "No data to export.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    // 1. Format the data into a CSV string and store it globally
                    // Use the reactive 'expenses' list here
                    csvDataToExport = viewModel.formatDataAsCsv(expenses)

                    // 2. Create the ACTION_CREATE_DOCUMENT intent
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val fileName = "FundTrackr_Export_$timestamp.csv"

                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/csv"
                        putExtra(Intent.EXTRA_TITLE, fileName)
                    }

                    // 3. Launch the system save dialogue via the global launcher
                    exportLauncher.launch(intent)
                },
                enabled = expenses.isNotEmpty(), // Button is enabled only if data is present
                modifier = Modifier.fillMaxWidth()
            ) {
                ExpenseTextView(text = "Export Data (CSV)")
            }

            Spacer(Modifier.height(16.dp))
            ExpenseTextView(
                text = "Note: You will be asked to choose a location (e.g., Downloads folder).",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// --- Preview Composable ---
@Preview(showBackground = true)
@Composable
fun PreviewExportDataScreen() {
    ExportDataScreen(navController = rememberNavController())
}