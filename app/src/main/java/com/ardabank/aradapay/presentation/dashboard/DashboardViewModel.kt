package com.ardabank.aradapay.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.Nudge
import com.ardabank.aradapay.domain.repository.AuthRepository
import com.ardabank.aradapay.domain.repository.ExpenseRepository
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.ardabank.aradapay.domain.repository.SettlementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardFinancialSummary(
    val netBalance: Double = 0.0,
    val alacakTotal: Double = 0.0,
    val borcTotal: Double = 0.0
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val expenseRepository: ExpenseRepository,
    private val settlementRepository: SettlementRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUserId: String
        get() = authRepository.currentUserId ?: "me"

    val groups: StateFlow<List<Group>> = groupRepository.groups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val financialSummary: StateFlow<DashboardFinancialSummary> = groups.combine(expenseRepository.expensesFlow) { groupList, expenseList ->
        var alacak = 0.0
        var borc = 0.0

        // Calculate from groups
        groupList.forEach { group ->
            if (group.userBalance > 0) {
                alacak += group.userBalance
            } else if (group.userBalance < 0) {
                borc += kotlin.math.abs(group.userBalance)
            }
        }

        // Calculate from individual expenses
        val myId = currentUserId
        expenseList.forEach { expense ->
            if (expense.paidBy == myId) {
                // I paid, others owe me
                val othersOwed = expense.splits.filter { it.userId != myId && it.status != ApprovalStatus.REJECTED }.sumOf { it.amountOwed }
                alacak += othersOwed
            } else {
                // Someone else paid, do I owe?
                val mySplit = expense.splits.find { it.userId == myId && it.status != ApprovalStatus.REJECTED }
                if (mySplit != null) {
                    borc += mySplit.amountOwed
                }
            }
        }

        val net = alacak - borc
        DashboardFinancialSummary(
            netBalance = net,
            alacakTotal = alacak,
            borcTotal = borc
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardFinancialSummary())

    val nudges: StateFlow<List<Nudge>> = settlementRepository.getNudgesFlow(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingExpenses: StateFlow<List<Expense>> = expenseRepository.expensesFlow.combine(groupRepository.groups) { expenses, _ ->
        val myId = currentUserId
        expenses.filter { expense ->
            expense.paidBy != myId && expense.splits.any { it.userId == myId && it.status == ApprovalStatus.PENDING }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveExpense(expenseId: String) {
        viewModelScope.launch {
            expenseRepository.updateExpenseStatus(expenseId, ApprovalStatus.APPROVED)
        }
    }

    fun rejectExpense(expenseId: String) {
        viewModelScope.launch {
            expenseRepository.updateExpenseStatus(expenseId, ApprovalStatus.REJECTED)
        }
    }
}
