package com.example.expensetracker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.expensetracker.screens.NavHostScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import java.io.OutputStreamWriter
import android.graphics.pdf.PdfDocument // Import for PDF handling
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import com.example.expensetracker.R // Ensure R is imported

// --- 1. Global Variables for Communication ---
// These must be defined outside the class scope.
lateinit var exportLauncher: ActivityResultLauncher<Intent>
// Global holders for the two distinct output formats
lateinit var csvDataToExport: String
lateinit var pdfDocumentToExport: PdfDocument // Holder for the generated PDF object
lateinit var currentExportFormat: String // "csv" or "pdf" (NEW)


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // --- 2. Initialize the ActivityResultLauncher ---
        exportLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // --- 4. Handle the result (the URI) ---
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    if (::currentExportFormat.isInitialized) {
                        // Call the appropriate writer based on the type requested by the Composable
                        when (currentExportFormat) {
                            "csv" -> writeCsvToUri(uri)
                            "pdf" -> writePdfToUri(uri)
                            else -> Toast.makeText(this, "Unknown file format selected.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Export cancelled or failed.", Toast.LENGTH_SHORT).show()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            ExpenseTrackerTheme {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    NavHostScreen()
                }
            }
        }
    }

    /**
     * Writes the globally held CSV data to the file URI chosen by the user.
     */
    private fun writeCsvToUri(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(csvDataToExport)
                }
            }
            Toast.makeText(this, "Success! Data exported as CSV.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error writing file: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    /**
     * Writes the globally held PDF document to the file URI chosen by the user.
     */
    private fun writePdfToUri(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                // FIX 1: Change 'write' to 'writeTo'
                pdfDocumentToExport.writeTo(outputStream)
            }
            pdfDocumentToExport.close()
            Toast.makeText(this, "Success! Report exported as PDF.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error writing PDF: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }}