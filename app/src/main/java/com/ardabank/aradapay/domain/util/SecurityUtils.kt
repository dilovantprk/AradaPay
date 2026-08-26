package com.ardabank.aradapay.domain.util

import java.security.MessageDigest

object SecurityUtils {

    /**
     * Hashes 4-digit financial PIN using SHA-256 algorithm.
     */
    fun hashPin(pin: String): String {
        require(pin.length == 4 && pin.all { it.isDigit() }) { "PIN must be exactly 4 digits" }
        val bytes = MessageDigest.getInstance("SHA-256").digest(pin.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies entered PIN string against stored SHA-256 hash.
     */
    fun verifyPin(enteredPin: String, storedHash: String?): Boolean {
        if (storedHash.isNull_or_empty()) return true
        if (enteredPin.length != 4) return false
        return hashPin(enteredPin) == storedHash
    }

    /**
     * Masks financial amount string when 2FA PIN is locked.
     */
    fun maskAmount(amount: Double, currencySymbol: String = "₺", isLocked: Boolean): String {
        return if (isLocked) {
            "•••• $currencySymbol"
        } else {
            "%.2f $currencySymbol".format(amount)
        }
    }

    /**
     * Masks transaction description text when 2FA PIN is locked.
     */
    fun maskText(text: String, isLocked: Boolean): String {
        return if (isLocked) {
            "•".repeat(text.length.coerceAtLeast(6))
        } else {
            text
        }
    }

    private fun String?.isNull_or_empty(): Boolean = this == null || this.isEmpty()
}
