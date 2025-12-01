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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.expensetracker.screens.NavHostScreen
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import java.io.OutputStreamWriter
import java.io.FileDescriptor
import java.io.PrintWriter

// --- 1. Global Variables for Communication (Needed by ExportDataScreen) ---
// These must be defined outside the class scope.
lateinit var exportLauncher: ActivityResultLauncher<Intent>
lateinit var csvDataToExport: String

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        // --- 2. Initialize the ActivityResultLauncher (Must be before super.onCreate) ---
        exportLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            // --- 4. Handle the result (the URI) ---
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    writeCsvToUri(uri)
                }
            } else {
                Toast.makeText(this, "Export cancelled or failed.", Toast.LENGTH_SHORT).show()
            }
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setting condition for splash screen transition
        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            ExpenseTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavHostScreen()
                }
            }
        }
    }

    /**
     * Writes the globally held CSV data (csvDataToExport) to the file URI chosen by the user.
     * This function executes AFTER the user selects a save location in the system dialog.
     */
    private fun writeCsvToUri(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                // Using OutputStreamWriter and PrintWriter for efficient text writing
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(csvDataToExport)
                }
            }
            Toast.makeText(this, "Success! Data exported to chosen location.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error writing file: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}