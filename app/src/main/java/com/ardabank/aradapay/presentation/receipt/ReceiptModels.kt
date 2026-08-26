package com.ardabank.aradapay.presentation.receipt

enum class ReceiptType(val title: String, val badge: String) {
    FAST_TRANSFER("FAST Para Transferi Dekontu", "FAST Transfer"),
    GROUP_EXPENSE("Grup Harcaması & Bölüşüm Dekontu", "Ortak Harcama"),
    CROSS_SETTLEMENT("Çapraz Fitleşme Dekontu", "Otomatik Mahsuplaşma"),
    PARTIAL_SETTLEMENT("Kısmi Ödeme Dekontu", "Ara Ödeme")
}

data class ReceiptParticipant(
    val name: String,
    val tag: String,
    val shareAmount: Double,
    val isPaid: Boolean
)

data class AradaPayReceipt(
    val receiptId: String,
    val referenceNo: String,
    val type: ReceiptType,
    val title: String,
    val totalAmount: Double,
    val date: String,
    val time: String,
    val senderName: String,
    val senderIban: String? = null,
    val receiverName: String,
    val receiverIban: String? = null,
    val category: String = "Genel",
    val participants: List<ReceiptParticipant> = emptyList(),
    val savingsAmount: Double? = null,
    val note: String? = null,
    val qrVerificationUrl: String = "https://aradapay.com/verify/receipt"
)
