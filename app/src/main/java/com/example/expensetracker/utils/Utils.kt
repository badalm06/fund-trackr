package com.example.expensetracker.utils

import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    fun formatDatetoHumanReadableForm(dateInMillis: Long): String {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormatter.format(dateInMillis)

    }

    fun formatToDecimalValue(d: Double): String {
        return String.format("%.2f", d)
    }
}