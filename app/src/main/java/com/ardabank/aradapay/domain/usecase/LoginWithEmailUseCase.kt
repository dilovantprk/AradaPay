package com.ardabank.aradapay.domain.usecase

import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, pass: String): Result<User> {
        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank()) {
            return Result.failure(IllegalArgumentException("E-posta adresi boş bırakılamaz."))
        }
        if (!trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Lütfen geçerli bir e-posta adresi girin."))
        }
        if (pass.isBlank()) {
            return Result.failure(IllegalArgumentException("Şifre boş bırakılamaz."))
        }
        return authRepository.signInWithEmail(trimmedEmail, pass)
    }
}
