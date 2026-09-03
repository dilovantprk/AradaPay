package com.ardabank.aradapay.presentation.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardabank.aradapay.domain.model.Nudge
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import com.ardabank.aradapay.domain.repository.FriendRepository
import com.ardabank.aradapay.domain.repository.SettlementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendRepository: FriendRepository,
    private val authRepository: AuthRepository,
    private val settlementRepository: SettlementRepository
) : ViewModel() {

    val currentUserId: String
        get() = authRepository.currentUserId ?: "me"

    val friends: StateFlow<List<User>> = friendRepository.getFriendsFlow(currentUserId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchResult = MutableStateFlow<User?>(null)
    val searchResult: StateFlow<User?> = _searchResult.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    fun findUserByTag(tag: String, onResult: (User?) -> Unit = {}) {
        viewModelScope.launch {
            _isSearching.value = true
            val user = friendRepository.findUserByTag(tag)
            _searchResult.value = user
            _isSearching.value = false
            onResult(user)
        }
    }

    fun addFriend(friend: User, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = friendRepository.addFriend(currentUserId, friend)
            onComplete(result.isSuccess)
        }
    }

    fun removeFriend(friendId: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = friendRepository.removeFriend(currentUserId, friendId)
            onComplete(result.isSuccess)
        }
    }

    fun sendNudge(toUserId: String, message: String = "Fitleşme hatırlatması", onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            val nudge = Nudge(
                id = "nudge_${System.currentTimeMillis()}",
                fromUserId = currentUserId,
                toUserId = toUserId,
                message = message,
                createdAt = dateFormat,
                isRead = false
            )
            val result = settlementRepository.sendNudge(nudge)
            onComplete(result.isSuccess)
        }
    }
}
