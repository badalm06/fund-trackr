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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.expensetracker.utils.NotificationScheduler



object SettingRoutes {
    const val EXPORT_DATA = "export_data_screen"
    const val ABOUT_APP = "about_screen"
    const val PRIVACY_POLICY = "privacy_policy_screen"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current

    var isReminderEnabled by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Toast.makeText(context, "Alerts permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Alerts denied. Turn on in App Settings.", Toast.LENGTH_LONG).show()
            }
        }
    )

    val areNotificationsEnabled = remember {
        {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                windowInsets = WindowInsets(0.dp),
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
                SettingsHeader(title = "Data & Security")
                SettingItem(
                    icon = Icons.AutoMirrored.Filled.ExitToApp,
                    title = "Export Data (CSV/PDF)",
                    description = "Save all transactions to a CSV/PDF file.",
                    onClick = { navController.navigate(SettingRoutes.EXPORT_DATA) }
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp))

                SettingsHeader(title = "Alerts")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !areNotificationsEnabled()) {
                    SettingItem(
                        icon = Icons.Filled.NotificationsActive,
                        title = "Enable Notifications",
                        description = "Grant permission for spending alerts.",
                        onClick = {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))
                }

                SettingToggleItem(
                    icon = Icons.Filled.Schedule,
                    title = "Daily Expense Reminder",
                    description = "Get an alert every evening to log transactions.",
                    checked = isReminderEnabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !areNotificationsEnabled()) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }

                            if (areNotificationsEnabled() || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                                NotificationScheduler.scheduleDailyReminder(context)
                                isReminderEnabled = true
                            }
                        } else {
                            NotificationScheduler.cancelReminder(context)
                            isReminderEnabled = false
                        }
                    }
                )

                Divider(modifier = Modifier.padding(horizontal = 16.dp))

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

@Composable
fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
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
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    SettingsScreen(navController = rememberNavController())
}