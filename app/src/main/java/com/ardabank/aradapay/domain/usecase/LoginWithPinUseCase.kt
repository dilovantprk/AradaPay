package com.ardabank.aradapay.domain.usecase

import com.ardabank.aradapay.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithPinUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(enteredPin: String): Result<Boolean> {
        if (enteredPin.length != 4 || !enteredPin.all { it.isDigit() }) {
            return Result.failure(IllegalArgumentException("PIN kodu 4 haneli rakam olmalıdır."))
        }
        return authRepository.verifyPin(enteredPin)
    }
}
