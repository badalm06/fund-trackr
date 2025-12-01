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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- IMPORTS FOR PDF GENERATION ---
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
// ----------------------------------


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

    /**
     * Generates the PDF file content from the expense data using Android's PdfDocument API.
     * @return The complete PdfDocument object.
     */
    fun generatePdf(expenses: List<ExpenseEntity>): PdfDocument {
        val pdfDocument = PdfDocument()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Define page setup (A4 size approximation at 72dpi: 595x842 points)
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        var yPosition = 50f
        val lineSpacing = 20f

        // Initialize first page
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()

        // --- Header and Title ---
        paint.color = Color.Black.toArgb()
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("FundTrackr Expense Report", margin, yPosition, paint)
        yPosition += 30f

        // --- Column Headers ---
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 10f
        val xPositions = floatArrayOf(margin, margin + 80f, margin + 200f, margin + 300f, margin + 400f)

        canvas.drawText("Date", xPositions[0], yPosition, paint)
        canvas.drawText("Category", xPositions[1], yPosition, paint)
        canvas.drawText("Title", xPositions[2], yPosition, paint)
        canvas.drawText("Amount", xPositions[3], yPosition, paint)
        canvas.drawText("Type", xPositions[4], yPosition, paint)

        yPosition += 5f
        canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, paint)
        yPosition += lineSpacing

        // --- Data Rows ---
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 9f
        var pageNumber = 1

        for (item in expenses) {
            // Check for new page if close to bottom
            if (yPosition > pageHeight - margin) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = margin // Reset Y position

                // Redraw header on new page (optional)
                paint.color = Color.Black.toArgb()
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                paint.textSize = 10f
                canvas.drawText("Date", xPositions[0], yPosition, paint)
                canvas.drawText("Category", xPositions[1], yPosition, paint)
                canvas.drawText("Title", xPositions[2], yPosition, paint)
                canvas.drawText("Amount", xPositions[3], yPosition, paint)
                canvas.drawText("Type", xPositions[4], yPosition, paint)
                yPosition += 5f
                canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, paint)
                yPosition += lineSpacing
                paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                paint.textSize = 9f
            }

            // Set text color based on Income/Expense
            paint.color = if (item.type == "Income") Color.Green.toArgb() else Color.Red.toArgb()

            // Draw columns
            canvas.drawText(dateFormatter.format(Date(item.data)), xPositions[0], yPosition, paint)
            canvas.drawText(item.category, xPositions[1], yPosition, paint)
            canvas.drawText(item.title, xPositions[2], yPosition, paint)
            canvas.drawText("₹ ${Utils.formatToDecimalValue(item.amount)}", xPositions[3], yPosition, paint)
            canvas.drawText(item.type, xPositions[4], yPosition, paint)

            yPosition += lineSpacing
        }

        // Finish the final page
        pdfDocument.finishPage(page)
        return pdfDocument
    }
    // ----------------------------------------------------
    // END: Export Data Functions
    // ----------------------------------------------------
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