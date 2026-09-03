package com.ardabank.aradapay.domain.usecase

import com.ardabank.aradapay.domain.model.Currency
import com.ardabank.aradapay.domain.model.User
import com.ardabank.aradapay.domain.repository.AuthRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AuthUseCasesTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var loginWithEmailUseCase: LoginWithEmailUseCase
    private lateinit var registerWithEmailUseCase: RegisterWithEmailUseCase
    private lateinit var loginWithPinUseCase: LoginWithPinUseCase
    private lateinit var loginWithGoogleUseCase: LoginWithGoogleUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase
    private lateinit var signOutUseCase: SignOutUseCase

    private val sampleUser = User(
        id = "user_123",
        email = "test@example.com",
        username = "TestUser",
        fullName = "Test User",
        tag = "@test#1234",
        defaultCurrency = Currency.TRY
    )

    @Before
    fun setUp() {
        authRepository = mock()
        loginWithEmailUseCase = LoginWithEmailUseCase(authRepository)
        registerWithEmailUseCase = RegisterWithEmailUseCase(authRepository)
        loginWithPinUseCase = LoginWithPinUseCase(authRepository)
        loginWithGoogleUseCase = LoginWithGoogleUseCase(authRepository)
        getCurrentUserUseCase = GetCurrentUserUseCase(authRepository)
        resetPasswordUseCase = ResetPasswordUseCase(authRepository)
        signOutUseCase = SignOutUseCase(authRepository)
    }

    // --- LoginWithEmailUseCase Tests ---
    @Test
    fun `loginWithEmail fails on empty email`() = runTest {
        val result = loginWithEmailUseCase("", "password123")
        assertTrue(result.isFailure)
        assertEquals("E-posta adresi boş bırakılamaz.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `loginWithEmail fails on invalid email format`() = runTest {
        val result = loginWithEmailUseCase("invalid-email", "password123")
        assertTrue(result.isFailure)
        assertEquals("Lütfen geçerli bir e-posta adresi girin.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `loginWithEmail fails on empty password`() = runTest {
        val result = loginWithEmailUseCase("test@example.com", "")
        assertTrue(result.isFailure)
        assertEquals("Şifre boş bırakılamaz.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `loginWithEmail delegates to repository on valid inputs`() = runTest {
        whenever(authRepository.signInWithEmail("test@example.com", "password123"))
            .thenReturn(Result.success(sampleUser))

        val result = loginWithEmailUseCase("test@example.com", "password123")
        assertTrue(result.isSuccess)
        assertEquals(sampleUser, result.getOrNull())
        verify(authRepository).signInWithEmail("test@example.com", "password123")
    }

    // --- RegisterWithEmailUseCase Tests ---
    @Test
    fun `registerWithEmail fails on blank name`() = runTest {
        val result = registerWithEmailUseCase(
            fullName = "",
            email = "test@example.com",
            pass = "123456",
            confirmPass = "123456"
        )
        assertTrue(result.isFailure)
        assertEquals("Lütfen adınızı ve soyadınızı girin.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `registerWithEmail fails on short password`() = runTest {
        val result = registerWithEmailUseCase(
            fullName = "Test User",
            email = "test@example.com",
            pass = "123",
            confirmPass = "123"
        )
        assertTrue(result.isFailure)
        assertEquals("Hesap şifreniz en az 6 karakter olmalıdır.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `registerWithEmail fails on password mismatch`() = runTest {
        val result = registerWithEmailUseCase(
            fullName = "Test User",
            email = "test@example.com",
            pass = "123456",
            confirmPass = "654321"
        )
        assertTrue(result.isFailure)
        assertEquals("Hesap şifreleri birbiriyle eşleşmiyor.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `registerWithEmail fails on invalid pin`() = runTest {
        val result = registerWithEmailUseCase(
            fullName = "Test User",
            email = "test@example.com",
            pass = "123456",
            confirmPass = "123456",
            pin = "12a"
        )
        assertTrue(result.isFailure)
        assertEquals("PIN kodu tam 4 haneli rakamlardan oluşmalıdır.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `registerWithEmail delegates to repository on valid inputs`() = runTest {
        whenever(
            authRepository.signUpWithEmail(
                fullName = "Test User",
                email = "test@example.com",
                pass = "123456",
                phone = "+905551112233",
                avatarUrl = "avatar.png",
                pin = "1234"
            )
        ).thenReturn(Result.success(sampleUser))

        val result = registerWithEmailUseCase(
            fullName = "Test User",
            email = "test@example.com",
            pass = "123456",
            confirmPass = "123456",
            phone = "+905551112233",
            avatarUrl = "avatar.png",
            pin = "1234"
        )
        assertTrue(result.isSuccess)
        assertEquals(sampleUser, result.getOrNull())
    }

    // --- LoginWithPinUseCase Tests ---
    @Test
    fun `loginWithPin fails on invalid pin length`() = runTest {
        val result = loginWithPinUseCase("123")
        assertTrue(result.isFailure)
    }

    @Test
    fun `loginWithPin fails on non-digit pin`() = runTest {
        val result = loginWithPinUseCase("123a")
        assertTrue(result.isFailure)
    }

    @Test
    fun `loginWithPin verifies valid 4-digit pin with repository`() = runTest {
        whenever(authRepository.verifyPin("1234")).thenReturn(Result.success(true))

        val result = loginWithPinUseCase("1234")
        assertTrue(result.isSuccess)
        assertTrue(result.getOrDefault(false))
        verify(authRepository).verifyPin("1234")
    }

    // --- LoginWithGoogleUseCase Tests ---
    @Test
    fun `loginWithGoogle fails on blank token`() = runTest {
        val result = loginWithGoogleUseCase("")
        assertTrue(result.isFailure)
    }

    @Test
    fun `loginWithGoogle delegates token to repository`() = runTest {
        whenever(authRepository.signInWithGoogle("sample_id_token")).thenReturn(Result.success(sampleUser))

        val result = loginWithGoogleUseCase("sample_id_token")
        assertTrue(result.isSuccess)
        assertEquals(sampleUser, result.getOrNull())
    }

    // --- ResetPasswordUseCase Tests ---
    @Test
    fun `resetPassword fails on invalid email`() = runTest {
        val result = resetPasswordUseCase("not-an-email")
        assertTrue(result.isFailure)
    }

    @Test
    fun `resetPassword delegates to repository on valid email`() = runTest {
        whenever(authRepository.sendPasswordReset("test@example.com")).thenReturn(Result.success(Unit))

        val result = resetPasswordUseCase("test@example.com")
        assertTrue(result.isSuccess)
        verify(authRepository).sendPasswordReset("test@example.com")
    }

    // --- SignOutUseCase Tests ---
    @Test
    fun `signOut delegates to repository`() = runTest {
        whenever(authRepository.signOut()).thenReturn(Result.success(Unit))

        val result = signOutUseCase()
        assertTrue(result.isSuccess)
        verify(authRepository).signOut()
    }
}
