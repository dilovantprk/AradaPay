package com.ardabank.aradapay.domain.usecase

import com.ardabank.aradapay.domain.repository.AuthRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        val trimmed = email.trim()
        if (trimmed.isBlank() || !trimmed.contains("@") || !trimmed.contains(".")) {
            return Result.failure(IllegalArgumentException("Lütfen geçerli bir e-posta adresi girin."))
        }
        return authRepository.sendPasswordReset(trimmed)
    }
}
