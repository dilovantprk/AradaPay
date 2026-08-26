package com.ardabank.aradapay.domain.algorithm

import com.ardabank.aradapay.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrossSettlementDfsEngineTest {

    @Test
    fun testDetectCrossSettlementCycle_ThreeUsersLoop() {
        // Setup Users A, B, C
        val userA = User(id = "userA", username = "Ahmet", fullName = "Ahmet Yılmaz")
        val userB = User(id = "userB", username = "Mehmet", fullName = "Mehmet Demir")
        val userC = User(id = "userC", username = "Ayşe", fullName = "Ayşe Kaya")

        val usersMap = mapOf(
            "userA" to userA,
            "userB" to userB,
            "userC" to userC
        )

        // Matrix: A owes B 250 TL, B owes C 300 TL, C owes A 200 TL
        val pairwiseMatrix = mapOf(
            Pair("userA", "userB") to 250.0,
            Pair("userB", "userC") to 300.0,
            Pair("userC", "userA") to 200.0
        )

        val offers = CrossSettlementDfsEngine.detectCrossSettlementCycles(usersMap, pairwiseMatrix)

        assertEquals(1, offers.size)
        val offer = offers.first()

        // Bottleneck capacity should be min(250, 300, 200) = 200 TL
        assertEquals(200.0, offer.cycleAmount, 0.01)
        assertEquals(3, offer.participants.size)
        assertEquals(3, offer.steps.size)

        // All steps in the offer should have amount = 200.0
        offer.steps.forEach { step ->
            assertEquals(200.0, step.amount, 0.01)
        }
    }
}
