package com.badal.fundtrackr.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.badal.fundtrackr.viewmodel.HomeViewModel
import com.badal.fundtrackr.viewmodel.HomeViewModelFactory
import com.badal.fundtrackr.widget.ExpenseTextView
import com.badal.fundtrackr.exportLauncher
// --- IMPORTANT: Import all global variables for communication ---
import com.badal.fundtrackr.csvDataToExport
import com.badal.fundtrackr.pdfDocumentToExport
import com.badal.fundtrackr.currentExportFormat
// ---
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDataScreen(navController: NavController) {
    val context = LocalContext.current

    val viewModel: HomeViewModel = remember {
        HomeViewModelFactory(context).create(HomeViewModel::class.java)
    }

    // Collect the StateFlow as Compose State for reactive updates
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())

    // --- Local function to handle data preparation and intent launch ---
    fun triggerExport(format: String) {
        if (expenses.isEmpty()) {
            Toast.makeText(context, "No data to export.", Toast.LENGTH_SHORT).show()
            return
        }

        // Set the global format variable for MainActivity to read
        currentExportFormat = format

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val (mimeType, fileExtension) = when (format) {
            "csv" -> {
                csvDataToExport = viewModel.formatDataAsCsv(expenses)
                Pair("text/csv", "csv")
            }
            "pdf" -> {
                pdfDocumentToExport = viewModel.generatePdf(expenses)
                Pair("application/pdf", "pdf")
            }
            else -> return // Should not happen
        }

        val fileName = "FundTrackr_Export_$timestamp.$fileExtension"

        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        exportLauncher.launch(intent)
    }
    // --- End of trigger function ---

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

            ExpenseTextView(
                text = "Choose format to export (${expenses.size} records found)",
                fontSize = 16.sp
            )
            Spacer(Modifier.height(32.dp))

            // --- CSV BUTTON ---
            Button(
                onClick = { triggerExport("csv") },
                enabled = expenses.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                ExpenseTextView(text = "Export as CSV (Spreadsheet)")
            }

            Spacer(Modifier.height(12.dp))

            // --- PDF BUTTON ---
            Button(
                onClick = { triggerExport("pdf") },
                enabled = expenses.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                ExpenseTextView(text = "Export as PDF (Document Report)")
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