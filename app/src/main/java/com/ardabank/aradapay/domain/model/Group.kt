package com.ardabank.aradapay.domain.model

data class GroupMember(
    val id: String = "",
    val name: String = "",
    val avatar: String = "",
    val tag: String = "",
    val balanceInGroup: Double = 0.0 // +: alacaklı, -: borçlu
)

data class GroupExpenseItem(
    val id: String = "",
    val groupId: String = "",
    val title: String = "",
    val totalAmount: Double = 0.0,
    val payerId: String = "",
    val payerName: String = "",
    val yourShare: Double = 0.0,
    val date: String = "",
    val category: ExpenseCategory = ExpenseCategory.OTHER,
    val isSettled: Boolean = false
)

data class Group(
    val id: String = "",
    val name: String = "",
    val emoji: String = "",
    val category: String = "Ev & Yaşam",
    val members: List<GroupMember> = emptyList(),
    val createdBy: String = "me",
    val createdAt: String = "",
    val userBalance: Double = 0.0, // +: grupta alacaklısın, -: grupta borçlusun
    val totalExpenses: Double = 0.0
)
