package com.ardabank.aradapay.domain.algorithm

import com.ardabank.aradapay.domain.model.Currency
import java.util.PriorityQueue

data class SimplifiedTransaction(
    val debtorId: String,   // Who needs to pay
    val creditorId: String, // Who receives payment
    val amount: Double,
    val currency: Currency = Currency.TRY
)

object DebtSimplifierEngine {

    /**
     * Minimizes total transaction count using Greedy Flow Algorithm.
     * Takes net balance map (userId -> netBalance) and returns simplified list of transactions.
     */
    fun simplifyDebts(
        netBalances: Map<String, Double>,
        currency: Currency = Currency.TRY
    ): List<SimplifiedTransaction> {
        // Debtor PriorityQueue (largest negative balance first => max debt)
        val debtors = PriorityQueue<Pair<String, Double>>(compareBy { it.second })
        // Creditor PriorityQueue (largest positive balance first => max credit)
        val creditors = PriorityQueue<Pair<String, Double>>(compareByDescending { it.second })

        netBalances.forEach { (userId, balance) ->
            if (balance < -0.01) {
                debtors.add(Pair(userId, -balance)) // Store positive debt amount
            } else if (balance > 0.01) {
                creditors.add(Pair(userId, balance))
            }
        }

        val result = mutableListOf<SimplifiedTransaction>()

        while (debtors.isNotEmpty() && creditors.isNotEmpty()) {
            val debtor = debtors.poll()!!
            val creditor = creditors.poll()!!

            val minAmount = Math.min(debtor.second, creditor.second)
            result.add(
                SimplifiedTransaction(
                    debtorId = debtor.first,
                    creditorId = creditor.first,
                    amount = minAmount,
                    currency = currency
                )
            )

            val remainingDebtorBalance = debtor.second - minAmount
            val remainingCreditorBalance = creditor.second - minAmount

            if (remainingDebtorBalance > 0.01) {
                debtors.add(Pair(debtor.first, remainingDebtorBalance))
            }
            if (remainingCreditorBalance > 0.01) {
                creditors.add(Pair(creditor.first, remainingCreditorBalance))
            }
        }

        return result
    }
}
