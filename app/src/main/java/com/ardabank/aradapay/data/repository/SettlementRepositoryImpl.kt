package com.ardabank.aradapay.data.repository

import com.ardabank.aradapay.data.remote.FirestoreService
import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Nudge
import com.ardabank.aradapay.domain.model.Settlement
import com.ardabank.aradapay.domain.repository.SettlementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class SettlementRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : SettlementRepository {

    override val settlementsFlow: Flow<List<Settlement>> = firestoreService.getSettlementsFlow()

    override fun getNudgesFlow(userId: String): Flow<List<Nudge>> {
        return firestoreService.getNudgesFlow(userId)
    }

    override suspend fun addSettlement(settlement: Settlement): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.addSettlement(settlement) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Fitleşme kaydedilemedi")))
            }
        }
    }

    override suspend fun updateSettlementStatus(settlementId: String, status: ApprovalStatus): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.updateSettlementStatus(settlementId, status) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Fitleşme durumu güncellenemedi")))
            }
        }
    }

    override suspend fun sendNudge(nudge: Nudge): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.sendNudge(nudge) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Dürtme gönderilemedi")))
            }
        }
    }
}
