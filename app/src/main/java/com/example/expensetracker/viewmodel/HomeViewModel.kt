package com.example.expensetracker.viewmodel

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.CategoryCatalog
import com.example.expensetracker.data.ExpenseDataBase
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.R
import com.example.expensetracker.utils.Utils
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow // ADDED
import kotlinx.coroutines.flow.SharingStarted // ADDED
import kotlinx.coroutines.flow.stateIn // ADDED
import java.text.SimpleDateFormat // ADDED
import java.util.Date // ADDED
import java.util.Locale // ADDED


class HomeViewModel(private val dao: ExpenseDao): ViewModel() {
    // MODIFIED: Convert standard Flow to StateFlow to allow synchronous access via .value for export
    val expenses: StateFlow<List<ExpenseEntity>> = dao.getAllExpenses().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addExpense(expense: ExpenseEntity) = viewModelScope.launch {
        dao.insertExpense(expense)
    }

    fun updateExpense(expense: ExpenseEntity) = viewModelScope.launch {
        dao.updateExpense(expense)
    }

    fun deleteExpense(expense: ExpenseEntity) = viewModelScope.launch {
        dao.deleteExpense(expense)
    }

    fun getBalance(list: List<ExpenseEntity>): String {
        var balance = 0.0
        list.forEach {
            if (it.type == "Income") {
                balance += it.amount
            } else {
                balance -= it.amount
            }
        }
        return "₹ ${Utils.formatToDecimalValue(balance)}"
    }

    fun getTotalExpense(list: List<ExpenseEntity>): String {
        var total = 0.0
        list.forEach {
            if (it.type == "Expense") {
                total += it.amount
            }
        }
        return "₹ ${Utils.formatToDecimalValue(total)}"
    }

    fun getTotalIncome(list: List<ExpenseEntity>): String {
        var totalIncome = 0.0
        list.forEach {
            if (it.type == "Income") {
                totalIncome += it.amount
            }
        }
        return "₹ ${Utils.formatToDecimalValue(totalIncome)}"
    }

    fun getItemIcon(item: ExpenseEntity) = CategoryCatalog.iconFor(item.category)

    /** Provide list of all known categories (for dropdowns). */
    fun getAllCategories(): List<String> = CategoryCatalog.allCategories

    // ----------------------------------------------------
    // START: Export Data Functions
    // ----------------------------------------------------

    /**
     * Retrieves the current, latest snapshot of all expenses for the export process.
     */
    fun getExpenseSnapshot(): List<ExpenseEntity> {
        // Access the current value of the StateFlow
        return expenses.value
    }

    /**
     * Converts a list of ExpenseEntity into a CSV formatted string.
     * @param expenses The list of data to format.
     * @return A String ready to be written to a .csv file.
     */
    fun formatDataAsCsv(expenses: List<ExpenseEntity>): String {
        val header = "ID,Title,Amount,Date,Category,Type\n"
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dataRows = expenses.joinToString("\n") { item ->
            val dateString = dateFormatter.format(Date(item.data))

            // Escape double quotes to ensure fields containing quotes/commas don't break CSV structure
            val titleSafe = "\"${item.title.replace("\"", "\"\"")}\""
            val categorySafe = "\"${item.category.replace("\"", "\"\"")}\""

            "${item.id},${titleSafe},${item.amount},${dateString},${categorySafe},${item.type}"
        }

        return header + dataRows
    }
}

class HomeViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val dao = ExpenseDataBase.getDatabase(context).expenseDao()
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}