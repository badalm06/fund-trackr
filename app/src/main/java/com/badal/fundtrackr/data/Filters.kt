package com.badal.fundtrackr.data

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryFilterDropdown(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Ensure the shown value is something in the list (fallback = first item)
    val displayValue = when {
        categories.contains(selectedCategory) -> selectedCategory
        categories.isNotEmpty() -> categories.first()
        else -> "All"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {}, // read-only
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
            maxLines = 1
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = category,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeFilterDropdown(
    selectedType: String,
    onTypeSelected: (String) -> Unit,
    types: List<String> = listOf("All", "Income", "Expense"),
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = when {
        types.contains(selectedType) -> selectedType
        types.isNotEmpty() -> types.first()
        else -> "All"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
            maxLines = 1
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeFilterDropdown(
    selectedDateRange: String,
    onDateRangeSelected: (String) -> Unit,
    dateRanges: List<String> = listOf("All Time", "Today", "Last 7 Days", "Last 30 Days", "Custom"),
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val displayValue = when {
        dateRanges.contains(selectedDateRange) -> selectedDateRange
        dateRanges.isNotEmpty() -> dateRanges.first()
        else -> "All Time"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date Range") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
            maxLines = 1
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            dateRanges.forEach { range ->
                DropdownMenuItem(
                    text = { Text(range) },
                    onClick = {
                        onDateRangeSelected(range)
                        expanded = false
                    }
                )
            }
        }
    }
}
