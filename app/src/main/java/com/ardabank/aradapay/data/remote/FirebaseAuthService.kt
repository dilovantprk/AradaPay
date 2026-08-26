package com.ardabank.aradapay.data.remote

import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val currentUserId: String?
        get() = firebaseAuth.currentUser?.uid

    val authStateFlow: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUser> = runCatching {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
        authResult.user ?: throw IllegalStateException("Kullanıcı oturumu açılamadı")
    }

    suspend fun signUpWithEmail(fullName: String, email: String, pass: String, phone: String? = null): Result<FirebaseUser> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
        val user = authResult.user ?: throw IllegalStateException("Kullanıcı oluşturulamadı")
        
        // Update user profile in Firestore
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val tagSuffix = (1000..9999).random()
        val userTag = "${fullName.split(" ").first()}#$tagSuffix"
        val newUser = User(
            id = user.uid,
            email = email,
            username = fullName,
            fullName = fullName,
            phone = phone,
            tag = userTag,
            defaultCurrency = Currency.TRY,
            createdAt = dateStr
        )
        FirebaseFirestore.getInstance().collection("users").document(user.uid).set(newUser).await()
        user
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}
