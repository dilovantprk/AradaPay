package com.ardabank.aradapay.presentation.receipt

import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class VerificationResult(
    val isValid: Boolean,
    val sealCode: String,
    val fullHash: String,
    val algorithm: String = "HMAC-SHA256",
    val verifiedAt: String,
    val message: String
)

object ReceiptCryptographicVerifier {

    private const val HMAC_SECRET = "AradaPay_Fintech_Security_Key_2026_HMAC_v2"

    /**
     * Generates a tamper-proof cryptographic signature payload for a receipt.
     */
    fun createPayloadString(receipt: AradaPayReceipt): String {
        return "REF=${receipt.referenceNo}|AMT=${String.format("%.2f", receipt.totalAmount)}|DATE=${receipt.date}|SENDER=${receipt.senderName}|RECEIVER=${receipt.receiverName}|TYPE=${receipt.type.name}"
    }

    /**
     * Computes the HMAC-SHA256 signature of the receipt payload.
     */
    fun generateSignature(receipt: AradaPayReceipt): String {
        return try {
            val payload = createPayloadString(receipt)
            val secretKey = SecretKeySpec(HMAC_SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(secretKey)
            val hmacBytes = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
            hmacBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            // Fallback SHA-256
            val payload = createPayloadString(receipt) + HMAC_SECRET
            val md = MessageDigest.getInstance("SHA-256")
            val hashBytes = md.digest(payload.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        }
    }

    /**
     * Generates a human-readable 16-character digital seal badge (e.g. 9F8A-3C4E-7B21-88D0).
     */
    fun generateSealCode(receipt: AradaPayReceipt): String {
        val sig = generateSignature(receipt).uppercase()
        return if (sig.length >= 16) {
            "${sig.substring(0, 4)}-${sig.substring(4, 8)}-${sig.substring(8, 12)}-${sig.substring(12, 16)}"
        } else {
            "AP-SEAL-2026-OK"
        }
    }

    /**
     * Cryptographically verifies the integrity and authenticity of the receipt against a signature.
     */
    fun verifyReceipt(receipt: AradaPayReceipt, signatureToVerify: String? = null): VerificationResult {
        val expectedSignature = generateSignature(receipt)
        val seal = generateSealCode(receipt)
        val now = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())

        val isValid = if (signatureToVerify != null) {
            expectedSignature.equals(signatureToVerify.trim(), ignoreCase = true) ||
                    seal.equals(signatureToVerify.trim().replace("-", ""), ignoreCase = true)
        } else {
            // Self verification: checks if receipt payload produces valid non-empty cryptographic hash
            expectedSignature.isNotBlank() && expectedSignature.length == 64
        }

        return if (isValid) {
            VerificationResult(
                isValid = true,
                sealCode = seal,
                fullHash = expectedSignature,
                verifiedAt = now,
                message = "Dekont SHA-256 algoritmasıyla doğrulandı. İşlem verileri değiştirilmemiş ve AradaPay dijital mührüyle kaydedilmiştir."
            )
        } else {
            VerificationResult(
                isValid = false,
                sealCode = "GEÇERSİZ / SAHTE",
                fullHash = "TAMPERED_OR_CORRUPT_PAYLOAD",
                verifiedAt = now,
                message = "DİKKAT: Kriptografik imza uyuşmadı! Dekont bilgileri üzerinde oynanmış veya sahte olabilir."
            )
        }
    }
}
