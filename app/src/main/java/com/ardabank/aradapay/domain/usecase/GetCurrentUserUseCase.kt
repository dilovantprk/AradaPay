package com.ardabank.aradapay.domain.usecase

import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.currentUserFlow
    }
}
