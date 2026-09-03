package com.ardabank.aradapay.domain.repository

import com.ardabank.aradapay.domain.model.CrossSettlementStep
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.GroupExpenseItem
import com.ardabank.aradapay.domain.model.GroupMember
import kotlinx.coroutines.flow.StateFlow

interface GroupRepository {
    val groups: StateFlow<List<Group>>
    val groupExpenses: StateFlow<Map<String, List<GroupExpenseItem>>>

    fun getGroupById(groupId: String): Group?
    fun getExpensesForGroup(groupId: String): List<GroupExpenseItem>

    fun createGroup(
        name: String,
        emoji: String,
        category: String,
        members: List<GroupMember>
    ): Group

    fun addMemberToGroup(groupId: String, member: GroupMember): Boolean

    fun addExpenseToGroup(
        groupId: String,
        title: String,
        amount: Double,
        category: ExpenseCategory,
        payerId: String = "me",
        payerName: String = "Sen",
        participantIds: List<String> = emptyList(),
        includeMyself: Boolean = true
    ): GroupExpenseItem?

    fun settleGroupBalance(
        groupId: String,
        amount: Double,
        isCash: Boolean,
        note: String
    ): Boolean

    fun getSharedGroupsWithFriend(friendId: String, friendName: String): List<Group>

    fun getSimplifyDebtsSuggestions(groupId: String): List<CrossSettlementStep>

    fun updateGroup(
        groupId: String,
        name: String,
        emoji: String,
        category: String
    ): Boolean

    fun removeMemberFromGroup(groupId: String, memberId: String): Boolean

    fun deleteGroup(groupId: String): Boolean

    fun archiveGroup(groupId: String): Boolean

    fun unarchiveGroup(groupId: String): Boolean
}
