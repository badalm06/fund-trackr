package com.example.expensetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.expensetracker.data.CategoryCatalog
import com.example.expensetracker.data.ExpenseDataBase
import com.example.expensetracker.data.dao.ExpenseDao
import com.example.expensetracker.data.model.ExpenseEntity
import com.example.expensetracker.utils.Utils
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb


class HomeViewModel(private val dao: ExpenseDao): ViewModel() {

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

    fun getAllCategories(): List<String> = CategoryCatalog.allCategories

    fun getExpenseSnapshot(): List<ExpenseEntity> {
        return expenses.value
    }

    fun formatDataAsCsv(expenses: List<ExpenseEntity>): String {
        val header = "ID,Title,Amount,Date,Category,Type\n"
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dataRows = expenses.joinToString("\n") { item ->
            val dateString = dateFormatter.format(Date(item.data))

            val titleSafe = "\"${item.title.replace("\"", "\"\"")}\""
            val categorySafe = "\"${item.category.replace("\"", "\"\"")}\""

            "${item.id},${titleSafe},${item.amount},${dateString},${categorySafe},${item.type}"
        }

        return header + dataRows
    }


    fun generatePdf(expenses: List<ExpenseEntity>): PdfDocument {
        val pdfDocument = PdfDocument()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        var yPosition = 50f
        val lineSpacing = 20f

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        val paint = Paint()

        paint.color = Color.Black.toArgb()
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("FundTrackr Expense Report", margin, yPosition, paint)
        yPosition += 30f

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

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.textSize = 9f
        var pageNumber = 1

        for (item in expenses) {
            if (yPosition > pageHeight - margin) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = margin

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

            paint.color = if (item.type == "Income") Color.Green.toArgb() else Color.Red.toArgb()

            canvas.drawText(dateFormatter.format(Date(item.data)), xPositions[0], yPosition, paint)
            canvas.drawText(item.category, xPositions[1], yPosition, paint)
            canvas.drawText(item.title, xPositions[2], yPosition, paint)
            canvas.drawText("₹ ${Utils.formatToDecimalValue(item.amount)}", xPositions[3], yPosition, paint)
            canvas.drawText(item.type, xPositions[4], yPosition, paint)

            yPosition += lineSpacing
        }

        pdfDocument.finishPage(page)
        return pdfDocument
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