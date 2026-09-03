package com.ardabank.aradapay.domain.usecase

import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Google kimlik doğrulama belirteci alınamadı."))
        }
        return authRepository.signInWithGoogle(idToken)
    }
}
