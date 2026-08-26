package com.ardabank.aradapay.domain.model

data class Settlement(
    val id: String = "",
    val payerId: String = "", // Ödemeyi yapan (borçlu)
    val receiverId: String = "", // Ödemeyi alan (alacaklı)
    val amount: Double = 0.0,
    val currency: Currency = Currency.TRY,
    val createdAt: String = "",
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val note: String? = null
)

data class CrossSettlementStep(
    val fromUserId: String = "",
    val fromUserName: String = "",
    val toUserId: String = "",
    val toUserName: String = "",
    val amount: Double = 0.0
)

data class CrossSettlementParticipant(
    val id: String = "",
    val name: String = "",
    val avatar: String = "",
    val username: String = ""
)

data class CrossSettlementOffer(
    val id: String = "",
    val cycleAmount: Double = 0.0, // Sıfırlanacak borç tutarı
    val participants: List<CrossSettlementParticipant> = emptyList(),
    val steps: List<CrossSettlementStep> = emptyList(),
    val approvals: Map<String, Boolean> = emptyMap(), // userId -> approved
    val status: ApprovalStatus = ApprovalStatus.PENDING,
    val createdAt: String = ""
)

data class Nudge(
    val id: String = "",
    val fromUserId: String = "",
    val toUserId: String = "",
    val expenseId: String? = null,
    val message: String = "",
    val createdAt: String = "",
    val isRead: Boolean = false
)
