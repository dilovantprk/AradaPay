package com.ardabank.aradapay.data.repository

import com.ardabank.aradapay.data.remote.FirestoreService
import com.ardabank.aradapay.domain.model.CrossSettlementStep
import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.Group
import com.ardabank.aradapay.domain.model.GroupExpenseItem
import com.ardabank.aradapay.domain.model.GroupMember
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class GroupRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService,
    private val auth: FirebaseAuth
) : GroupRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    override val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _groupExpenses = MutableStateFlow<Map<String, List<GroupExpenseItem>>>(emptyMap())
    override val groupExpenses: StateFlow<Map<String, List<GroupExpenseItem>>> = _groupExpenses.asStateFlow()

    init {
        // Real-time Firestore sync for groups
        repositoryScope.launch {
            firestoreService.getGroupsFlow().collect { firestoreGroups ->
                if (firestoreGroups.isNotEmpty() || _groups.value.isEmpty()) {
                    _groups.value = firestoreGroups
                    // Listen to expenses for each group
                    firestoreGroups.forEach { group ->
                        observeGroupExpenses(group.id)
                    }
                }
            }
        }
    }

    private fun observeGroupExpenses(groupId: String) {
        repositoryScope.launch {
            firestoreService.getGroupExpensesFlow(groupId).collect { expenses ->
                val currentMap = _groupExpenses.value.toMutableMap()
                currentMap[groupId] = expenses
                _groupExpenses.value = currentMap
            }
        }
    }

    override fun getGroupById(groupId: String): Group? {
        return _groups.value.find { it.id == groupId }
    }

    override fun getExpensesForGroup(groupId: String): List<GroupExpenseItem> {
        return _groupExpenses.value[groupId] ?: emptyList()
    }

    override fun createGroup(
        name: String,
        emoji: String,
        category: String,
        members: List<GroupMember>
    ): Group {
        val currentUserId = auth.currentUser?.uid ?: "me"
        val newGroup = Group(
            id = "grp_${System.currentTimeMillis()}",
            name = name.trim(),
            emoji = emoji,
            category = category,
            members = members,
            createdBy = currentUserId,
            createdAt = "Bugün",
            userBalance = 0.0,
            totalExpenses = 0.0,
            isArchived = false
        )
        val currentList = _groups.value.toMutableList()
        currentList.add(0, newGroup)
        _groups.value = currentList

        // Persist to Firestore
        firestoreService.saveGroup(newGroup) { success ->
            if (!success) {
                // If offline, Firestore cache will sync automatically
            }
        }
        observeGroupExpenses(newGroup.id)
        return newGroup
    }

    override fun addMemberToGroup(groupId: String, member: GroupMember): Boolean {
        val currentList = _groups.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == groupId }
        if (index != -1) {
            val group = currentList[index]
            if (group.members.none { it.id == member.id }) {
                val updatedMembers = group.members + member
                val updatedGroup = group.copy(members = updatedMembers)
                currentList[index] = updatedGroup
                _groups.value = currentList

                // Persist to Firestore
                firestoreService.saveGroup(updatedGroup) {}
                return true
            }
        }
        return false
    }

    override fun addExpenseToGroup(
        groupId: String,
        title: String,
        amount: Double,
        category: ExpenseCategory,
        payerId: String,
        payerName: String,
        participantIds: List<String>,
        includeMyself: Boolean
    ): GroupExpenseItem? {
        val currentGroups = _groups.value.toMutableList()
        val groupIndex = currentGroups.indexOfFirst { it.id == groupId }
        if (groupIndex == -1) return null

        val group = currentGroups[groupIndex]
        val targetMembers = if (participantIds.isEmpty()) {
            group.members
        } else {
            group.members.filter { it.id in participantIds || (includeMyself && it.id == "me") }
        }

        val participantCount = targetMembers.size.coerceAtLeast(1)
        val perPersonShare = amount / participantCount

        val isMePayer = payerId == "me" || payerId.isEmpty()
        val myShareInExpense = if (includeMyself) perPersonShare else 0.0
        val yourNetExpenseShare = if (isMePayer) {
            amount - myShareInExpense
        } else {
            -myShareInExpense
        }

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newExpenseItem = GroupExpenseItem(
            id = "ge_${System.currentTimeMillis()}",
            groupId = groupId,
            title = title,
            totalAmount = amount,
            payerId = if (isMePayer) "me" else payerId,
            payerName = if (isMePayer) "Sen ödedin" else "$payerName ödedi",
            yourShare = yourNetExpenseShare,
            date = "Bugün, $timeFormat",
            category = category,
            isSettled = false
        )

        // Update group members' balances
        val updatedMembers = group.members.map { member ->
            val isIncluded = member.id in participantIds || (includeMyself && member.id == "me")
            if (!isIncluded) {
                member
            } else {
                val memberShare = perPersonShare
                val newBal = if (member.id == payerId || (isMePayer && member.id == "me")) {
                    member.balanceInGroup + (amount - memberShare)
                } else {
                    member.balanceInGroup - memberShare
                }
                member.copy(balanceInGroup = newBal)
            }
        }

        val updatedUserBalance = updatedMembers.find { it.id == "me" }?.balanceInGroup ?: (group.userBalance + yourNetExpenseShare)

        val updatedGroup = group.copy(
            members = updatedMembers,
            userBalance = updatedUserBalance,
            totalExpenses = group.totalExpenses + amount
        )
        currentGroups[groupIndex] = updatedGroup
        _groups.value = currentGroups

        // Update group expenses map locally
        val currentExpensesMap = _groupExpenses.value.toMutableMap()
        val expensesList = (currentExpensesMap[groupId] ?: emptyList()).toMutableList()
        expensesList.add(0, newExpenseItem)
        currentExpensesMap[groupId] = expensesList
        _groupExpenses.value = currentExpensesMap

        // Persist to Firestore
        firestoreService.saveGroup(updatedGroup) {}
        firestoreService.addGroupExpense(groupId, newExpenseItem) {}

        return newExpenseItem
    }

    override fun settleGroupBalance(groupId: String, amount: Double, isCash: Boolean, note: String): Boolean {
        val currentGroups = _groups.value.toMutableList()
        val groupIndex = currentGroups.indexOfFirst { it.id == groupId }
        if (groupIndex == -1) return false

        val group = currentGroups[groupIndex]
        val currentBalance = group.userBalance
        val updatedBalance = if (currentBalance > 0) {
            (currentBalance - amount).coerceAtLeast(0.0)
        } else {
            (currentBalance + amount).coerceAtMost(0.0)
        }

        val updatedMembers = group.members.map { member ->
            if (member.id == "me") {
                member.copy(balanceInGroup = updatedBalance)
            } else {
                member.copy(balanceInGroup = 0.0)
            }
        }

        val updatedGroup = group.copy(
            members = updatedMembers,
            userBalance = updatedBalance
        )
        currentGroups[groupIndex] = updatedGroup
        _groups.value = currentGroups

        // Mark previous expenses as settled or add a settlement item
        val currentExpensesMap = _groupExpenses.value.toMutableMap()
        val expensesList = (currentExpensesMap[groupId] ?: emptyList()).map {
            it.copy(isSettled = true)
        }.toMutableList()

        val settlementItem = GroupExpenseItem(
            id = "ge_settle_${System.currentTimeMillis()}",
            groupId = groupId,
            title = if (isCash) "Nakit Fitleşme" else "FAST Fitleşme",
            totalAmount = amount,
            payerId = "me",
            payerName = "Fitleşildi",
            yourShare = 0.0,
            date = "Bugün, Şimdi",
            category = ExpenseCategory.OTHER,
            isSettled = true
        )
        expensesList.add(0, settlementItem)
        currentExpensesMap[groupId] = expensesList
        _groupExpenses.value = currentExpensesMap

        // Persist to Firestore
        firestoreService.saveGroup(updatedGroup) {}
        firestoreService.addGroupExpense(groupId, settlementItem) {}

        return true
    }

    override fun getSharedGroupsWithFriend(friendId: String, friendName: String): List<Group> {
        return _groups.value.filter { group ->
            group.members.any { member ->
                member.id == friendId ||
                (friendName.isNotBlank() && member.name.contains(friendName.split(" ").first(), ignoreCase = true))
            }
        }
    }

    override fun getSimplifyDebtsSuggestions(groupId: String): List<CrossSettlementStep> {
        val group = getGroupById(groupId) ?: return emptyList()
        val creditors = group.members.filter { it.balanceInGroup > 0 }.sortedByDescending { it.balanceInGroup }
        val debtors = group.members.filter { it.balanceInGroup < 0 }.sortedBy { it.balanceInGroup }

        val steps = mutableListOf<CrossSettlementStep>()
        var cIdx = 0
        var dIdx = 0

        val credBals = creditors.map { it.balanceInGroup }.toMutableList()
        val debBals = debtors.map { abs(it.balanceInGroup) }.toMutableList()

        while (cIdx < creditors.size && dIdx < debtors.size) {
            val c = creditors[cIdx]
            val d = debtors[dIdx]
            val cAmt = credBals[cIdx]
            val dAmt = debBals[dIdx]

            val transfer = minOf(cAmt, dAmt)
            if (transfer > 0.01) {
                steps.add(
                    CrossSettlementStep(
                        fromUserId = d.id,
                        fromUserName = d.name,
                        toUserId = c.id,
                        toUserName = c.name,
                        amount = transfer
                    )
                )
            }

            credBals[cIdx] -= transfer
            debBals[dIdx] -= transfer

            if (credBals[cIdx] < 0.01) cIdx++
            if (debBals[dIdx] < 0.01) dIdx++
        }

        return steps
    }

    override fun updateGroup(
        groupId: String,
        name: String,
        emoji: String,
        category: String
    ): Boolean {
        val current = _groups.value.toMutableList()
        val index = current.indexOfFirst { it.id == groupId }
        if (index != -1) {
            val updatedGroup = current[index].copy(
                name = name.trim(),
                emoji = emoji,
                category = category
            )
            current[index] = updatedGroup
            _groups.value = current

            firestoreService.saveGroup(updatedGroup) {}
            return true
        }
        return false
    }

    override fun removeMemberFromGroup(groupId: String, memberId: String): Boolean {
        val current = _groups.value.toMutableList()
        val index = current.indexOfFirst { it.id == groupId }
        if (index != -1) {
            val group = current[index]
            val updatedMembers = group.members.filter { it.id != memberId }
            val updatedGroup = group.copy(members = updatedMembers)
            current[index] = updatedGroup
            _groups.value = current

            firestoreService.saveGroup(updatedGroup) {}
            return true
        }
        return false
    }

    override fun deleteGroup(groupId: String): Boolean {
        val current = _groups.value.toMutableList()
        val index = current.indexOfFirst { it.id == groupId }
        if (index != -1) {
            current.removeAt(index)
            _groups.value = current

            firestoreService.deleteGroup(groupId) {}
            return true
        }
        return false
    }

    override fun archiveGroup(groupId: String): Boolean {
        val current = _groups.value.toMutableList()
        val index = current.indexOfFirst { it.id == groupId }
        if (index != -1) {
            val updatedGroup = current[index].copy(isArchived = true)
            current[index] = updatedGroup
            _groups.value = current

            firestoreService.saveGroup(updatedGroup) {}
            return true
        }
        return false
    }

    override fun unarchiveGroup(groupId: String): Boolean {
        val current = _groups.value.toMutableList()
        val index = current.indexOfFirst { it.id == groupId }
        if (index != -1) {
            val updatedGroup = current[index].copy(isArchived = false)
            current[index] = updatedGroup
            _groups.value = current

            firestoreService.saveGroup(updatedGroup) {}
            return true
        }
        return false
    }
}
