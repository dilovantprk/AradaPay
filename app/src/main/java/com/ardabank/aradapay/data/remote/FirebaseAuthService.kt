package com.ardabank.aradapay.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
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
        val authResult = firebaseAuth.signInWithEmailAndPassword(email.trim(), pass).await()
        authResult.user ?: throw IllegalStateException("Kullanıcı oturumu açılamadı.")
    }

    suspend fun signUpWithEmail(email: String, pass: String): Result<FirebaseUser> = runCatching {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email.trim(), pass).await()
        authResult.user ?: throw IllegalStateException("Kullanıcı oluşturulamadı.")
    }

    suspend fun signInWithGoogle(idToken: String): Result<FirebaseUser> = runCatching {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        authResult.user ?: throw IllegalStateException("Google ile oturum açılamadı.")
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        firebaseAuth.sendPasswordResetEmail(email.trim()).await()
    }

    fun signOut() {
        firebaseAuth.signOut()
    }
}

