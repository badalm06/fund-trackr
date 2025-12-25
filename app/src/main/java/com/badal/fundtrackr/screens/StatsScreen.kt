package com.badal.fundtrackr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.badal.fundtrackr.viewmodel.HomeViewModel
import com.badal.fundtrackr.viewmodel.HomeViewModelFactory
import com.badal.fundtrackr.widget.ExpenseTextView
import kotlin.math.roundToInt
import com.github.tehras.charts.piechart.PieChart
import com.github.tehras.charts.piechart.PieChartData
import com.github.tehras.charts.piechart.renderer.SimpleSliceDrawer
// -------------------------------------------------

@Composable
fun StatsScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(LocalContext.current))
) {
    // Collect list of expenses from Flow
    val expensesList by viewModel.expenses.collectAsState(initial = emptyList())

    // Tabs: 0 => Expense, 1 => Income
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Expenses", "Income")

    // Filter list per tab
    val filtered = remember(expensesList, selectedTab) {
        if (selectedTab == 0) {
            expensesList.filter { it.type.equals("Expense", true) }
        } else {
            expensesList.filter { it.type.equals("Income", true) }
        }
    }

    // Category wise aggregation
    val categoryStats = remember(filtered) {
        val grouped = filtered.groupBy { it.category.ifBlank { "Other" } }
        val total = grouped.values.sumOf { list -> list.sumOf { it.amount } }
        if (total == 0.0) emptyList()
        else grouped.map { (cat, list) ->
            val sum = list.sumOf { it.amount }
            CategoryStat(
                category = cat,
                amount = sum,
                percent = (sum / total.toFloat()) * 100f
            )
        }.sortedByDescending { it.amount }
    }

    // Totals / balance strings (already formatted inside ViewModel)
    val totalIncomeStr = viewModel.getTotalIncome(expensesList)
    val totalExpenseStr = viewModel.getTotalExpense(expensesList)
    val balanceStr = viewModel.getBalance(expensesList)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {

        // Top Summary Row
        SummaryRow(
            income = totalIncomeStr,
            expense = totalExpenseStr,
            balance = balanceStr
        )

        Spacer(Modifier.height(12.dp))

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        ExpenseTextView(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp,
                            color = if (selectedTab == index)
                                MaterialTheme.colorScheme.primary
                            else Color.Gray
                        )
                    }
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Content
        if (categoryStats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp), // leave space for bottom bar if any
                contentAlignment = Alignment.Center
            ) {
                ExpenseTextView(
                    text = "No ${tabs[selectedTab]} data yet",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        } else {
            // Show Pie + List
            PieChartWithCategoryList(
                stats = categoryStats,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private data class CategoryStat(
    val category: String,
    val amount: Double,
    val percent: Double
)

@Composable
private fun SummaryRow(
    income: String,
    expense: String,
    balance: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SummaryChip(label = "Income", value = income, color = Color(0xFF2E7D32))
        SummaryChip(label = "Expense", value = expense, color = Color(0xFFD32F2F))
        SummaryChip(label = "Balance", value = balance, color = Color(0xFF1565C0))
    }
}

@Composable
private fun SummaryChip(label: String, value: String, color: Color) {
    Column {
        ExpenseTextView(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
        ExpenseTextView(
            text = value, // already contains $ from ViewModel
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

@Composable
private fun PieChartWithCategoryList(
    stats: List<CategoryStat>,
    modifier: Modifier = Modifier
) {
    // Simple palette cycle
    val palette = listOf(
        Color(0xFFEF5350),
        Color(0xFFFFA726),
        Color(0xFF66BB6A),
        Color(0xFF42A5F5),
        Color(0xFFAB47BC),
        Color(0xFFFFD54F),
        Color(0xFF26C6DA),
        Color(0xFF8D6E63)
    )

    val slices = stats.mapIndexed { index, stat ->
        PieChartData.Slice(
            value = stat.amount.toFloat(),
            color = palette[index % palette.size]
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Pie Chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            PieChart(
                pieChartData = PieChartData(slices),
                sliceDrawer = SimpleSliceDrawer(
                    sliceThickness = 36f,
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(8.dp)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Category List
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(stats) { stat ->
                CategoryRow(
                    stat = stat,
                    color = slices[stats.indexOf(stat)].color
                )
            }
            item { Spacer(Modifier.height(60.dp)) } // bottom padding space
        }
    }
}

@Composable
private fun CategoryRow(stat: CategoryStat, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(color = color, shape = MaterialTheme.shapes.small)
            )
            Column(Modifier.padding(start = 12.dp)) {
                ExpenseTextView(
                    text = stat.category,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                ExpenseTextView(
                    text = "${stat.percent.roundToInt()}%",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }
        ExpenseTextView(
            text = formatMoneyForRow(stat.amount),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// Simple money formatter (without duplicating ViewModel logic)
private fun formatMoneyForRow(value: Double): String {
    return if (value % 1.0 == 0.0) {
        "₹ ${"%,.0f".format(value)}"
    } else {
        "₹ ${"%,.2f".format(value)}"
    }
}
