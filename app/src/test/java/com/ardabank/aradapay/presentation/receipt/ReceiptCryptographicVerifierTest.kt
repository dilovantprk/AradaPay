package com.ardabank.aradapay.presentation.receipt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReceiptCryptographicVerifierTest {

    private val sampleReceipt = AradaPayReceipt(
        receiptId = "rec_001",
        referenceNo = "AP-FAST-2026-981240",
        type = ReceiptType.FAST_TRANSFER,
        title = "Akşam Yemeği FAST Transferi",
        totalAmount = 450.0,
        date = "22.08.2026",
        time = "14:30",
        senderName = "Mehmet Dilovan",
        senderIban = "TR64 0006 2000 0000 1122 3344 55",
        receiverName = "Ahmet Yılmaz",
        receiverIban = "TR64 0006 2000 0000 9988 7766 55"
    )

    @Test
    fun `generateSignature should create a consistent 64 character SHA-256 HMAC hash`() {
        val sig1 = ReceiptCryptographicVerifier.generateSignature(sampleReceipt)
        val sig2 = ReceiptCryptographicVerifier.generateSignature(sampleReceipt)

        assertEquals(64, sig1.length)
        assertEquals(sig1, sig2)
    }

    @Test
    fun `generateSealCode should return 19 character formatted seal code`() {
        val seal = ReceiptCryptographicVerifier.generateSealCode(sampleReceipt)
        assertEquals(19, seal.length)
        assertTrue(seal.contains("-"))
    }

    @Test
    fun `verifyReceipt should return true for authentic receipt`() {
        val result = ReceiptCryptographicVerifier.verifyReceipt(sampleReceipt)
        assertTrue(result.isValid)
        assertEquals("HMAC-SHA256", result.algorithm)
    }

    @Test
    fun `tampering with total amount should invalidate the signature`() {
        val originalSig = ReceiptCryptographicVerifier.generateSignature(sampleReceipt)
        val tamperedReceipt = sampleReceipt.copy(totalAmount = 900.0)
        val tamperedSig = ReceiptCryptographicVerifier.generateSignature(tamperedReceipt)

        assertNotEquals(originalSig, tamperedSig)

        // Verifying tampered receipt against original signature must fail
        val verifyResult = ReceiptCryptographicVerifier.verifyReceipt(tamperedReceipt, originalSig)
        assertFalse(verifyResult.isValid)
    }

    @Test
    fun `tampering with sender name should change cryptographic seal`() {
        val originalSeal = ReceiptCryptographicVerifier.generateSealCode(sampleReceipt)
        val forgedReceipt = sampleReceipt.copy(senderName = "Hacker / Fake Sender")
        val forgedSeal = ReceiptCryptographicVerifier.generateSealCode(forgedReceipt)

        assertNotEquals(originalSeal, forgedSeal)
    }
}
