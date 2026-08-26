package com.ardabank.aradapay.domain.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun testPinHashingAndVerification() {
        val pin = "1453"
        val hash = SecurityUtils.hashPin(pin)

        // SHA-256 should produce a 64-character hex string
        assertEquals(64, hash.length)
        assertTrue(SecurityUtils.verifyPin("1453", hash))
        assertFalse(SecurityUtils.verifyPin("9999", hash))
    }

    @Test
    fun testFinancialDataMasking() {
        val amount = 1250.50
        val unmasked = SecurityUtils.maskAmount(amount, "₺", isLocked = false)
        val masked = SecurityUtils.maskAmount(amount, "₺", isLocked = true)

        assertTrue(unmasked.contains("1250"))
        assertEquals("•••• ₺", masked)
    }
}
