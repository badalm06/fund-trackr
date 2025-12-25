package com.badal.fundtrackr.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.badal.fundtrackr.data.ExpenseDataBase
import com.badal.fundtrackr.data.dao.ExpenseDao
import com.badal.fundtrackr.data.model.ExpenseEntity
import com.badal.fundtrackr.utils.NotificationHelper
import com.badal.fundtrackr.utils.Utils
import kotlinx.coroutines.flow.first

class AddExpenseViewModel(private val dao: ExpenseDao, private val context: Context): ViewModel() {

    suspend fun addExpense(expenseEntity: ExpenseEntity): Boolean {
        try {
            dao.insertExpense(expenseEntity)
            checkAndNotify(expenseEntity)
            return true
        } catch (ex: Throwable) {
            return false
        }
    }

    private suspend fun checkAndNotify(transaction: ExpenseEntity) {

        val transactions = dao.getAllExpenses().first()

        val totalIncome = transactions.filter { it.type.equals("Income", true) }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type.equals("Expense", true) }.sumOf { it.amount }
        val balance = totalIncome - totalExpense

        if (balance < 100) {
            NotificationHelper.showNotification(
                context,
                "⚠️ Low Balance Alert",
                "Your balance is dangerously low — ₹${"%.2f".format(balance)} left.",
                1
            )
        }

        if (totalExpense > 2000 && totalExpense <= 5000) {
            NotificationHelper.showNotification(
                context,
                "💸 Spending Alert",
                "You've crossed ₹2000 in expenses this month!",
                2
            )
        }

        if (totalExpense > 5000 && totalExpense <= 10000) {
            NotificationHelper.showNotification(
                context,
                "💸 Spending Alert",
                "You've crossed ₹5000 in expenses this month!",
                2
            )
        }

        if (totalExpense > 10000 && totalExpense <= 50000) {
            NotificationHelper.showNotification(
                context,
                "💸 Spending Alert",
                "You've crossed ₹10000 in expenses this month!",
                2
            )
        }

        if (totalExpense > 50000 && totalExpense <= 100000) {
            NotificationHelper.showNotification(
                context,
                "💸 Spending Alert",
                "You've crossed ₹50000 in expenses this month!",
                2
            )
        }

        if (totalExpense > 100000 && totalExpense <= 500000) {
            NotificationHelper.showNotification(
                context,
                "💸 Spending Alert",
                "You've crossed ₹100000 in expenses this month!",
                2
            )
        }

        if (transaction.type.equals("Expense", true) && transaction.amount > 1000) {
            NotificationHelper.showNotification(
                context,
                "💳 Big Transaction",
                "You spent ₹${Utils.formatToDecimalValue(transaction.amount)} on ${transaction.category}.",
                5
            )
        }
    }
}

class AddExpenseViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseViewModel::class.java)) {
            val dao = ExpenseDataBase.getDatabase(context).expenseDao()
            @Suppress("UNCHECKED_CAST")
            return AddExpenseViewModel(dao, context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}