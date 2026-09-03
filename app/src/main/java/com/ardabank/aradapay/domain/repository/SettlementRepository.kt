package com.ardabank.aradapay.domain.repository

import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Nudge
import com.ardabank.aradapay.domain.model.Settlement
import kotlinx.coroutines.flow.Flow

interface SettlementRepository {
    val settlementsFlow: Flow<List<Settlement>>
    fun getNudgesFlow(userId: String): Flow<List<Nudge>>
    suspend fun addSettlement(settlement: Settlement): Result<Unit>
    suspend fun updateSettlementStatus(settlementId: String, status: ApprovalStatus): Result<Unit>
    suspend fun sendNudge(nudge: Nudge): Result<Unit>
}
