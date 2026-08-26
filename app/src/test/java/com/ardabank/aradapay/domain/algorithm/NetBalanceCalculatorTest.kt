package com.ardabank.aradapay.domain.algorithm

import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.model.ExpenseSplit
import org.junit.Assert.assertEquals
import org.junit.Test

class NetBalanceCalculatorTest {

    @Test
    fun testUserFinancialSummary_OnlyApprovedExpensesAffectBalance() {
        val userIds = listOf("user1", "user2")

        val approvedExpense = Expense(
            id = "exp1",
            paidBy = "user1",
            amount = 200.0,
            status = ApprovalStatus.APPROVED,
            splits = listOf(
                ExpenseSplit(id = "s1", expenseId = "exp1", userId = "user1", amountOwed = 100.0, status = ApprovalStatus.APPROVED),
                ExpenseSplit(id = "s2", expenseId = "exp1", userId = "user2", amountOwed = 100.0, status = ApprovalStatus.APPROVED)
            )
        )

        val pendingExpense = Expense(
            id = "exp2",
            paidBy = "user2",
            amount = 500.0,
            status = ApprovalStatus.PENDING,
            splits = listOf(
                ExpenseSplit(id = "s3", expenseId = "exp2", userId = "user1", amountOwed = 250.0, status = ApprovalStatus.PENDING)
            )
        )

        val expenses = listOf(approvedExpense, pendingExpense)

        // User1 financial summary
        val summaryUser1 = NetBalanceCalculator.calculateUserFinancialSummary("user1", userIds, expenses, emptyList())

        // User1 is owed 100.0 by User2 from approved expense. Pending expense should NOT alter balance.
        assertEquals(100.0, summaryUser1.totalReceivable, 0.01)
        assertEquals(0.0, summaryUser1.totalPayable, 0.01)
        assertEquals(100.0, summaryUser1.netBalance, 0.01)
    }
}
