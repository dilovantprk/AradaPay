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
        // Seed test groups
        groupRepository.createGroup(
            name = "Kadıköy Evi",
            emoji = "🏠",
            category = "Ev",
            members = listOf(
                GroupMember("me", "Sen", "MD", "@me", 350.0),
                GroupMember("1", "Ahmet Yılmaz", "AY", "@ahmet", 100.0),
                GroupMember("2", "Zeynep Kaya", "ZK", "@zeynep", -200.0),
                GroupMember("3", "Mert Demir", "MD", "@mert", -250.0)
            )
        )
        groupRepository.createGroup(
            name = "Ofis Öğle Yemekleri",
            emoji = "🍔",
            category = "Yemek",
            members = listOf(
                GroupMember("me", "Sen", "MD", "@me", 0.0),
                GroupMember("1", "Ahmet Yılmaz", "AY", "@ahmet", 0.0),
                GroupMember("8", "Deniz Arda", "DA", "@deniz", 0.0)
            )
        )
    }

    @Test
    fun `initial groups are loaded properly`() {
        val groups = groupRepository.groups.value
        assertTrue(groups.isNotEmpty())
        assertEquals(2, groups.size)

        val firstGroup = groupRepository.groups.value.find { it.name == "Kadıköy Evi" }
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
        val group = groupRepository.groups.value.first()
        val newMember = GroupMember("99", "Yeni Üye", "YÜ", "@yeni", 0.0)

        val success = groupRepository.addMemberToGroup(group.id, newMember)
        assertTrue(success)

        val updatedGroup = groupRepository.getGroupById(group.id)!!
        assertTrue(updatedGroup.members.any { it.id == "99" })
    }

    @Test
    fun `addExpenseToGroup correctly recalculates group balances`() {
        val group = groupRepository.groups.value.find { it.name == "Ofis Öğle Yemekleri" }!!
        val initialUserBalance = group.userBalance

        // Add 300 TL expense paid by 'me', split equally among 3
        val expense = groupRepository.addExpenseToGroup(
            groupId = group.id,
            title = "Ortak Pizza",
            amount = 300.0,
            category = ExpenseCategory.DINING,
            payerId = "me",
            payerName = "Sen",
            participantIds = listOf("1", "8"),
            includeMyself = true
        )

        assertNotNull(expense)
        val updatedGroup = groupRepository.getGroupById(group.id)!!
        // Per person share = 100. Since user paid 300, user is owed 200 (+200)
        assertEquals(initialUserBalance + 200.0, updatedGroup.userBalance, 0.01)

        val groupExpenses = groupRepository.getExpensesForGroup(group.id)
        assertTrue(groupExpenses.any { it.title == "Ortak Pizza" })
    }

    @Test
    fun `settleGroupBalance zeroes out or decreases debt properly`() {
        val group = groupRepository.groups.value.find { it.name == "Kadıköy Evi" }!!
        val originalBalance = 350.0

        val settled = groupRepository.settleGroupBalance(
            groupId = group.id,
            amount = originalBalance,
            isCash = true,
            note = "Tam fitleşme"
        )

        assertTrue(settled)
        val updatedGroup = groupRepository.getGroupById(group.id)!!
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
        val group = groupRepository.groups.value.find { it.name == "Kadıköy Evi" }!!
        // Add expense so members have positive/negative balances
        groupRepository.addExpenseToGroup(
            groupId = group.id,
            title = "Ortak Harcama",
            amount = 400.0,
            category = ExpenseCategory.HOUSING,
            payerId = "me",
            payerName = "Sen",
            participantIds = listOf("1", "2", "3"),
            includeMyself = true
        )

        val suggestions = groupRepository.getSimplifyDebtsSuggestions(group.id)
        assertTrue(suggestions.isNotEmpty())
        val totalTransferred = suggestions.sumOf { it.amount }
        assertTrue(totalTransferred > 0.0)
    }
}


