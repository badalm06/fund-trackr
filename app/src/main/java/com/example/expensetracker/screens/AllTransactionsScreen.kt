package com.example.expensetracker.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.expensetracker.data.CategoryCatalog
import com.example.expensetracker.data.CategoryFilterDropdown
import com.example.expensetracker.data.DateRangeFilterDropdown
import com.example.expensetracker.data.TypeFilterDropdown
import com.example.expensetracker.utils.Utils
import com.example.expensetracker.viewmodel.HomeViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(navController: NavController, viewModel: HomeViewModel) {

    val allTransactions by viewModel.expenses.collectAsState(initial = emptyList())

    var selectedCategory by remember { mutableStateOf("All") }
    var selectedType by remember { mutableStateOf("All") }
    var selectedDateRange by remember { mutableStateOf("All Time") }
    var selectedCustomDate by remember { mutableStateOf<Calendar?>(null) }
    var showCustomDatePicker by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val now = Calendar.getInstance()


    if (showCustomDatePicker) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                selectedCustomDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                showCustomDatePicker = false
            },
            now.get(Calendar.YEAR),
            now.get(Calendar.MONTH),
            now.get(Calendar.DAY_OF_MONTH)
        ).show()
    }




    val filteredTransactions = remember(selectedCategory, selectedType, selectedDateRange, allTransactions, selectedCustomDate) {
        allTransactions.filter { transaction ->
            val matchesCategory = selectedCategory == "All" || transaction.category == selectedCategory
            val matchesType = selectedType == "All" || transaction.type == selectedType

            val transactionDate = Calendar.getInstance().apply { timeInMillis = transaction.data }
            val now = Calendar.getInstance()

            val matchesDate = when (selectedDateRange) {
                "Today" -> transactionDate.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        transactionDate.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
                "Last 7 Days" -> {
                    val sevenDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -7) }
                    transactionDate.after(sevenDaysAgo) && !transactionDate.after(now)
                }

                "Last 30 Days" -> {
                    val thirtyDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
                    transactionDate.after(thirtyDaysAgo) && !transactionDate.after(now)
                }
                "Custom" -> selectedCustomDate?.let {
                    transactionDate.get(Calendar.YEAR) == it.get(Calendar.YEAR) &&
                            transactionDate.get(Calendar.DAY_OF_YEAR) == it.get(Calendar.DAY_OF_YEAR)
                } ?: false
                else -> true
            }
            matchesCategory && matchesType && matchesDate
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Transactions") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    var showFilterMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            CategoryFilterDropdown(
                                selectedCategory = selectedCategory,
                                onCategorySelected = { selectedCategory = it },
                                categories = listOf("All") + CategoryCatalog.allCategories
                            )
                            Spacer(Modifier.height(8.dp))
                            TypeFilterDropdown(
                                selectedType = selectedType,
                                onTypeSelected = { selectedType = it }
                            )
                            Spacer(Modifier.height(8.dp))
                            DateRangeFilterDropdown(
                                selectedDateRange = selectedDateRange,
                                onDateRangeSelected = { range ->
                                    selectedDateRange = range
                                    if (range == "Custom") {
                                        showCustomDatePicker = true
                                    }
                                }
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { showFilterMenu = false }, modifier = Modifier.fillMaxWidth()) {
                                Text("Apply Filters")
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            if (filteredTransactions.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillParentMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No transactions found for the selected filters.", color = Color.Gray, fontSize = 16.sp)
                    }
                }
            } else {
                items(filteredTransactions) { item ->
                    val isIncome = item.type == "Income"
                    val sign = if (isIncome) "+ ₹" else "- ₹"
                    val amt = Utils.formatToDecimalValue(item.amount)
                    TransactionItem(
                        title = item.title,
                        amountDisplay = "$sign$amt",
                        image = CategoryCatalog.iconFor(item.category),
                        date = Utils.formatDatetoHumanReadableForm(item.data),
                        amountColor = if (isIncome) Color(0xFF2E7D32) else Color(0xFFD32F2F),
                        onEditClick = { navController.navigate("edit_expense/${item.id}") },
                        onDeleteClick = { viewModel.deleteExpense(item) }
                    )
            }
            }
        }
    }
}
