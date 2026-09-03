package com.ardabank.aradapay.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ardabank.aradapay.data.preferences.SecurityPreferencesManager
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.usecase.GetCurrentUserUseCase
import com.ardabank.aradapay.domain.usecase.LoginWithEmailUseCase
import com.ardabank.aradapay.domain.usecase.LoginWithGoogleUseCase
import com.ardabank.aradapay.domain.usecase.LoginWithPinUseCase
import com.ardabank.aradapay.domain.usecase.RegisterWithEmailUseCase
import com.ardabank.aradapay.domain.usecase.ResetPasswordUseCase
import com.ardabank.aradapay.domain.usecase.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

sealed interface PinAuthState {
    object Idle : PinAuthState
    object Loading : PinAuthState
    object Success : PinAuthState
    data class Error(val message: String) : PinAuthState
}

sealed interface ResetPasswordUiState {
    object Idle : ResetPasswordUiState
    object Loading : ResetPasswordUiState
    data class Success(val message: String) : ResetPasswordUiState
    data class Error(val message: String) : ResetPasswordUiState
}

sealed interface AuthNavigationEvent {
    data class NavigateToDashboard(val userName: String) : AuthNavigationEvent
    object NavigateToOnboarding : AuthNavigationEvent
    object NavigateToWelcome : AuthNavigationEvent
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginWithEmailUseCase: LoginWithEmailUseCase,
    private val registerWithEmailUseCase: RegisterWithEmailUseCase,
    private val loginWithPinUseCase: LoginWithPinUseCase,
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
    private val signOutUseCase: SignOutUseCase,
    val securityPreferencesManager: SecurityPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _pinAuthState = MutableStateFlow<PinAuthState>(PinAuthState.Idle)
    val pinAuthState: StateFlow<PinAuthState> = _pinAuthState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<ResetPasswordUiState>(ResetPasswordUiState.Idle)
    val resetPasswordState: StateFlow<ResetPasswordUiState> = _resetPasswordState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<AuthNavigationEvent>()
    val navigationEvents: SharedFlow<AuthNavigationEvent> = _navigationEvents.asSharedFlow()

    val currentUser: StateFlow<User?> = getCurrentUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val userNameFlow: StateFlow<String> = securityPreferencesManager.userNameFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "Kullanıcı"
        )

    val userIbanFlow: StateFlow<String> = securityPreferencesManager.userIbanFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val avatarUrlFlow: StateFlow<String> = securityPreferencesManager.avatarUrlFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    val avatarEmojiFlow: StateFlow<String> = securityPreferencesManager.avatarEmojiFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "AP"
        )

    val isPinEnabledFlow: StateFlow<Boolean> = securityPreferencesManager.isPinEnabledFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val isDataLockedFlow: StateFlow<Boolean> = securityPreferencesManager.isDataLockedFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun loginWithEmail(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginWithEmailUseCase(email, pass)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
                _navigationEvents.emit(AuthNavigationEvent.NavigateToDashboard(user.fullName.ifBlank { user.username }))
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(
                    exception.localizedMessage ?: "Giriş yapılırken bir hata oluştu."
                )
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        pass: String,
        confirmPass: String,
        phone: String? = null,
        avatarUrl: String = "",
        pin: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = registerWithEmailUseCase(
                fullName = fullName,
                email = email,
                pass = pass,
                confirmPass = confirmPass,
                phone = phone,
                avatarUrl = avatarUrl,
                pin = pin
            )
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
                _navigationEvents.emit(AuthNavigationEvent.NavigateToOnboarding)
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(
                    exception.localizedMessage ?: "Kayıt işlemi başarısız oldu."
                )
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = loginWithGoogleUseCase(idToken)
            result.onSuccess { user ->
                _uiState.value = AuthUiState.Success(user)
                _navigationEvents.emit(AuthNavigationEvent.NavigateToDashboard(user.fullName.ifBlank { user.username }))
            }.onFailure { exception ->
                _uiState.value = AuthUiState.Error(
                    exception.localizedMessage ?: "Google ile giriş yapılırken bir hata oluştu."
                )
            }
        }
    }

    fun verifyPin(pin: String) {
        viewModelScope.launch {
            _pinAuthState.value = PinAuthState.Loading
            val result = loginWithPinUseCase(pin)
            result.onSuccess { isValid ->
                if (isValid) {
                    _pinAuthState.value = PinAuthState.Success
                    val currentName = userNameFlow.value
                    _navigationEvents.emit(AuthNavigationEvent.NavigateToDashboard(currentName))
                } else {
                    _pinAuthState.value = PinAuthState.Error("Girilen PIN kodu hatalı. Lütfen tekrar deneyin.")
                }
            }.onFailure { exception ->
                _pinAuthState.value = PinAuthState.Error(
                    exception.localizedMessage ?: "PIN doğrulanırken bir hata oluştu."
                )
            }
        }
    }

    fun sendPasswordReset(email: String) {
        viewModelScope.launch {
            _resetPasswordState.value = ResetPasswordUiState.Loading
            val result = resetPasswordUseCase(email)
            result.onSuccess {
                _resetPasswordState.value = ResetPasswordUiState.Success(
                    "$email adresine şifre sıfırlama bağlantısı gönderildi."
                )
            }.onFailure { exception ->
                _resetPasswordState.value = ResetPasswordUiState.Error(
                    exception.localizedMessage ?: "Şifre sıfırlama isteği gönderilemedi."
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _uiState.value = AuthUiState.Idle
            _pinAuthState.value = PinAuthState.Idle
            _navigationEvents.emit(AuthNavigationEvent.NavigateToWelcome)
        }
    }

    fun toggleDataLock(isLocked: Boolean) {
        viewModelScope.launch {
            securityPreferencesManager.toggleLock(isLocked)
        }
    }

    fun clearErrors() {
        if (_uiState.value is AuthUiState.Error) {
            _uiState.value = AuthUiState.Idle
        }
        if (_pinAuthState.value is PinAuthState.Error) {
            _pinAuthState.value = PinAuthState.Idle
        }
        if (_resetPasswordState.value is ResetPasswordUiState.Error || _resetPasswordState.value is ResetPasswordUiState.Success) {
            _resetPasswordState.value = ResetPasswordUiState.Idle
        }
    }

    fun resetUiState() {
        _uiState.value = AuthUiState.Idle
        _pinAuthState.value = PinAuthState.Idle
        _resetPasswordState.value = ResetPasswordUiState.Idle
    }
}
