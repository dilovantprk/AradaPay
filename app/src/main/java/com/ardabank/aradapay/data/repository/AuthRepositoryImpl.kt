package com.ardabank.aradapay.data.repository

import com.ardabank.aradapay.data.preferences.SecurityPreferencesManager
import com.ardabank.aradapay.data.remote.FirebaseAuthService
import com.ardabank.aradapay.data.remote.FirestoreService
import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import com.ardabank.aradapay.domain.util.SecurityUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService,
    private val firestoreService: FirestoreService,
    private val securityPreferencesManager: SecurityPreferencesManager
) : AuthRepository {

    override val authStateFlow: Flow<String?> = firebaseAuthService.authStateFlow.map { it?.uid }

    override val currentUserFlow: Flow<User?> = firebaseAuthService.authStateFlow.flatMapLatest { firebaseUser ->
        if (firebaseUser != null) {
            firestoreService.getUserFlow(firebaseUser.uid)
        } else {
            flowOf(null)
        }
    }

    override val currentUserId: String?
        get() = firebaseAuthService.currentUserId

    override suspend fun signInWithEmail(email: String, pass: String): Result<User> {
        val authResult = firebaseAuthService.signInWithEmail(email, pass)
        return authResult.mapCatching { firebaseUser ->
            val existingUser = firestoreService.getUser(firebaseUser.uid)
            val user = existingUser ?: User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: email,
                username = firebaseUser.displayName ?: email.substringBefore("@"),
                fullName = firebaseUser.displayName ?: email.substringBefore("@"),
                tag = "@${email.substringBefore("@")}#${(1000..9999).random()}",
                defaultCurrency = Currency.TRY,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            ).also {
                firestoreService.saveUserSuspend(it)
            }

            val emoji = if (user.fullName.isNotBlank()) {
                val parts = user.fullName.trim().split(" ").filter { it.isNotBlank() }
                if (parts.size >= 2) "${parts[0].first()}${parts[1].first()}".uppercase()
                else user.fullName.take(2).uppercase()
            } else "AP"

            securityPreferencesManager.saveUserSession(
                name = user.fullName.ifBlank { user.username },
                iban = user.iban ?: "",
                avatarUrl = user.avatarUrl,
                avatarEmoji = emoji
            )
            user
        }
    }

    override suspend fun signUpWithEmail(
        fullName: String,
        email: String,
        pass: String,
        phone: String?,
        avatarUrl: String,
        pin: String
    ): Result<User> {
        val authResult = firebaseAuthService.signUpWithEmail(email, pass)
        return authResult.mapCatching { firebaseUser ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val firstName = fullName.trim().split(" ").firstOrNull() ?: fullName.trim()
            val tagSuffix = (1000..9999).random()
            val userTag = "@${firstName.lowercase()}#$tagSuffix"

            val newUser = User(
                id = firebaseUser.uid,
                email = email.trim(),
                username = firstName,
                fullName = fullName.trim(),
                avatarUrl = avatarUrl,
                phone = phone,
                iban = "TR64 0006 2000 0000 5566 7788 99",
                tag = userTag,
                defaultCurrency = Currency.TRY,
                pin = pin,
                createdAt = dateStr
            )

            firestoreService.saveUserSuspend(newUser).getOrThrow()

            if (pin.isNotBlank()) {
                securityPreferencesManager.setPin(pin)
            }

            val emoji = if (fullName.isNotBlank()) {
                val parts = fullName.trim().split(" ").filter { it.isNotBlank() }
                if (parts.size >= 2) "${parts[0].first()}${parts[1].first()}".uppercase()
                else fullName.take(2).uppercase()
            } else "AP"

            securityPreferencesManager.saveUserSession(
                name = fullName.trim(),
                iban = newUser.iban ?: "",
                avatarUrl = avatarUrl,
                avatarEmoji = emoji
            )

            newUser
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        val authResult = firebaseAuthService.signInWithGoogle(idToken)
        return authResult.mapCatching { firebaseUser ->
            val existing = firestoreService.getUser(firebaseUser.uid)
            val user = if (existing != null) {
                existing
            } else {
                val name = firebaseUser.displayName ?: "Google Kullanıcısı"
                val firstName = name.split(" ").firstOrNull() ?: name
                val tag = "@${firstName.lowercase()}#${(1000..9999).random()}"
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    username = firstName,
                    fullName = name,
                    avatarUrl = firebaseUser.photoUrl?.toString() ?: "",
                    phone = firebaseUser.phoneNumber,
                    iban = "TR64 0006 2000 0000 5566 7788 99",
                    tag = tag,
                    defaultCurrency = Currency.TRY,
                    createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                )
                firestoreService.saveUserSuspend(newUser).getOrThrow()
                newUser
            }

            val emoji = if (user.fullName.isNotBlank()) {
                val parts = user.fullName.trim().split(" ").filter { it.isNotBlank() }
                if (parts.size >= 2) "${parts[0].first()}${parts[1].first()}".uppercase()
                else user.fullName.take(2).uppercase()
            } else "AP"

            securityPreferencesManager.saveUserSession(
                name = user.fullName,
                iban = user.iban ?: "",
                avatarUrl = user.avatarUrl,
                avatarEmoji = emoji
            )

            user
        }
    }

    override suspend fun verifyPin(enteredPin: String): Result<Boolean> {
        return runCatching {
            val storedHash = securityPreferencesManager.pinHashFlow.firstOrNull()
            if (storedHash.isNullOrEmpty()) {
                // If no PIN stored yet, accept valid 4 digit PIN
                true
            } else {
                val isValid = SecurityUtils.verifyPin(enteredPin, storedHash)
                if (isValid) {
                    securityPreferencesManager.toggleLock(false)
                }
                isValid
            }
        }
    }

    override suspend fun savePin(pin: String): Result<Unit> = runCatching {
        securityPreferencesManager.setPin(pin)
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> {
        return firebaseAuthService.sendPasswordReset(email)
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        firebaseAuthService.signOut()
        securityPreferencesManager.clearSession()
    }

    override suspend fun getUserProfile(userId: String): User? {
        return firestoreService.getUser(userId)
    }

    override suspend fun saveUserProfile(user: User): Result<Unit> {
        return firestoreService.saveUserSuspend(user)
    }
}
