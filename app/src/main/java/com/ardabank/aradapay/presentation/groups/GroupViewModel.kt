package com.ardabank.aradapay.presentation.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardabank.aradapay.domain.model.CrossSettlementStep
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.GroupExpenseItem
import com.ardabank.aradapay.domain.model.GroupMember
import com.ardabank.aradapay.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    val groupRepository: GroupRepository
) : ViewModel() {

    val groups: StateFlow<List<Group>> = groupRepository.groups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groupExpenses: StateFlow<Map<String, List<GroupExpenseItem>>> = groupRepository.groupExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun getGroupById(groupId: String): Group? {
        return groupRepository.getGroupById(groupId)
    }

    fun getExpensesForGroup(groupId: String): List<GroupExpenseItem> {
        return groupRepository.getExpensesForGroup(groupId)
    }

    fun createGroup(
        name: String,
        emoji: String,
        category: String,
        members: List<GroupMember>
    ): Group {
        return groupRepository.createGroup(name, emoji, category, members)
    }

    fun addMemberToGroup(groupId: String, member: GroupMember): Boolean {
        return groupRepository.addMemberToGroup(groupId, member)
    }

    fun addExpenseToGroup(
        groupId: String,
        title: String,
        amount: Double,
        category: ExpenseCategory,
        payerId: String = "me",
        payerName: String = "Sen",
        participantIds: List<String> = emptyList(),
        includeMyself: Boolean = true
    ): GroupExpenseItem? {
        return groupRepository.addExpenseToGroup(
            groupId = groupId,
            title = title,
            amount = amount,
            category = category,
            payerId = payerId,
            payerName = payerName,
            participantIds = participantIds,
            includeMyself = includeMyself
        )
    }

    fun settleGroupBalance(
        groupId: String,
        amount: Double,
        isCash: Boolean,
        note: String
    ): Boolean {
        return groupRepository.settleGroupBalance(groupId, amount, isCash, note)
    }

    fun getSharedGroupsWithFriend(friendId: String, friendName: String): List<Group> {
        return groupRepository.getSharedGroupsWithFriend(friendId, friendName)
    }

    fun getSimplifyDebtsSuggestions(groupId: String): List<CrossSettlementStep> {
        return groupRepository.getSimplifyDebtsSuggestions(groupId)
    }

    fun updateGroup(
        groupId: String,
        name: String,
        emoji: String,
        category: String
    ): Boolean {
        return groupRepository.updateGroup(groupId, name, emoji, category)
    }

    fun removeMemberFromGroup(groupId: String, memberId: String): Boolean {
        return groupRepository.removeMemberFromGroup(groupId, memberId)
    }

    fun deleteGroup(groupId: String): Boolean {
        return groupRepository.deleteGroup(groupId)
    }

    fun archiveGroup(groupId: String): Boolean {
        return groupRepository.archiveGroup(groupId)
    }

    fun unarchiveGroup(groupId: String): Boolean {
        return groupRepository.unarchiveGroup(groupId)
    }
}
