package com.ardabank.aradapay.domain.usecase

import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        pass: String,
        confirmPass: String,
        phone: String? = null,
        avatarUrl: String = "",
        pin: String = ""
    ): Result<User> {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim()

        if (trimmedName.isBlank()) {
            return Result.failure(IllegalArgumentException("Lütfen adınızı ve soyadınızı girin."))
        }
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@") || !trimmedEmail.contains(".")) {
            return Result.failure(IllegalArgumentException("Lütfen geçerli bir e-posta adresi girin."))
        }
        if (pass.length < 6) {
            return Result.failure(IllegalArgumentException("Hesap şifreniz en az 6 karakter olmalıdır."))
        }
        if (pass != confirmPass) {
            return Result.failure(IllegalArgumentException("Hesap şifreleri birbiriyle eşleşmiyor."))
        }
        if (pin.isNotBlank() && (pin.length != 4 || !pin.all { it.isDigit() })) {
            return Result.failure(IllegalArgumentException("PIN kodu tam 4 haneli rakamlardan oluşmalıdır."))
        }

        return authRepository.signUpWithEmail(
            fullName = trimmedName,
            email = trimmedEmail,
            pass = pass,
            phone = phone,
            avatarUrl = avatarUrl,
            pin = pin
        )
    }
}
