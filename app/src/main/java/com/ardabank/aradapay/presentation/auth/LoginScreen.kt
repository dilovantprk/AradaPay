package com.ardabank.aradapay.presentation.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ardabank.aradapay.presentation.components.bounceClick
import com.ardabank.aradapay.presentation.theme.PrimaryEmerald
import com.ardabank.aradapay.presentation.theme.PrimaryEmeraldContainer
import com.ardabank.aradapay.util.BiometricAuthHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    currentUserName: String = "Mehmet Dilovan",
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginSuccess: (String) -> Unit = {},
    onSwitchUser: (String) -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToWelcome: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pinAuthState by viewModel.pinAuthState.collectAsStateWithLifecycle()
    val resetPasswordState by viewModel.resetPasswordState.collectAsStateWithLifecycle()
    val storedUserName by viewModel.userNameFlow.collectAsStateWithLifecycle()

    val effectiveUserName = if (currentUserName.isNotBlank() && currentUserName != "Kullanıcı") {
        currentUserName
    } else {
        storedUserName
    }

    val isKnownUser = effectiveUserName.isNotBlank() && effectiveUserName != "Kullanıcı"
    var isQuickUnlockMode by remember { mutableStateOf(isKnownUser) }

    var userName by remember { mutableStateOf(effectiveUserName) }
    var pinCode by remember { mutableStateOf("") }
    val pinFocusRequester = remember { FocusRequester() }

    var identifierInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }

    var showResetPasswordDialog by remember { mutableStateOf(false) }
    var resetEmailInput by remember { mutableStateOf("") }

    val isBiometricAvailable = remember { BiometricAuthHelper.isBiometricAvailable(context) }

    // Listen to Auth Navigation Events
    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is AuthNavigationEvent.NavigateToDashboard -> {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, "Hoş geldiniz, ${event.userName}!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(event.userName)
                }
                is AuthNavigationEvent.NavigateToOnboarding -> {
                    onLoginSuccess(userName)
                }
                is AuthNavigationEvent.NavigateToWelcome -> {
                    onNavigateToWelcome()
                }
            }
        }
    }

    // Handle UI errors
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Error) {
            Toast.makeText(context, (uiState as AuthUiState.Error).message, Toast.LENGTH_LONG).show()
            viewModel.clearErrors()
        }
    }

    // Handle PIN Auth errors
    LaunchedEffect(pinAuthState) {
        if (pinAuthState is PinAuthState.Error) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            Toast.makeText(context, (pinAuthState as PinAuthState.Error).message, Toast.LENGTH_LONG).show()
            pinCode = ""
            viewModel.clearErrors()
        }
    }

    // Handle Reset Password State
    LaunchedEffect(resetPasswordState) {
        when (resetPasswordState) {
            is ResetPasswordUiState.Success -> {
                Toast.makeText(context, (resetPasswordState as ResetPasswordUiState.Success).message, Toast.LENGTH_LONG).show()
                showResetPasswordDialog = false
                viewModel.clearErrors()
            }
            is ResetPasswordUiState.Error -> {
                Toast.makeText(context, (resetPasswordState as ResetPasswordUiState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.clearErrors()
            }
            else -> {}
        }
    }

    val webClientId = "908604335031-0pd3pls2r9f2i4j671bd3q5v7b0ma2mv.apps.googleusercontent.com"
    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }
    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (!idToken.isNullOrBlank()) {
                    viewModel.loginWithGoogle(idToken)
                } else {
                    val googleName = account.displayName ?: account.givenName ?: "Google Kullanıcısı"
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    Toast.makeText(context, "Hoş geldiniz, $googleName", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(googleName)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Google girişi başarısız oldu: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(isQuickUnlockMode) {
        if (isQuickUnlockMode) {
            delay(200)
            pinFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    val initials = remember(userName) {
        val parts = userName.trim().split(" ").filter { it.isNotBlank() }
        when {
            parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
            parts.size == 1 && parts[0].length >= 2 -> parts[0].substring(0, 2).uppercase()
            parts.size == 1 -> parts[0].uppercase()
            else -> "MD"
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        AnimatedContent(
            targetState = isQuickUnlockMode,
            transitionSpec = {
                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
            },
            label = "LoginScreenModeAnimation"
        ) { quickUnlock ->
            if (quickUnlock) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            pinFocusRequester.requestFocus()
                            keyboardController?.show()
                        }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = {
                                isQuickUnlockMode = false
                                pinCode = ""
                                keyboardController?.hide()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Geri",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFF1F5F9),
                            modifier = Modifier.size(80.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = initials,
                                    color = Color(0xFF0F172A),
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Lütfen 4 haneli PIN kodunuzu girin",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        BasicTextField(
                            value = pinCode,
                            onValueChange = { input ->
                                if (input.length <= 4 && input.all { it.isDigit() }) {
                                    pinCode = input
                                    if (input.isNotEmpty()) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    if (input.length == 4) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        keyboardController?.hide()
                                        viewModel.verifyPin(input)
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.NumberPassword,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (pinCode.length == 4) {
                                        viewModel.verifyPin(pinCode)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .size(1.dp)
                                .focusRequester(pinFocusRequester)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            (0 until 4).forEach { index ->
                                val isFilled = index < pinCode.length
                                val isActive = index == pinCode.length
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isFilled) PrimaryEmeraldContainer.copy(alpha = 0.25f) else Color(0xFFF8FAFC),
                                    border = BorderStroke(
                                        width = if (isActive || isFilled) 2.dp else 1.5.dp,
                                        color = if (isActive || isFilled) PrimaryEmerald else Color(0xFFCBD5E1)
                                    ),
                                    modifier = Modifier
                                        .size(58.dp)
                                        .bounceClick {
                                            pinFocusRequester.requestFocus()
                                            keyboardController?.show()
                                        }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        if (pinAuthState is PinAuthState.Loading && isFilled) {
                                            CircularProgressIndicator(
                                                color = PrimaryEmerald,
                                                modifier = Modifier.size(16.dp),
                                                strokeWidth = 2.dp
                                            )
                                        } else if (isFilled) {
                                            Box(
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clip(CircleShape)
                                                    .background(PrimaryEmerald)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isBiometricAvailable) {
                            Spacer(modifier = Modifier.height(28.dp))
                            Surface(
                                shape = CircleShape,
                                color = PrimaryEmeraldContainer,
                                modifier = Modifier
                                    .size(54.dp)
                                    .bounceClick {
                                        val activity = context as? FragmentActivity
                                        if (activity != null) {
                                            BiometricAuthHelper.showBiometricPrompt(
                                                activity = activity,
                                                title = "AradaPay Güvenli Giriş",
                                                subtitle = "$userName olarak parmak izinizle giriş yapın",
                                                onSuccess = {
                                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                    Toast.makeText(context, "Biyometrik doğrulandı! Hoş geldiniz, $userName", Toast.LENGTH_SHORT).show()
                                                    viewModel.toggleDataLock(false)
                                                    onLoginSuccess(userName)
                                                },
                                                onError = { _ ->
                                                    pinFocusRequester.requestFocus()
                                                    keyboardController?.show()
                                                }
                                            )
                                        }
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Fingerprint,
                                        contentDescription = "Biyometrik Giriş",
                                        tint = PrimaryEmerald,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "PIN kodunuzu mu unuttunuz?",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { showResetPasswordDialog = true }
                                .padding(8.dp)
                        )

                        Text(
                            text = "Farklı bir hesapla giriş yap",
                            color = PrimaryEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    isQuickUnlockMode = false
                                    pinCode = ""
                                    keyboardController?.hide()
                                }
                                .padding(6.dp)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .imePadding()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            FilledTonalIconButton(
                                onClick = {
                                    if (isKnownUser) {
                                        isQuickUnlockMode = true
                                    } else {
                                        onNavigateToWelcome()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = Color(0xFFF1F5F9)
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Geri",
                                    tint = Color(0xFF0F172A),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = PrimaryEmeraldContainer,
                            modifier = Modifier.size(68.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "₺",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryEmerald
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Tekrar Hoş Geldiniz! 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "AradaPay hesabınıza giriş yaparak ortak harcamalarınızı yönetin.",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            OutlinedTextField(
                                value = identifierInput,
                                onValueChange = { identifierInput = it },
                                label = { Text("E-Posta veya @etiket", maxLines = 1) },
                                placeholder = { Text("ornek@mail.com veya @mehmet", maxLines = 1) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.AlternateEmail,
                                        contentDescription = null,
                                        tint = PrimaryEmerald
                                    )
                                },
                                singleLine = true,
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = passwordInput,
                                onValueChange = { passwordInput = it },
                                label = { Text("Şifre", maxLines = 1) },
                                placeholder = { Text("••••••••", maxLines = 1) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = PrimaryEmerald
                                    )
                                },
                                trailingIcon = {
                                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (isPasswordVisible) "Gizle" else "Göster",
                                            tint = Color(0xFF94A3B8)
                                        )
                                    }
                                },
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                maxLines = 1,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryEmerald,
                                    unfocusedBorderColor = Color(0xFFE2E8F0)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                                ) {
                                    Checkbox(
                                        checked = rememberMe,
                                        onCheckedChange = { rememberMe = it },
                                        colors = CheckboxDefaults.colors(checkedColor = PrimaryEmerald)
                                    )
                                    Text(
                                        text = "Beni Hatırla",
                                        fontSize = 13.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                TextButton(
                                    onClick = { showResetPasswordDialog = true }
                                ) {
                                    Text(
                                        text = "Şifremi Unuttum",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryEmerald
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (identifierInput.isBlank()) {
                                    Toast.makeText(context, "Lütfen e-posta adresinizi veya kullanıcı adınızı girin.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (passwordInput.isBlank()) {
                                    Toast.makeText(context, "Lütfen şifrenizi girin.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                viewModel.loginWithEmail(identifierInput, passwordInput)
                            },
                            enabled = uiState !is AuthUiState.Loading,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .bounceClick { }
                        ) {
                            if (uiState is AuthUiState.Loading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(22.dp),
                                    strokeWidth = 2.5.dp
                                )
                            } else {
                                Text(
                                    text = "Giriş Yap",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFE2E8F0),
                                thickness = 1.dp
                            )
                            Text(
                                text = "veya",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFE2E8F0),
                                thickness = 1.dp
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        OutlinedButton(
                            onClick = {
                                val signInIntent = googleSignInClient.signInIntent
                                googleSignInLauncher.launch(signInIntent)
                            },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .bounceClick { }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "G",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = Color(0xFF4285F4)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Google ile Devam Et",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Henüz bir hesabınız yok mu? ",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                        Text(
                            text = "Kayıt Ol",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryEmerald,
                            modifier = Modifier
                                .bounceClick {
                                    onNavigateToRegister()
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }

    if (showResetPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showResetPasswordDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Şifre & PIN Sıfırlama",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Kayıtlı e-posta adresinizi girin, şifre sıfırlama bağlantısını anında iletelim.",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = resetEmailInput,
                        onValueChange = { resetEmailInput = it },
                        label = { Text("E-posta Adresi") },
                        placeholder = { Text("isim@ornek.com") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmailInput.isNotBlank()) {
                            viewModel.sendPasswordReset(resetEmailInput)
                        } else {
                            Toast.makeText(context, "Lütfen geçerli bir e-posta girin.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = resetPasswordState !is ResetPasswordUiState.Loading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryEmerald)
                ) {
                    if (resetPasswordState is ResetPasswordUiState.Loading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Sıfırlama Bağlantısı Gönder", fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                FilledTonalButton(
                    onClick = { showResetPasswordDialog = false },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF64748B))
                ) {
                    Text("İptal", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
