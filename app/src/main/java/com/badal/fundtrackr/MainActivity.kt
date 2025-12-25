package com.badal.fundtrackr

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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.badal.fundtrackr.screens.NavHostScreen
import com.badal.fundtrackr.ui.theme.ExpenseTrackerTheme
import java.io.OutputStreamWriter
import com.badal.fundtrackr.utils.NotificationHelper
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.Manifest


lateinit var exportLauncher: ActivityResultLauncher<Intent>
lateinit var csvDataToExport: String
lateinit var pdfDocumentToExport: PdfDocument
lateinit var currentExportFormat: String

class MainActivity : ComponentActivity() {

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show()
            }
        }

        exportLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { uri ->
                    if (::currentExportFormat.isInitialized) {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        NotificationHelper.createNotificationChannel(this)

        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            ExpenseTrackerTheme {
                Surface(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                    NavHostScreen()
                }
            }
        }
    }

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

    private fun writePdfToUri(uri: Uri) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocumentToExport.writeTo(outputStream)
            }
            pdfDocumentToExport.close()
            Toast.makeText(this, "Success! Report exported as PDF.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error writing PDF: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}