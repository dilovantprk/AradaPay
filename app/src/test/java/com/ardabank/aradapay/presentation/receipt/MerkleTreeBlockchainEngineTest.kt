package com.ardabank.aradapay.presentation.receipt

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MerkleTreeBlockchainEngineTest {

    private val sampleReceipt = AradaPayReceipt(
        receiptId = "rec_chain_01",
        referenceNo = "AP-FAST-2026-778899",
        type = ReceiptType.FAST_TRANSFER,
        title = "Ortak Harcama Blockchain Kaydı",
        totalAmount = 500.0,
        date = "22.08.2026",
        time = "16:00",
        senderName = "Mehmet Dilovan",
        receiverName = "Ahmet Yılmaz"
    )

    @Test
    fun `generateBlockchainAnchor should produce valid txHash and MerkleRoot`() {
        val anchor = MerkleTreeBlockchainEngine.generateBlockchainAnchor(sampleReceipt)

        assertTrue(anchor.txHash.startsWith("0x"))
        assertTrue(anchor.merkleRoot.startsWith("0x"))
        assertEquals("0,00 ₺ (Maliyetsiz L2 Merkle Rollup)", anchor.gasFee)
        assertEquals(2, anchor.merkleProof.size)
    }

    @Test
    fun `verifyMerkleProof should return true for authentic zero-gas blockchain anchor`() {
        val anchor = MerkleTreeBlockchainEngine.generateBlockchainAnchor(sampleReceipt)

        val isValid = MerkleTreeBlockchainEngine.verifyMerkleProof(
            txHash = anchor.txHash,
            merkleRoot = anchor.merkleRoot,
            proof = anchor.merkleProof
        )

        assertTrue("Merkle proof verification must succeed mathematically without gas", isValid)
    }

    @Test
    fun `tampering with transaction payload must fail Merkle proof verification`() {
        val anchor = MerkleTreeBlockchainEngine.generateBlockchainAnchor(sampleReceipt)
        val tamperedTxHash = "0xdeadbeef1234567890abcdef1234567890abcdef1234567890abcdef12345678"

        val isValid = MerkleTreeBlockchainEngine.verifyMerkleProof(
            txHash = tamperedTxHash,
            merkleRoot = anchor.merkleRoot,
            proof = anchor.merkleProof
        )

        assertFalse("Tampered tx hash must be rejected by Merkle tree", isValid)
    }
}
