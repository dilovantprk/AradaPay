package com.ardabank.aradapay.domain.repository

import com.ardabank.aradapay.domain.model.User
import kotlinx.coroutines.flow.Flow

interface FriendRepository {
    fun getFriendsFlow(userId: String): Flow<List<User>>
    suspend fun findUserByTag(tag: String): User?
    suspend fun addFriend(currentUserId: String, friend: User): Result<Unit>
    suspend fun removeFriend(currentUserId: String, friendId: String): Result<Unit>
}
