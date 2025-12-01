package com.example.expensetracker.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.widget.ExpenseTextView
import com.example.expensetracker.R
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {

    // --- Define Clean Policy Content (Structured for Compose) ---
    val corePrinciple = "This policy outlines how FundTrackr handles your data. Our core principle is complete user privacy and local data control.\n\n" +
            "FundTrackr is designed exclusively as a private, on-device budget management tool. We operate without any external data collection mechanisms.\n"

    val dataCollected = "\nWe collect NO personal identifying information (like email, name, or location) and NO financial data is transmitted externally.\n"

    val dataStorageBody = "\nAll your expense and income data (transaction details, amounts, categories, and dates) is stored Locally on your device. We do not use third-party analytics or server-side services that collect personal data. Your data is yours alone.\n"

    val dataSharingBody = "\nWe do not sell, rent, or share your locally stored data with any third-party entities, advertisers, or affiliates.\n"

    val dataRetentionBody = "\nData is retained on your device until you manually clear the app data, delete the application from your device, or choose to use the 'Export Data' feature.\n"

    val policyConclusion = "\nThis policy may be updated occasionally. We encourage users to review this page periodically for any changes. Continued use of FundTrackr constitutes your acceptance of the updated terms. Last Updated: December 2025.\n"
    // ----------------------------------------------------


    Scaffold(
        topBar = {
            TopAppBar(
                // Applying the window inset fix for the gap:
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                title = { ExpenseTextView(text = "Privacy Policy") },
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
                .verticalScroll(rememberScrollState()) // Ensures the whole content is scrollable
                .padding(16.dp)
        ) {
            // --- TITLE ---
            ExpenseTextView(
                text = "FundTrackr Privacy Commitment",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(16.dp))

            // --- CORE PRINCIPLE / INTRO ---
            ExpenseTextView(
                text = corePrinciple,
                fontSize = 14.sp
            )

            // --- 1. DATA COLLECTED ---
            ExpenseTextView(
                text = "1. Data Collected:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            ExpenseTextView(
                text = dataCollected,
                fontSize = 14.sp
            )

            // --- 2. DATA STORAGE & SECURITY ---
            ExpenseTextView(
                text = "2. Data Storage & Security:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            ExpenseTextView(
                text = dataStorageBody,
                fontSize = 14.sp
            )

            // --- 3. DATA SHARING ---
            ExpenseTextView(
                text = "3. Data Sharing:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            ExpenseTextView(
                text = dataSharingBody, // <-- NEW LINE ADDED HERE
                fontSize = 14.sp
            )

            // --- 4. DATA RETENTION ---
            ExpenseTextView(
                text = "4. Data Retention:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            ExpenseTextView(
                text = dataRetentionBody,
                fontSize = 14.sp
            )

            // --- 5. POLICY UPDATES ---
            Spacer(Modifier.height(16.dp))
            ExpenseTextView(
                text = "5. Policy Updates:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            ExpenseTextView(
                text = policyConclusion,
                fontSize = 14.sp
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPrivacyPolicyScreen() {
    PrivacyPolicyScreen(navController = rememberNavController())
}