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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    // NOTE: In a real app, this text would be loaded from a network resource or a large string resource file.
    val policyText = "Your privacy is very important to us. FundTrackr is designed to be a private budget management tool. We collect NO personal identifying information and NO financial data is transmitted externally. All your expense and income data is stored locally on your device (in the local Room database you set up). We do not use third-party analytics services that collect personal data. Your data is yours alone.\n\n" +
            "1. Data Collection: None.\n" +
            "2. Data Storage: Local (on-device only).\n" +
            "3. Data Sharing: None.\n" +
            "4. Data Retention: Data is retained on your device until you manually clear the app data or uninstall the application. \n\n" +
            "By using FundTrackr, you agree to this policy. Last Updated: December 2025."

    Scaffold(
        topBar = {
            TopAppBar(
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ExpenseTextView(
                text = "FundTrackr Privacy Commitment",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))
            ExpenseTextView(
                text = policyText,
                fontSize = 14.sp
            )
        }
    }
}

// Inside PrivacyPolicyScreen.kt

@Preview(showBackground = true)
@Composable
fun PreviewPrivacyPolicyScreen() {
    PrivacyPolicyScreen(navController = rememberNavController())
}