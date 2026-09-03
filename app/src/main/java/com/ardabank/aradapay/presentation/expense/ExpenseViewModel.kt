package com.ardabank.aradapay.presentation.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.ExpenseSplit
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.SplitMethod
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import com.ardabank.aradapay.domain.repository.ExpenseRepository
import com.ardabank.aradapay.domain.repository.FriendRepository
import com.ardabank.aradapay.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val groupRepository: GroupRepository,
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val expenses: StateFlow<List<Expense>> = expenseRepository.expensesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<Group>> = groupRepository.groups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentUserId: String
        get() = authRepository.currentUserId ?: "me"

    val friends: StateFlow<List<User>> = friendRepository.getFriendsFlow(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveExpense(
        amount: Double,
        description: String,
        category: ExpenseCategory,
        splitMethod: SplitMethod,
        selectedUserIds: List<String>,
        groupId: String? = null,
        payerId: String = "me",
        payerName: String = "Sen",
        includeMyself: Boolean = true,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val myId = currentUserId
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val expenseId = "exp_${System.currentTimeMillis()}"

            // If it's a group expense, add to group repository as well
            if (!groupId.isNullOrBlank()) {
                groupRepository.addExpenseToGroup(
                    groupId = groupId,
                    title = description,
                    amount = amount,
                    category = category,
                    payerId = payerId,
                    payerName = payerName,
                    participantIds = selectedUserIds,
                    includeMyself = includeMyself
                )
            }

            // Create individual splits
            val totalParticipants = (selectedUserIds + if (includeMyself) listOf(myId) else emptyList()).distinct()
            val participantCount = totalParticipants.size.coerceAtLeast(1)
            val sharePerPerson = amount / participantCount

            val splits = totalParticipants.map { userId ->
                ExpenseSplit(
                    id = "split_${System.currentTimeMillis()}_$userId",
                    expenseId = expenseId,
                    userId = userId,
                    amountOwed = sharePerPerson,
                    status = if (userId == myId) ApprovalStatus.APPROVED else ApprovalStatus.PENDING
                )
            }

            val expense = Expense(
                id = expenseId,
                groupId = groupId,
                paidBy = if (payerId == "me") myId else payerId,
                amount = amount,
                description = description,
                category = category,
                splitMethod = splitMethod,
                status = ApprovalStatus.APPROVED,
                createdAt = dateFormat,
                date = dateFormat,
                splits = splits
            )

            val result = expenseRepository.addExpense(expense)
            onComplete(result.isSuccess)
        }
    }

    fun deleteExpense(expenseId: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = expenseRepository.deleteExpense(expenseId)
            onComplete(result.isSuccess)
        }
    }

    fun updateExpenseStatus(expenseId: String, status: ApprovalStatus, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = expenseRepository.updateExpenseStatus(expenseId, status)
            onComplete(result.isSuccess)
        }
    }
}
