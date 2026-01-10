package com.badal.fundtrackr.screens

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
import com.badal.fundtrackr.widget.ExpenseTextView
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(navController: NavController) {


    Scaffold(
        topBar = {
            TopAppBar(
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            ExpenseTextView(
                text = "FundTrackr Privacy Commitment",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(16.dp))

            ExpenseTextView(
                text = corePrinciple,
                fontSize = 14.sp
            )

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

            ExpenseTextView(
                text = "3. Notifications & Alerts:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            ExpenseTextView(
                text = notificationBody,
                fontSize = 14.sp
            )

            ExpenseTextView(
                text = "4. Data Sharing:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            ExpenseTextView(
                text = dataSharingBody,
                fontSize = 14.sp
            )

            ExpenseTextView(
                text = "5. Data Retention:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            ExpenseTextView(
                text = dataRetentionBody,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(16.dp))
            ExpenseTextView(
                text = "6. Policy Updates:",
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