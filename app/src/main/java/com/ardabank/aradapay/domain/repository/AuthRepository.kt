package com.ardabank.aradapay.domain.repository

import com.ardabank.aradapay.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val authStateFlow: Flow<String?>
    val currentUserFlow: Flow<User?>
    val currentUserId: String?

    suspend fun signInWithEmail(email: String, pass: String): Result<User>
    suspend fun signUpWithEmail(
        fullName: String,
        email: String,
        pass: String,
        phone: String? = null,
        avatarUrl: String = "",
        pin: String = ""
    ): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun verifyPin(enteredPin: String): Result<Boolean>
    suspend fun savePin(pin: String): Result<Unit>
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun signOut(): Result<Unit>
    suspend fun getUserProfile(userId: String): User?
    suspend fun saveUserProfile(user: User): Result<Unit>
}
