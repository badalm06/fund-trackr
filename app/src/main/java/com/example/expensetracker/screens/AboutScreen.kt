package com.example.expensetracker.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.R
import com.example.expensetracker.widget.ExpenseTextView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    // This value would normally be read dynamically from BuildConfig
    val appVersion = "1.0.0"
    val developerName = "Badal Sharma"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ExpenseTextView(text = "About FundTrackr") },
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
                .verticalScroll(rememberScrollState()) // Allow scrolling for long content
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // App Logo (Placeholder - replace with your actual logo drawable)
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "FundTrackr Logo",
                modifier = Modifier.size(96.dp)
            )

            Spacer(Modifier.height(16.dp))

            ExpenseTextView(
                text = "FundTrackr",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            ExpenseTextView(
                text = "Version $appVersion",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Divider(Modifier.padding(vertical = 24.dp).fillMaxWidth(0.6f))

            ExpenseTextView(
                text = "FundTrackr is your essential tool for mindful spending and effective budget management. Track every rupee, understand your spending habits, and achieve your financial goals.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))

            ExpenseTextView(
                text = "Developed by $developerName",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            // Optional: Link for feedback
            TextButton(onClick = { /* TODO: Open email intent for feedback */ }) {
                ExpenseTextView(text = "Send Feedback")
            }

            // Essential: Link to Privacy Policy, though it has its own screen
            TextButton(onClick = { navController.navigate(SettingRoutes.PRIVACY_POLICY) }) {
                ExpenseTextView(text = "View Privacy Policy")
            }
        }
    }
}

// Inside AboutScreen.kt

@Preview(showBackground = true)
@Composable
fun PreviewAboutScreen() {
    AboutScreen(navController = rememberNavController())
}