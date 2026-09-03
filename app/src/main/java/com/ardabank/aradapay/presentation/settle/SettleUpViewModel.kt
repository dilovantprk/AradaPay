package com.ardabank.aradapay.presentation.settle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardabank.aradapay.domain.model.ApprovalStatus
import com.ardabank.aradapay.domain.model.Settlement
import com.ardabank.aradapay.domain.repository.AuthRepository
import com.ardabank.aradapay.domain.repository.GroupRepository
import com.ardabank.aradapay.domain.repository.SettlementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettleUpViewModel @Inject constructor(
    private val settlementRepository: SettlementRepository,
    private val groupRepository: GroupRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUserId: String
        get() = authRepository.currentUserId ?: "me"

    fun confirmSettlement(
        receiverId: String,
        amount: Double,
        note: String,
        isCash: Boolean,
        groupId: String? = null,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())

            // If it's for a specific group, update the group balance
            if (!groupId.isNullOrBlank()) {
                groupRepository.settleGroupBalance(groupId, amount, isCash, note)
            }

            val settlement = Settlement(
                id = "set_${System.currentTimeMillis()}",
                payerId = currentUserId,
                receiverId = receiverId,
                amount = amount,
                createdAt = dateFormat,
                status = ApprovalStatus.APPROVED,
                note = note
            )

            val result = settlementRepository.addSettlement(settlement)
            onComplete(result.isSuccess)
        }
    }
}
