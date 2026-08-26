package com.ardabank.aradapay.domain.model

enum class SplitMethod {
    EQUAL, EXACT, PERCENTAGE
}

enum class ExpenseCategory {
    DINING, GROCERIES, TRAVEL, HOUSING, ENTERTAINMENT, UTILITIES, SHOPPING, OTHER
}

enum class ApprovalStatus {
    PENDING, APPROVED, REJECTED
}

data class ExpenseSplit(
    val id: String = "",
    val expenseId: String = "",
    val userId: String = "",
    val amountOwed: Double = 0.0,
    val percentage: Double? = null,
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val approvedAt: String? = null
)

data class Expense(
    val id: String = "",
    val groupId: String? = null,
    val paidBy: String = "", // Harcamayı yapan kullanıcı ID
    val amount: Double = 0.0,
    val currency: Currency = Currency.TRY,
    val description: String = "",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val splitMethod: SplitMethod = SplitMethod.EQUAL,
    val dueDate: String? = null,
    val status: ApprovalStatus = ApprovalStatus.APPROVED,
    val createdAt: String = "",
    val date: String? = null,
    val splits: List<ExpenseSplit> = emptyList()
)
