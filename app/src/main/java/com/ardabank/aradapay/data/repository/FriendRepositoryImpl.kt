package com.ardabank.aradapay.data.repository

import com.ardabank.aradapay.data.remote.FirestoreService
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.FriendRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Singleton
class FriendRepositoryImpl @Inject constructor(
    private val firestoreService: FirestoreService
) : FriendRepository {

    override fun getFriendsFlow(userId: String): Flow<List<User>> {
        return firestoreService.getFriendsFlow(userId)
    }

    override suspend fun findUserByTag(tag: String): User? = suspendCoroutine { continuation ->
        firestoreService.findUserByTag(tag) { user ->
            continuation.resume(user)
        }
    }

    override suspend fun addFriend(currentUserId: String, friend: User): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.syncFriendWithFirestore(currentUserId, friend) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Arkadaş eklenemedi")))
            }
        }
    }

    override suspend fun removeFriend(currentUserId: String, friendId: String): Result<Unit> = suspendCoroutine { continuation ->
        firestoreService.deleteFriend(currentUserId, friendId) { success ->
            if (success) {
                continuation.resume(Result.success(Unit))
            } else {
                continuation.resume(Result.failure(Exception("Arkadaş silinemedi")))
            }
        }
    }
}
