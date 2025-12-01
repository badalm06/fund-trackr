package com.example.expensetracker.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.expensetracker.widget.ExpenseTextView
import com.example.expensetracker.viewmodel.HomeViewModel // Assuming you might need this later
import com.example.expensetracker.R // Import R for potential resources

// Define routes needed for navigation from this screen
object SettingRoutes {
    const val EXPORT_DATA = "export_data_screen"
    const val ABOUT_APP = "about_screen"
    const val PRIVACY_POLICY = "privacy_policy_screen"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                // 1. Manually apply status bar padding to position content below the status bar.
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),

                // 2. CRITICAL FIX: Set windowInsets to zero to prevent double padding.
                windowInsets = WindowInsets(0.dp), // <-- ADD THIS LINE

                title = { ExpenseTextView(text = "Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            item {
                // --- CATEGORY 1: Data Management ---
                SettingsHeader(title = "Data & Security")
                SettingItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Export Data (CSV/PDF)",
                    description = "Save all transactions to a CSV/PDF file.",
                    onClick = { navController.navigate(SettingRoutes.EXPORT_DATA) }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                // --- CATEGORY 2: Information & Legal ---
                SettingsHeader(title = "Information")
                SettingItem(
                    icon = Icons.Filled.Info,
                    title = "About FundTrackr",
                    description = "View app version and developer details.",
                    onClick = { navController.navigate(SettingRoutes.ABOUT_APP) }
                )
                SettingItem(
                    icon = Icons.Filled.Lock,
                    title = "Privacy Policy",
                    description = "Read our data handling commitment.",
                    onClick = { navController.navigate(SettingRoutes.PRIVACY_POLICY) }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                // --- CATEGORY 3: Other Settings (Placeholder) ---
                SettingsHeader(title = "Appearance")
                // TODO: Add Theme/Dark Mode toggle here later
            }
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    ExpenseTextView(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 16.dp, start = 16.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            ExpenseTextView(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            ExpenseTextView(text = description, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Go", tint = Color.Gray)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(navController = rememberNavController())
}