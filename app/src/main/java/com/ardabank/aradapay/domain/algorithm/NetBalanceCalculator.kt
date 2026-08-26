package com.ardabank.aradapay.domain.algorithm

import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Expense
import com.ardabank.aradapay.domain.model.Settlement

data class UserFinancialSummary(
    val totalReceivable: Double, // Toplam alacak
    val totalPayable: Double,   // Toplam borç
    val netBalance: Double      // Net bakiye (Receivable - Payable)
)

object NetBalanceCalculator {

    /**
     * Calculates pairwise balance matrix between users.
     * Pairwise matrix[u][v] represents how much user 'u' owes user 'v'.
     */
    fun calculatePairwiseMatrix(
        userIds: List<String>,
        expenses: List<Expense>,
        settlements: List<Settlement>
    ): Map<Pair<String, String>, Double> {
        val pairwiseOwed = mutableMapOf<Pair<String, String>, Double>()

        // 1. Process Approved Expenses
        expenses.filter { it.status == ApprovalStatus.APPROVED }.forEach { expense ->
            val paidBy = expense.paidBy
            expense.splits.filter { it.status == ApprovalStatus.APPROVED }.forEach { split ->
                if (split.userId != paidBy && split.amountOwed > 0) {
                    val key = Pair(split.userId, paidBy) // split.userId owes paidBy
                    pairwiseOwed[key] = (pairwiseOwed[key] ?: 0.0) + split.amountOwed
                }
            }
        }

        // 2. Deduct Approved Settlements (Payments)
        settlements.filter { it.status == ApprovalStatus.APPROVED }.forEach { settlement ->
            val key = Pair(settlement.payerId, settlement.receiverId)
            val currentOwed = pairwiseOwed[key] ?: 0.0
            val remaining = currentOwed - settlement.amount
            if (remaining > 0) {
                pairwiseOwed[key] = remaining
            } else {
                pairwiseOwed.remove(key)
                if (remaining < 0) {
                    val reverseKey = Pair(settlement.receiverId, settlement.payerId)
                    pairwiseOwed[reverseKey] = (pairwiseOwed[reverseKey] ?: 0.0) + Math.abs(remaining)
                }
            }
        }

        // 3. Consolidate mutual debts (A owes B 100, B owes A 40 => A owes B 60)
        val consolidated = mutableMapOf<Pair<String, String>, Double>()
        val processedPairs = mutableSetOf<Pair<String, String>>()

        for (u in userIds) {
            for (v in userIds) {
                if (u == v) continue
                val pairUV = Pair(u, v)
                val pairVU = Pair(v, u)

                if (processedPairs.contains(pairUV) || processedPairs.contains(pairVU)) continue

                val owedUV = pairwiseOwed[pairUV] ?: 0.0
                val owedVU = pairwiseOwed[pairVU] ?: 0.0

                val netOwed = owedUV - owedVU
                if (netOwed > 0.01) {
                    consolidated[pairUV] = netOwed
                } else if (netOwed < -0.01) {
                    consolidated[pairVU] = -netOwed
                }

                processedPairs.add(pairUV)
                processedPairs.add(pairVU)
            }
        }

        return consolidated
    }

    /**
     * Calculates User Financial Summary (Total Receivable, Total Payable, Net Balance)
     */
    fun calculateUserFinancialSummary(
        currentUserId: String,
        allUserIds: List<String>,
        expenses: List<Expense>,
        settlements: List<Settlement>
    ): UserFinancialSummary {
        val matrix = calculatePairwiseMatrix(allUserIds, expenses, settlements)

        var totalPayable = 0.0   // What currentUserId owes others
        var totalReceivable = 0.0 // What others owe currentUserId

        matrix.forEach { (pair, amount) ->
            val (from, to) = pair
            if (from == currentUserId) {
                totalPayable += amount
            } else if (to == currentUserId) {
                totalReceivable += amount
            }
        }

        return UserFinancialSummary(
            totalReceivable = totalReceivable,
            totalPayable = totalPayable,
            netBalance = totalReceivable - totalPayable
        )
    }

    /**
     * Calculates direct balance between two users.
     * Positive => peer owes current user.
     * Negative => current user owes peer.
     */
    fun getPairwiseBalanceBetweenUsers(
        currentUserId: String,
        peerUserId: String,
        allUserIds: List<String>,
        expenses: List<Expense>,
        settlements: List<Settlement>
    ): Double {
        val matrix = calculatePairwiseMatrix(allUserIds, expenses, settlements)
        val peerOwesCurrent = matrix[Pair(peerUserId, currentUserId)] ?: 0.0
        val currentOwesPeer = matrix[Pair(currentUserId, peerUserId)] ?: 0.0
        return peerOwesCurrent - currentOwesPeer
    }
}
