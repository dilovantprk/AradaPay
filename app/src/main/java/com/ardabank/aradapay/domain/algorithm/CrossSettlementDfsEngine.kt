package com.ardabank.aradapay.domain.algorithm

import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.CrossSettlementOffer
import com.ardabank.aradapay.domain.model.CrossSettlementParticipant
import com.ardabank.aradapay.domain.model.CrossSettlementStep
import com.ardabank.aradapay.domain.model.User
import java.util.UUID

object CrossSettlementDfsEngine {

    /**
     * Scans for directed debt cycles using DFS algorithm (e.g. A -> B -> C -> A).
     * Returns a list of CrossSettlementOffer detected.
     */
    fun detectCrossSettlementCycles(
        usersMap: Map<String, User>,
        pairwiseMatrix: Map<Pair<String, String>, Double>
    ): List<CrossSettlementOffer> {
        val userIds = usersMap.keys.toList()
        val graph = mutableMapOf<String, MutableMap<String, Double>>()

        // Build directed adjacency list: graph[u][v] = amount (u owes v)
        pairwiseMatrix.forEach { (pair, amount) ->
            if (amount > 0.01) {
                val (from, to) = pair
                graph.getOrPut(from) { mutableMapOf() }[to] = amount
            }
        }

        val visited = mutableSetOf<String>()
        val recursionStack = mutableListOf<String>()
        val detectedCycles = mutableListOf<List<String>>()

        fun dfs(current: String) {
            visited.add(current)
            recursionStack.add(current)

            val neighbors = graph[current] ?: emptyMap()
            for ((nextUser, _) in neighbors) {
                if (recursionStack.contains(nextUser)) {
                    // Cycle detected!
                    val cycleStartIndex = recursionStack.indexOf(nextUser)
                    val cycle = recursionStack.subList(cycleStartIndex, recursionStack.size).toList()
                    if (cycle.size >= 3 && !isDuplicateCycle(detectedCycles, cycle)) {
                        detectedCycles.add(cycle)
                    }
                } else if (!visited.contains(nextUser)) {
                    dfs(nextUser)
                }
            }

            recursionStack.removeAt(recursionStack.size - 1)
        }

        for (u in userIds) {
            if (!visited.contains(u)) {
                dfs(u)
            }
        }

        // Convert raw cycle node lists into CrossSettlementOffer domain models
        val offers = mutableListOf<CrossSettlementOffer>()

        for (cycle in detectedCycles) {
            val steps = mutableListOf<CrossSettlementStep>()
            var minCapacity = Double.MAX_VALUE

            for (i in cycle.indices) {
                val fromId = cycle[i]
                val toId = cycle[(i + 1) % cycle.size]
                val owedAmount = graph[fromId]?.get(toId) ?: 0.0

                if (owedAmount < minCapacity) {
                    minCapacity = owedAmount
                }

                val fromUser = usersMap[fromId]
                val toUser = usersMap[toId]

                steps.add(
                    CrossSettlementStep(
                        fromUserId = fromId,
                        fromUserName = fromUser?.fullName ?: (fromUser?.username ?: fromId),
                        toUserId = toId,
                        toUserName = toUser?.fullName ?: (toUser?.username ?: toId),
                        amount = 0.0 // Set after bottleneck minCapacity is finalized
                    )
                )
            }

            if (minCapacity > 0.01) {
                val finalizedSteps = steps.map { it.copy(amount = minCapacity) }
                val participants = cycle.mapNotNull { uid ->
                    usersMap[uid]?.let { u ->
                        CrossSettlementParticipant(
                            id = u.id,
                            name = u.fullName,
                            avatar = u.avatarUrl,
                            username = u.username
                        )
                    }
                }

                val initialApprovals = cycle.associateWith { false }

                offers.add(
                    CrossSettlementOffer(
                        id = UUID.randomUUID().toString(),
                        cycleAmount = minCapacity,
                        participants = participants,
                        steps = finalizedSteps,
                        approvals = initialApprovals,
                        status = ApprovalStatus.PENDING,
                        createdAt = System.currentTimeMillis().toString()
                    )
                )
            }
        }

        return offers
    }

    private fun isDuplicateCycle(
        existingCycles: List<List<String>>,
        newCycle: List<String>
    ): Boolean {
        val newSet = newCycle.toSet()
        return existingCycles.any { existing ->
            existing.toSet() == newSet && existing.size == newCycle.size
        }
    }
}
