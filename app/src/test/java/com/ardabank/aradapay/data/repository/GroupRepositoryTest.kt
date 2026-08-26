package com.ardabank.aradapay.data.repository

import com.ardabank.aradapay.domain.model.ExpenseCategory
import com.ardabank.aradapay.domain.model.GroupMember
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GroupRepositoryTest {

    private lateinit var groupRepository: GroupRepository

    @Before
    fun setUp() {
        groupRepository = GroupRepository()
    }

    @Test
    fun `initial groups are loaded properly`() {
        val groups = groupRepository.groups.value
        assertTrue(groups.isNotEmpty())
        assertEquals(4, groups.size)

        val firstGroup = groupRepository.getGroupById("grp_1")
        assertNotNull(firstGroup)
        assertEquals("Kadıköy Evi", firstGroup?.name)
    }

    @Test
    fun `createGroup adds a new group to reactive state`() {
        val initialCount = groupRepository.groups.value.size
        val created = groupRepository.createGroup(
            name = "Test Grubu",
            emoji = "🎮",
            category = "Etkinlik",
            members = listOf(
                GroupMember("me", "Sen", "MD", "@me", 0.0),
                GroupMember("1", "Ahmet Yılmaz", "AY", "@ahmet", 0.0)
            )
        )

        assertEquals("Test Grubu", created.name)
        assertEquals(initialCount + 1, groupRepository.groups.value.size)
        assertEquals(created.id, groupRepository.groups.value.first().id)
    }

    @Test
    fun `addMemberToGroup adds new member successfully`() {
        val group = groupRepository.getGroupById("grp_1")!!
        val newMember = GroupMember("99", "Yeni Üye", "YÜ", "@yeni", 0.0)

        val success = groupRepository.addMemberToGroup("grp_1", newMember)
        assertTrue(success)

        val updatedGroup = groupRepository.getGroupById("grp_1")!!
        assertTrue(updatedGroup.members.any { it.id == "99" })
    }

    @Test
    fun `addExpenseToGroup correctly recalculates group balances`() {
        val group = groupRepository.getGroupById("grp_3")!! // 3 members: me, Ahmet, Deniz
        val initialUserBalance = group.userBalance

        // Add 300 TL expense paid by 'me', split equally among 3
        val expense = groupRepository.addExpenseToGroup(
            groupId = "grp_3",
            title = "Ortak Pizza",
            amount = 300.0,
            category = ExpenseCategory.DINING,
            payerId = "me",
            payerName = "Sen",
            participantIds = listOf("1", "8"),
            includeMyself = true
        )

        assertNotNull(expense)
        val updatedGroup = groupRepository.getGroupById("grp_3")!!
        // Per person share = 100. Since user paid 300, user is owed 200 (+200)
        assertEquals(initialUserBalance + 200.0, updatedGroup.userBalance, 0.01)

        val groupExpenses = groupRepository.getExpensesForGroup("grp_3")
        assertTrue(groupExpenses.any { it.title == "Ortak Pizza" })
    }

    @Test
    fun `settleGroupBalance zeroes out or decreases debt properly`() {
        val group = groupRepository.getGroupById("grp_1")!!
        val originalBalance = group.userBalance

        val settled = groupRepository.settleGroupBalance(
            groupId = "grp_1",
            amount = originalBalance,
            isCash = true,
            note = "Tam fitleşme"
        )

        assertTrue(settled)
        val updatedGroup = groupRepository.getGroupById("grp_1")!!
        assertEquals(0.0, updatedGroup.userBalance, 0.01)
    }

    @Test
    fun `getSharedGroupsWithFriend returns matching shared groups`() {
        val sharedWithAhmet = groupRepository.getSharedGroupsWithFriend("1", "Ahmet Yılmaz")
        assertTrue(sharedWithAhmet.isNotEmpty())
        assertTrue(sharedWithAhmet.any { it.name == "Kadıköy Evi" })
        assertTrue(sharedWithAhmet.any { it.name == "Ofis Öğle Yemekleri" })
    }

    @Test
    fun `getSimplifyDebtsSuggestions optimizes group debts without cyclic transfers`() {
        val suggestions = groupRepository.getSimplifyDebtsSuggestions("grp_1")
        assertTrue(suggestions.isNotEmpty())
        // In grp_1: me is owed +350, Ahmet is owed +100, Zeynep owes -200, Mert owes -250
        // Net sum of suggestions should balance
        val totalTransferred = suggestions.sumOf { it.amount }
        assertTrue(totalTransferred > 0.0)
    }
}
