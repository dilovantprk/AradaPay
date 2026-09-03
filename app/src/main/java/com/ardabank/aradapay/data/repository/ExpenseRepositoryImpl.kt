package com.ardabank.aradapay.data.repository

import com.ardabank.aradapay.data.remote.FirestoreService
import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : ExpenseRepository {

    override val expensesFlow: Flow<List<Expense>> = firestoreService.getExpensesFlow()

    override suspend fun addExpense(expense: Expense): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.addExpense(expense) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Harcama kaydedilemedi")))
            }
        }
    }

    override suspend fun updateExpenseStatus(expenseId: String, status: ApprovalStatus): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.updateExpenseStatus(expenseId, status) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Harcama durumu güncellenemedi")))
            }
        }
    }

    override suspend fun deleteExpense(expenseId: String): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.deleteExpense(expenseId) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Harcama silinemedi")))
            }
        }
    }
}
