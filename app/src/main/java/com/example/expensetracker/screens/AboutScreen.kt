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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.R
import com.example.expensetracker.widget.ExpenseTextView
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(navController: NavController) {
    val context = LocalContext.current

    // --- APP DETAILS ---
    val appVersion = "1.0.0"
    val developerName = "Badal Sharma"
    val feedbackEmail = "badalsh908@gmail.com"

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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            // --- 1. LOGO AND VERSION HEADER ---
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.logo_no_bg), // Use your official logo drawable
                contentDescription = "FundTrackr Logo",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(16.dp))

            ExpenseTextView(
                text = "FundTrackr",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            ExpenseTextView(
                text = "Version $appVersion",
                fontSize = 16.sp,
                color = Color.Gray
            )

            Divider(Modifier.padding(vertical = 24.dp).fillMaxWidth(0.6f))

            // --- 2. MISSION STATEMENT & DESCRIPTION ---
            ExpenseTextView(
                text = "Control your cash, not your mood.", // Tagline
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 14.dp)
            )

            ExpenseTextView(
                text = "FundTrackr is your essential tool for mindful spending and effective budget management. Track every rupee, understand your spending habits, and achieve your financial goals.",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )

            Divider(Modifier.padding(vertical = 24.dp).fillMaxWidth(0.8f))

            // --- 3. CONTACT & LEGAL SECTION ---

            ExpenseTextView(
                text = "Developed by $developerName",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware color for readability
            )

            Spacer(Modifier.height(16.dp))

            // Clickable button for feedback email intent
            TextButton(
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:") // Only email apps should handle this
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(feedbackEmail))
                        putExtra(Intent.EXTRA_SUBJECT, "FundTrackr Feedback/Support - v$appVersion")
                    }
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        // Handle case where no email app is installed
                        android.widget.Toast.makeText(context, "No email app found.", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                ExpenseTextView(text = "Contact: $feedbackEmail", fontWeight = FontWeight.Bold)
            }

            // Link to Privacy Policy
            TextButton(onClick = { navController.navigate(SettingRoutes.PRIVACY_POLICY) }) {
                ExpenseTextView(text = "View Privacy Policy")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// --- PREVIEW COMPOSABLE ---
@Preview(showBackground = true)
@Composable
fun PreviewAboutScreen() {
    AboutScreen(navController = rememberNavController())
}