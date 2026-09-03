package com.ardabank.aradapay.domain.repository

import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    val expensesFlow: Flow<List<Expense>>
    suspend fun addExpense(expense: Expense): Result<Unit>
    suspend fun updateExpenseStatus(expenseId: String, status: ApprovalStatus): Result<Unit>
    suspend fun deleteExpense(expenseId: String): Result<Unit>
}
