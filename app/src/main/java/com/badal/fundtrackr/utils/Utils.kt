package com.badal.fundtrackr.utils

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

    fun String.isNumericOrEmpty(): Boolean {
        if (this.isEmpty()) return true
        return this.matches(Regex("-?\\d*(\\.\\d+)?"))
    }

}